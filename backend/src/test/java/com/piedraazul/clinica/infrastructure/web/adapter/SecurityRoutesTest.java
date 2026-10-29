package com.piedraazul.clinica.infrastructure.web.adapter;

import static org.mockito.ArgumentMatchers.any;
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
import com.piedraazul.clinica.application.port.in.IniciarSesionUseCase;
import com.piedraazul.clinica.application.port.in.ListarPacientesUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.infrastructure.config.SecurityConfiguration;

@WebMvcTest(controllers = {
		AuthController.class,
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

	@Test
	void publicPatientListDoesNotExposePrivateFields() throws Exception {
		when(listarPacientes.listarPublicos()).thenReturn(List.of(
				new ListarPacientesUseCase.PacienteListado(
						null, "Ana Pérez", "123456", null, null, null)));

		mockMvc.perform(get("/api/pacientes/publico"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].nombre").value("Ana Pérez"))
				.andExpect(jsonPath("$[0].documento").value("123456"))
				.andExpect(jsonPath("$[0].correo").doesNotExist());
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
	void patientRegistrationIsPublicAndAlwaysAssignsPatientRole() throws Exception {
		when(registrarPaciente.registrar(any())).thenReturn(
				new RegistrarPacienteUseCase.UsuarioRegistrado(
						7L, "Ana Pérez", "ana@example.com", Rol.PACIENTE));

		mockMvc.perform(post("/api/auth/register")
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
	void registrationRejectsWeakPasswords() throws Exception {
		mockMvc.perform(post("/api/auth/register")
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
