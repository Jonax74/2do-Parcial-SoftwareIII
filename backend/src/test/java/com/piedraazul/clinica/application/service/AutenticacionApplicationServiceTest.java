package com.piedraazul.clinica.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piedraazul.clinica.application.port.in.IniciarSesionUseCase.ComandoInicioSesion;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase.ComandoRegistroPaciente;
import com.piedraazul.clinica.domain.exception.CorreoYaRegistradoException;
import com.piedraazul.clinica.domain.exception.CredencialesInvalidasException;
import com.piedraazul.clinica.domain.exception.DocumentoYaRegistradoException;
import com.piedraazul.clinica.domain.model.Paciente;
import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.domain.model.Usuario;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PasswordHasherPort;
import com.piedraazul.clinica.domain.port.out.TokenIssuerPort;
import com.piedraazul.clinica.domain.port.out.UnidadDeTrabajoPort;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

@ExtendWith(MockitoExtension.class)
class AutenticacionApplicationServiceTest {

	@Mock
	private UsuarioRepositoryPort usuarioRepository;

	@Mock
	private PacienteRepositoryPort pacienteRepository;

	@Mock
	private PasswordHasherPort passwordHasher;

	@Mock
	private TokenIssuerPort tokenIssuer;

	private AutenticacionApplicationService service;

	@BeforeEach
	void setUp() {
		service = new AutenticacionApplicationService(
				usuarioRepository,
				pacienteRepository,
				passwordHasher,
				tokenIssuer,
				new UnidadDeTrabajoPort() {
					@Override
					public <T> T ejecutar(Supplier<T> operation) {
						return operation.get();
					}
				});
	}

	@Test
	void publicRegistrationAlwaysCreatesPatientAndStoresOnlyPasswordHash() {
		when(usuarioRepository.existsByCorreo("ana@example.com")).thenReturn(false);
		when(pacienteRepository.existsByDocumento("123456")).thenReturn(false);
		when(passwordHasher.hash("password123")).thenReturn("bcrypt-hash");
		when(usuarioRepository.save(any())).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			return new Usuario(17L, user.nombre(), user.correo(), user.passwordHash(), user.rol());
		});
		when(pacienteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		var result = service.registrar(new ComandoRegistroPaciente(
				" Ana Pérez ", " ANA@EXAMPLE.COM ", "password123", " 123456 ", " 300123 ", LocalDate.of(1990, 1, 2)));

		assertEquals(Rol.PACIENTE, result.rol());
		assertEquals("ana@example.com", result.correo());

		ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
		verify(usuarioRepository).save(userCaptor.capture());
		assertEquals("bcrypt-hash", userCaptor.getValue().passwordHash());
		assertEquals(Rol.PACIENTE, userCaptor.getValue().rol());

		ArgumentCaptor<Paciente> patientCaptor = ArgumentCaptor.forClass(Paciente.class);
		verify(pacienteRepository).save(patientCaptor.capture());
		assertEquals(17L, patientCaptor.getValue().usuarioId());
		assertEquals("123456", patientCaptor.getValue().documento());
		assertEquals("300123", patientCaptor.getValue().telefono());
	}

	@Test
	void publicRegistrationRejectsDuplicateEmailBeforeSaving() {
		when(usuarioRepository.existsByCorreo("ana@example.com")).thenReturn(true);

		assertThrows(CorreoYaRegistradoException.class, () -> service.registrar(
				new ComandoRegistroPaciente("Ana", "ana@example.com", "password123", "123456", null, null)));

		verify(usuarioRepository, never()).save(any());
		verify(pacienteRepository, never()).save(any());
	}

	@Test
	void publicRegistrationRejectsDuplicateDocumentBeforeSaving() {
		when(usuarioRepository.existsByCorreo("ana@example.com")).thenReturn(false);
		when(pacienteRepository.existsByDocumento("123456")).thenReturn(true);

		assertThrows(DocumentoYaRegistradoException.class, () -> service.registrar(
				new ComandoRegistroPaciente("Ana", "ana@example.com", "password123", "123456", null, null)));

		verify(usuarioRepository, never()).save(any());
	}

	@Test
	void loginNormalizesEmailAndIssuesRoleAwareToken() {
		Usuario user = new Usuario(4L, "Admin", "admin@example.com", "stored-hash", Rol.ADMINISTRADOR);
		when(usuarioRepository.findByCorreo("admin@example.com")).thenReturn(Optional.of(user));
		when(passwordHasher.matches("password123", "stored-hash")).thenReturn(true);
		when(tokenIssuer.issueFor(user)).thenReturn(
				new TokenIssuerPort.IssuedToken("signed-token", Instant.parse("2026-10-29T22:00:00Z")));

		var result = service.ejecutar(new ComandoInicioSesion(" ADMIN@EXAMPLE.COM ", "password123"));

		assertEquals("signed-token", result.token());
		assertEquals(Rol.ADMINISTRADOR, result.usuario().rol());
		verify(tokenIssuer).issueFor(user);
	}

	@Test
	void loginRejectsInvalidCredentials() {
		when(usuarioRepository.findByCorreo("ana@example.com")).thenReturn(Optional.empty());

		assertThrows(CredencialesInvalidasException.class, () -> service.ejecutar(
				new ComandoInicioSesion("ana@example.com", "wrong-password")));

		verify(tokenIssuer, never()).issueFor(any());
	}
}
