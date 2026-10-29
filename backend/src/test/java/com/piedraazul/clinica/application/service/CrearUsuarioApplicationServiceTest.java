package com.piedraazul.clinica.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase.ComandoCrearUsuario;
import com.piedraazul.clinica.domain.model.Paciente;
import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.domain.model.Usuario;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PasswordHasherPort;
import com.piedraazul.clinica.domain.port.out.UnidadDeTrabajoPort;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

@ExtendWith(MockitoExtension.class)
class CrearUsuarioApplicationServiceTest {

	@Mock
	private UsuarioRepositoryPort usuarioRepository;

	@Mock
	private PacienteRepositoryPort pacienteRepository;

	@Mock
	private PasswordHasherPort passwordHasher;

	private CrearUsuarioApplicationService service;

	@BeforeEach
	void setUp() {
		service = new CrearUsuarioApplicationService(
				usuarioRepository,
				pacienteRepository,
				passwordHasher,
				new UnidadDeTrabajoPort() {
					@Override
					public <T> T ejecutar(java.util.function.Supplier<T> operation) {
						return operation.get();
					}
				});
	}

	@Test
	void administratorCanCreateAnAdministratorWithoutPatientProfile() {
		when(usuarioRepository.existsByCorreo("admin2@example.com")).thenReturn(false);
		when(passwordHasher.hash("password123")).thenReturn("bcrypt-hash");
		when(usuarioRepository.save(any())).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			return new Usuario(2L, user.nombre(), user.correo(), user.passwordHash(), user.rol());
		});

		var result = service.crear(new ComandoCrearUsuario(
				"Admin Dos", "ADMIN2@EXAMPLE.COM", "password123", Rol.ADMINISTRADOR, null, null, null));

		assertEquals(Rol.ADMINISTRADOR, result.rol());
		assertEquals("admin2@example.com", result.correo());
		verify(pacienteRepository, never()).save(any(Paciente.class));
	}

	@Test
	void administratorCanCreateAPatientWithItsProfile() {
		when(usuarioRepository.existsByCorreo("patient@example.com")).thenReturn(false);
		when(pacienteRepository.existsByDocumento("654321")).thenReturn(false);
		when(passwordHasher.hash("password123")).thenReturn("bcrypt-hash");
		when(usuarioRepository.save(any())).thenAnswer(invocation -> {
			Usuario user = invocation.getArgument(0);
			return new Usuario(3L, user.nombre(), user.correo(), user.passwordHash(), user.rol());
		});
		when(pacienteRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

		var result = service.crear(new ComandoCrearUsuario(
				"Paciente", "patient@example.com", "password123", Rol.PACIENTE, "654321", null, null));

		assertEquals(Rol.PACIENTE, result.rol());
		verify(pacienteRepository).save(any(Paciente.class));
	}
}
