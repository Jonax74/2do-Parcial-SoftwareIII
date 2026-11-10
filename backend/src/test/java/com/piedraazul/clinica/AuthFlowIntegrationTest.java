package com.piedraazul.clinica;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepositoryPort usuarioRepository;

	@Autowired
	private JwtEncoder jwtEncoder;

	@Autowired
	private PacienteRepositoryPort pacienteRepository;

	@Autowired
	private MedicoRepositoryPort medicoRepository;

	@Test
	void administratorCanRegisterPatientAndThenPatientCanLogin() throws Exception {
		String email = "patient-" + UUID.randomUUID() + "@example.com";
		String document = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		String requestBody = """
				{
				  "nombre":"Paciente de Prueba",
				  "correo":"%s",
				  "password":"password123",
				  "documento":"%s",
				  "telefono":"3001234567",
				  "fechaNacimiento":"1990-01-02"
				}
				""".formatted(email, document);

		mockMvc.perform(post("/api/admin/pacientes")
						.header("Authorization", administratorToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("PACIENTE"))
				.andExpect(jsonPath("$.correo").value(email));

		var storedUser = usuarioRepository.findByCorreo(email).orElseThrow();
		assertNotEquals("password123", storedUser.passwordHash());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"correo":"%s","password":"password123"}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.usuario.rol").value("PACIENTE"));

		mockMvc.perform(get("/api/admin/pacientes/existe")
						.header("Authorization", administratorToken())
						.param("documento", document))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
						.string("true"));

		mockMvc.perform(post("/api/admin/pacientes")
						.header("Authorization", administratorToken())
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isConflict());
	}

	@Test
	void administratorCreatesAppointmentAndPatientSeesOnlyTheirOwnList() throws Exception {
		String email = "appointment-patient-" + UUID.randomUUID() + "@example.com";
		String document = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		String adminToken = administratorToken();

		mockMvc.perform(post("/api/admin/pacientes")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(patientRequest(email, document)))
				.andExpect(status().isCreated());

		mockMvc.perform(get("/api/admin/pacientes")
						.header("Authorization", adminToken))
				.andExpect(status().isOk());
		long patientId = pacienteRepository.findAllWithUsuario().stream()
				.filter(patient -> email.equals(patient.correo()))
				.findFirst()
				.orElseThrow()
				.pacienteId();

		String doctorEmail = "doctor-" + UUID.randomUUID() + "@example.com";
		mockMvc.perform(post("/api/admin/medicos")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombre":"Dr. Juan Ruiz",
								  "especialidad":"Pediatría",
								  "correo":"%s",
								  "telefono":"3001234567"
								}
								""".formatted(doctorEmail)))
				.andExpect(status().isCreated());
		long doctorId = medicoRepository.findAll().stream()
				.filter(doctor -> doctorEmail.equals(doctor.correo()))
				.mapToLong(com.piedraazul.clinica.domain.model.Medico::id)
				.max()
				.orElseThrow();

		String appointmentTime = java.time.Instant.now().plusSeconds(3600).toString();
		mockMvc.perform(post("/api/admin/citas")
						.header("Authorization", adminToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "pacienteId":%d,
								  "medicoId":%d,
								  "fechaHora":"%s",
								  "motivo":"Control"
								}
								""".formatted(patientId, doctorId, appointmentTime)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.estado").value("PROGRAMADA"));

		var loginResult = mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"correo":"%s","password":"password123"}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andReturn();
		String patientToken = "Bearer " + extractToken(loginResult.getResponse().getContentAsString());

		mockMvc.perform(get("/api/paciente/citas")
						.header("Authorization", patientToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pacienteDocumento").value(document))
				.andExpect(jsonPath("$[0].medicoNombre").value("Dr. Juan Ruiz"));
	}

	private String patientRequest(String email, String document) {
		return """
				{
				  "nombre":"Paciente de Prueba",
				  "correo":"%s",
				  "password":"password123",
				  "documento":"%s",
				  "telefono":"3001234567",
				  "fechaNacimiento":"1990-01-02"
				}
				""".formatted(email, document);
	}

	private String extractToken(String responseBody) {
		Matcher matcher = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(responseBody);
		if (!matcher.find()) {
			throw new AssertionError("El login no devolvió un token");
		}
		return matcher.group(1);
	}

	private String administratorToken() {
		var issuedAt = java.time.Instant.now();
		var claims = JwtClaimsSet.builder()
				.issuer("piedra-azul-api")
				.issuedAt(issuedAt)
				.expiresAt(issuedAt.plusSeconds(3600))
				.subject("admin@example.com")
				.claim("rol", "ADMINISTRADOR")
				.build();
		var header = JwsHeader.with(MacAlgorithm.HS256).build();
		return "Bearer " + jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}
}
