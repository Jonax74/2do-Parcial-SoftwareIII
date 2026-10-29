package com.piedraazul.clinica.infrastructure.persistence.jpa.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.piedraazul.clinica.domain.model.Paciente;
import com.piedraazul.clinica.domain.model.PacienteConUsuario;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;
import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.PacienteJpaEntity;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataPacienteJpaRepository;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataUsuarioJpaRepository;

@Repository
public class PacienteRepositoryAdapter implements PacienteRepositoryPort {

	private final SpringDataPacienteJpaRepository pacienteRepository;
	private final SpringDataUsuarioJpaRepository usuarioRepository;

	public PacienteRepositoryAdapter(
			SpringDataPacienteJpaRepository pacienteRepository,
			SpringDataUsuarioJpaRepository usuarioRepository) {
		this.pacienteRepository = pacienteRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public boolean existsByDocumento(String documento) {
		return pacienteRepository.existsByDocumento(documento);
	}

	@Override
	public Paciente save(Paciente paciente) {
		var usuario = usuarioRepository.getReferenceById(paciente.usuarioId());
		var entity = new PacienteJpaEntity(
				usuario,
				paciente.documento(),
				paciente.telefono(),
				paciente.fechaNacimiento());
		return pacienteRepository.save(entity).toDomain();
	}

	@Override
	public List<PacienteConUsuario> findAllWithUsuario() {
		return pacienteRepository.findAllByOrderByIdAsc().stream()
				.map(paciente -> new PacienteConUsuario(
						paciente.getId(),
						paciente.getUsuario().getNombre(),
						paciente.getDocumento(),
						paciente.getUsuario().getCorreo(),
						paciente.getTelefono(),
						paciente.getFechaNacimiento()))
				.toList();
	}
}
