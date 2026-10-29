package com.piedraazul.clinica.application.service;

import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
import com.piedraazul.clinica.domain.exception.CorreoYaRegistradoException;
import com.piedraazul.clinica.domain.exception.DocumentoYaRegistradoException;
import com.piedraazul.clinica.domain.model.Paciente;
import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.domain.model.Usuario;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PasswordHasherPort;
import com.piedraazul.clinica.domain.port.out.UnidadDeTrabajoPort;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

public class CrearUsuarioApplicationService implements CrearUsuarioUseCase {

	private final UsuarioRepositoryPort usuarioRepository;
	private final PacienteRepositoryPort pacienteRepository;
	private final PasswordHasherPort passwordHasher;
	private final UnidadDeTrabajoPort unidadDeTrabajo;

	public CrearUsuarioApplicationService(
			UsuarioRepositoryPort usuarioRepository,
			PacienteRepositoryPort pacienteRepository,
			PasswordHasherPort passwordHasher,
			UnidadDeTrabajoPort unidadDeTrabajo) {
		this.usuarioRepository = usuarioRepository;
		this.pacienteRepository = pacienteRepository;
		this.passwordHasher = passwordHasher;
		this.unidadDeTrabajo = unidadDeTrabajo;
	}

	@Override
	public RegistrarPacienteUseCase.UsuarioRegistrado crear(ComandoCrearUsuario comando) {
		return unidadDeTrabajo.ejecutar(() -> crearUsuario(comando));
	}

	private RegistrarPacienteUseCase.UsuarioRegistrado crearUsuario(ComandoCrearUsuario comando) {
		if (comando.rol() == null) {
			throw new IllegalArgumentException("El rol es obligatorio");
		}

		String correo = AutenticacionApplicationService.normalizeEmail(comando.correo());
		if (usuarioRepository.existsByCorreo(correo)) {
			throw new CorreoYaRegistradoException();
		}

		String documento = null;
		if (comando.rol() == Rol.PACIENTE) {
			if (comando.documento() == null || comando.documento().isBlank()) {
				throw new IllegalArgumentException("El documento es obligatorio para los pacientes");
			}
			documento = AutenticacionApplicationService.normalizeDocument(comando.documento());
			if (pacienteRepository.existsByDocumento(documento)) {
				throw new DocumentoYaRegistradoException();
			}
		}

		Usuario usuario = usuarioRepository.save(Usuario.nuevo(
				comando.nombre().trim(),
				correo,
				passwordHasher.hash(comando.password()),
				comando.rol()));

		if (comando.rol() == Rol.PACIENTE) {
			pacienteRepository.save(Paciente.nuevo(
					usuario.id(),
					documento,
					AutenticacionApplicationService.blankToNull(comando.telefono()),
					comando.fechaNacimiento()));
		}

		return new RegistrarPacienteUseCase.UsuarioRegistrado(
				usuario.id(), usuario.nombre(), usuario.correo(), usuario.rol());
	}
}
