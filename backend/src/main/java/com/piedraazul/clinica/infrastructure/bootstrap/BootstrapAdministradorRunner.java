package com.piedraazul.clinica.infrastructure.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase;
import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase.ComandoCrearUsuario;
import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

@Component
public class BootstrapAdministradorRunner implements ApplicationRunner {

	private final CrearUsuarioUseCase crearUsuario;
	private final UsuarioRepositoryPort usuarioRepository;
	private final String nombre;
	private final String correo;
	private final String password;

	public BootstrapAdministradorRunner(
			CrearUsuarioUseCase crearUsuario,
			UsuarioRepositoryPort usuarioRepository,
			@Value("${clinic.bootstrap-admin.name:}") String nombre,
			@Value("${clinic.bootstrap-admin.email:}") String correo,
			@Value("${clinic.bootstrap-admin.password:}") String password) {
		this.crearUsuario = crearUsuario;
		this.usuarioRepository = usuarioRepository;
		this.nombre = nombre;
		this.correo = correo;
		this.password = password;
	}

	@Override
	public void run(ApplicationArguments args) {
		if (nombre.isBlank() && correo.isBlank() && password.isBlank()) {
			return;
		}
		if (nombre.isBlank() || correo.isBlank() || password.isBlank()) {
			throw new IllegalStateException(
					"Define BOOTSTRAP_ADMIN_NAME, BOOTSTRAP_ADMIN_EMAIL y BOOTSTRAP_ADMIN_PASSWORD juntos");
		}
		if (password.length() < 8) {
			throw new IllegalStateException("BOOTSTRAP_ADMIN_PASSWORD debe tener al menos 8 caracteres");
		}

		var existingUser = usuarioRepository.findByCorreo(correo.trim().toLowerCase(java.util.Locale.ROOT));
		if (existingUser.isPresent()) {
			if (existingUser.get().rol() != Rol.ADMINISTRADOR) {
				throw new IllegalStateException("El correo de bootstrap ya pertenece a un usuario no administrador");
			}
			return;
		}

		crearUsuario.crear(new ComandoCrearUsuario(
				nombre,
				correo,
				password,
				Rol.ADMINISTRADOR,
				null,
				null,
				null));
	}
}
