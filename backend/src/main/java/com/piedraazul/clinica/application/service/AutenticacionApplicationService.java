package com.piedraazul.clinica.application.service;

import com.piedraazul.clinica.application.port.in.IniciarSesionUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
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

public class AutenticacionApplicationService implements IniciarSesionUseCase, RegistrarPacienteUseCase {

	private final UsuarioRepositoryPort usuarioRepository;
	private final PacienteRepositoryPort pacienteRepository;
	private final PasswordHasherPort passwordHasher;
	private final TokenIssuerPort tokenIssuer;
	private final UnidadDeTrabajoPort unidadDeTrabajo;

	public AutenticacionApplicationService(
			UsuarioRepositoryPort usuarioRepository,
			PacienteRepositoryPort pacienteRepository,
			PasswordHasherPort passwordHasher,
			TokenIssuerPort tokenIssuer,
			UnidadDeTrabajoPort unidadDeTrabajo) {
		this.usuarioRepository = usuarioRepository;
		this.pacienteRepository = pacienteRepository;
		this.passwordHasher = passwordHasher;
		this.tokenIssuer = tokenIssuer;
		this.unidadDeTrabajo = unidadDeTrabajo;
	}

	@Override
	public SesionIniciada ejecutar(ComandoInicioSesion comando) {
		String correo = normalizeEmail(comando.correo());
		Usuario usuario = usuarioRepository.findByCorreo(correo)
				.filter(candidate -> passwordHasher.matches(comando.password(), candidate.passwordHash()))
				.orElseThrow(CredencialesInvalidasException::new);
		TokenIssuerPort.IssuedToken token = tokenIssuer.issueFor(usuario);

		return new SesionIniciada(
				token.value(),
				token.expiresAt(),
				new UsuarioAutenticado(usuario.id(), usuario.nombre(), usuario.correo(), usuario.rol()));
	}

	@Override
	public UsuarioRegistrado registrar(ComandoRegistroPaciente comando) {
		return unidadDeTrabajo.ejecutar(() -> registrarPaciente(comando));
	}

	private UsuarioRegistrado registrarPaciente(ComandoRegistroPaciente comando) {
		String correo = normalizeEmail(comando.correo());
		String documento = normalizeDocument(comando.documento());
		if (usuarioRepository.existsByCorreo(correo)) {
			throw new CorreoYaRegistradoException();
		}
		if (pacienteRepository.existsByDocumento(documento)) {
			throw new DocumentoYaRegistradoException();
		}

		Usuario usuario = usuarioRepository.save(Usuario.nuevo(
				comando.nombre().trim(),
				correo,
				passwordHasher.hash(comando.password()),
				Rol.PACIENTE));
		pacienteRepository.save(Paciente.nuevo(
				usuario.id(),
				documento,
				blankToNull(comando.telefono()),
				comando.fechaNacimiento()));

		return new UsuarioRegistrado(usuario.id(), usuario.nombre(), usuario.correo(), usuario.rol());
	}

	static String normalizeEmail(String email) {
		return email.trim().toLowerCase(java.util.Locale.ROOT);
	}

	static String normalizeDocument(String document) {
		return document.trim();
	}

	static String blankToNull(String value) {
		return value == null || value.isBlank() ? null : value.trim();
	}
}
