package com.piedraazul.clinica.infrastructure.web.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase;
import com.piedraazul.clinica.application.port.in.GestionarCitasUseCase;
import com.piedraazul.clinica.application.port.in.GestionarMedicosUseCase;
import com.piedraazul.clinica.application.port.in.IniciarSesionUseCase;
import com.piedraazul.clinica.application.port.in.ListarPacientesUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.infrastructure.config.SecurityConfiguration;

@WebMvcTest(controllers = {
		AuthController.class,
		MedicoController.class,
		CitaController.class,
		UsuarioAdminController.class,
		PacienteController.class
})
@Import(SecurityConfiguration.class)
@ActiveProfiles("test")
class SecurityRoutesTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JwtEncoder jwtEncoder;

	@MockitoBean(name = "iniciarSesionUseCase")
	private IniciarSesionUseCase iniciarSesion;

	@MockitoBean(name = "registrarPacienteUseCase")
	private RegistrarPacienteUseCase registrarPaciente;

	@MockitoBean
	private CrearUsuarioUseCase crearUsuario;

	@MockitoBean
	private ListarPacientesUseCase listarPacientes;

	@MockitoBean
	private GestionarMedicosUseCase gestionarMedicos;

	@MockitoBean
	private GestionarCitasUseCase gestionarCitas;

	@Test
	void publicDoctorListDoesNotRequireToken() throws Exception {
		when(gestionarMedicos.listar()).thenReturn(List.of(
				new GestionarMedicosUseCase.MedicoListado(
						1L, "Dra. Ana Pérez", "Cardiología", "ana@example.com", "3001234567")));

		mockMvc.perform(get("/api/medicos/publico"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Dra. Ana Pérez"))
				.andExpect(jsonPath("$[0].especialidad").value("Cardiología"))
				.andExpect(jsonPath("$[0].id").value(1));
	}

	@Test
	void adminDoctorRoutesRequireAdministratorRole() throws Exception {
		mockMvc.perform(get("/api/admin/medicos"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/admin/medicos").header("Authorization", bearerToken(Rol.PACIENTE)))
				.andExpect(status().isForbidden());
	}

	@Test
	void administratorCanRegisterDoctor() throws Exception {
		when(gestionarMedicos.registrar(any())).thenReturn(
				new GestionarMedicosUseCase.MedicoRegistrado(
						3L, "Dra. Ana Pérez", "Cardiología", "ana@example.com", "3001234567"));

		mockMvc.perform(post("/api/admin/medicos")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombre":"Dra. Ana Pérez",
								  "especialidad":"Cardiología",
								  "correo":"ana@example.com",
								  "telefono":"3001234567"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(3))
				.andExpect(jsonPath("$.especialidad").value("Cardiología"));
	}

	@Test
	void administratorCanListDoctors() throws Exception {
		when(gestionarMedicos.listar()).thenReturn(List.of(
				new GestionarMedicosUseCase.MedicoListado(
						1L, "Dra. Ana Pérez", "Cardiología", "ana@example.com", "3001234567")));

		mockMvc.perform(get("/api/admin/medicos")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].correo").value("ana@example.com"));
	}

	@Test
	void administrationPatientListRejectsRequestsWithoutToken() throws Exception {
		mockMvc.perform(get("/api/admin/pacientes"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void administrationPatientListRejectsPatientRole() throws Exception {
		mockMvc.perform(get("/api/admin/pacientes").header("Authorization", bearerToken(Rol.PACIENTE)))
				.andExpect(status().isForbidden());
	}

	@Test
	void administrationPatientListAllowsAdministratorRole() throws Exception {
		when(listarPacientes.listarAdministrativos()).thenReturn(List.of(
				new ListarPacientesUseCase.PacienteListado(
						1L, "Ana Pérez", "123456", "ana@example.com", "3001234567", null)));

		mockMvc.perform(get("/api/admin/pacientes")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].correo").value("ana@example.com"));
	}

	@Test
	void patientRegistrationRequiresAdministratorRole() throws Exception {
		String request = """
				{
				  "nombre":"Ana Pérez",
				  "correo":"ana@example.com",
				  "password":"password123",
				  "documento":"123456",
				  "telefono":"3001234567",
				  "fechaNacimiento":"1990-01-02"
				}
				""";

		mockMvc.perform(post("/api/admin/pacientes")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/api/admin/pacientes")
						.header("Authorization", bearerToken(Rol.PACIENTE))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isForbidden());
	}

	@Test
	void administratorCanRegisterPatient() throws Exception {
		when(registrarPaciente.registrar(any())).thenReturn(
				new RegistrarPacienteUseCase.UsuarioRegistrado(
						7L, "Ana Pérez", "ana@example.com", Rol.PACIENTE));

		mockMvc.perform(post("/api/admin/pacientes")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombre":"Ana Pérez",
								  "correo":"ana@example.com",
								  "password":"password123",
								  "documento":"123456",
								  "telefono":"3001234567",
								  "fechaNacimiento":"1990-01-02"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(7))
				.andExpect(jsonPath("$.rol").value("PACIENTE"));
	}

	@Test
	void patientDocumentLookupRequiresAdministratorRole() throws Exception {
		mockMvc.perform(get("/api/admin/pacientes/existe").param("documento", "123456"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/admin/pacientes/existe")
						.param("documento", "123456")
						.header("Authorization", bearerToken(Rol.PACIENTE)))
				.andExpect(status().isForbidden());
		when(listarPacientes.existeDocumento("123456")).thenReturn(true);
		mockMvc.perform(get("/api/admin/pacientes/existe")
						.param("documento", "123456")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR)))
				.andExpect(status().isOk())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content()
						.string("true"));
	}

	@Test
	void adminAppointmentRoutesRequireAdministratorRole() throws Exception {
		mockMvc.perform(get("/api/admin/citas"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/admin/citas").header("Authorization", bearerToken(Rol.PACIENTE)))
				.andExpect(status().isForbidden());
	}

	@Test
	void patientCanOnlyRequestTheirOwnAppointments() throws Exception {
		when(gestionarCitas.listarPropias("test@example.com")).thenReturn(List.of(
				new GestionarCitasUseCase.CitaListado(
						1L, 7L, "Ana Pérez", "123456", 3L, "Dr. Juan Ruiz", "Pediatría",
						Instant.parse("2027-01-10T10:00:00Z"), "Control", com.piedraazul.clinica.domain.model.EstadoCita.PROGRAMADA)));

		mockMvc.perform(get("/api/paciente/citas"))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(get("/api/paciente/citas").header("Authorization", bearerToken(Rol.ADMINISTRADOR)))
				.andExpect(status().isForbidden());
		mockMvc.perform(get("/api/paciente/citas").header("Authorization", bearerToken(Rol.PACIENTE)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].pacienteDocumento").value("123456"))
				.andExpect(jsonPath("$[0].estado").value("PROGRAMADA"));
		verify(gestionarCitas).listarPropias("test@example.com");
	}

	@Test
	void administratorCanCreateAnAppointment() throws Exception {
		Instant appointmentTime = Instant.now().plusSeconds(3600);
		when(gestionarCitas.crear(any())).thenReturn(
				new GestionarCitasUseCase.CitaCreada(
						11L, 7L, 3L, appointmentTime, "Control", com.piedraazul.clinica.domain.model.EstadoCita.PROGRAMADA));
		String request = """
				{
				  "pacienteId":7,
				  "medicoId":3,
				  "fechaHora":"%s",
				  "motivo":"Control"
				}
				""".formatted(appointmentTime);

		mockMvc.perform(post("/api/admin/citas")
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isUnauthorized());
		mockMvc.perform(post("/api/admin/citas")
						.header("Authorization", bearerToken(Rol.PACIENTE))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isForbidden());
		mockMvc.perform(post("/api/admin/citas")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR))
						.contentType(MediaType.APPLICATION_JSON)
						.content(request))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(11))
				.andExpect(jsonPath("$.estado").value("PROGRAMADA"));
	}

	@Test
	void administratorCanListAllAppointments() throws Exception {
		when(gestionarCitas.listarTodas()).thenReturn(List.of());

		mockMvc.perform(get("/api/admin/citas")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray());
	}

	@Test
	void loginIsPublicAndReturnsJwt() throws Exception {
		when(iniciarSesion.ejecutar(any())).thenReturn(
				new IniciarSesionUseCase.SesionIniciada(
						"signed.jwt.token",
						Instant.parse("2026-10-29T22:00:00Z"),
						new IniciarSesionUseCase.UsuarioAutenticado(
								1L, "Admin", "admin@example.com", Rol.ADMINISTRADOR)));

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"correo":"admin@example.com","password":"correct-password"}
								"""))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").value("signed.jwt.token"))
				.andExpect(jsonPath("$.usuario.rol").value("ADMINISTRADOR"));
	}

	@Test
	void adminUserRegistrationRejectsRequestsWithoutToken() throws Exception {
		mockMvc.perform(post("/api/admin/usuarios")
						.contentType(MediaType.APPLICATION_JSON)
						.content(adminRegistrationBody()))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void adminUserRegistrationRejectsPatientRole() throws Exception {
		mockMvc.perform(post("/api/admin/usuarios")
						.header("Authorization", bearerToken(Rol.PACIENTE))
						.contentType(MediaType.APPLICATION_JSON)
						.content(adminRegistrationBody()))
				.andExpect(status().isForbidden());
	}

	@Test
	void administratorCanRegisterAnotherAdministrator() throws Exception {
		when(crearUsuario.crear(any())).thenReturn(
				new RegistrarPacienteUseCase.UsuarioRegistrado(
						8L, "Segundo Admin", "admin2@example.com", Rol.ADMINISTRADOR));

		mockMvc.perform(post("/api/admin/usuarios")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR))
						.contentType(MediaType.APPLICATION_JSON)
						.content(adminRegistrationBody()))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
	}

	@Test
	void adminRegistrationCanCreateAPatientAsWell() throws Exception {
		when(crearUsuario.crear(any())).thenReturn(
				new RegistrarPacienteUseCase.UsuarioRegistrado(
						9L, "Paciente", "patient@example.com", Rol.PACIENTE));

		mockMvc.perform(post("/api/admin/usuarios")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								  "nombre":"Paciente",
								  "correo":"patient@example.com",
								  "password":"password123",
								  "rol":"PACIENTE",
								  "documento":"987654"
								}
								"""))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("PACIENTE"));
	}

	@Test
	void patientRegistrationRejectsWeakPasswords() throws Exception {
		mockMvc.perform(post("/api/admin/pacientes")
						.header("Authorization", bearerToken(Rol.ADMINISTRADOR))
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"nombre":"Ana","correo":"ana@example.com","password":"short","documento":"123"}
								"""))
				.andExpect(status().isBadRequest());
	}

	private String adminRegistrationBody() {
		return """
				{
				  "nombre":"Segundo Admin",
				  "correo":"admin2@example.com",
				  "password":"password123",
				  "rol":"ADMINISTRADOR"
				}
				""";
	}

	private String bearerToken(Rol rol) {
		Instant issuedAt = Instant.now();
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("piedra-azul-api")
				.issuedAt(issuedAt)
				.expiresAt(issuedAt.plusSeconds(3600))
				.subject("test@example.com")
				.claim("rol", rol.name())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		return "Bearer " + jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
	}
}
