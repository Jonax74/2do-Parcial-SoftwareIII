package com.piedraazul.clinica.infrastructure.persistence.jpa.adapter;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.piedraazul.clinica.domain.model.Medico;
import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;
import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.MedicoJpaEntity;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataMedicoJpaRepository;

@Repository
public class MedicoRepositoryAdapter implements MedicoRepositoryPort {

	private final SpringDataMedicoJpaRepository medicoRepository;

	public MedicoRepositoryAdapter(SpringDataMedicoJpaRepository medicoRepository) {
		this.medicoRepository = medicoRepository;
	}

	@Override
	public boolean existsByCorreo(String correo) {
		return medicoRepository.existsByCorreo(correo);
	}

	@Override
	public boolean existsById(Long medicoId) {
		return medicoRepository.existsById(medicoId);
	}

	@Override
	public Medico save(Medico medico) {
		return medicoRepository.save(new MedicoJpaEntity(
				medico.nombre(),
				medico.especialidad(),
				medico.correo(),
				medico.telefono())).toDomain();
	}

	@Override
	public List<Medico> findAll() {
		return medicoRepository.findAllByOrderByIdAsc().stream()
				.map(MedicoJpaEntity::toDomain)
				.toList();
	}
}
