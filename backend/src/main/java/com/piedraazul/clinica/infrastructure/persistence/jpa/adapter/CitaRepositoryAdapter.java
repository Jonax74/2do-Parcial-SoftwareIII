package com.piedraazul.clinica.infrastructure.persistence.jpa.adapter;

import java.time.Instant;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;
import com.piedraazul.clinica.domain.model.EstadoCita;
import com.piedraazul.clinica.domain.port.out.CitaRepositoryPort;
import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.CitaJpaEntity;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataCitaJpaRepository;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataMedicoJpaRepository;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataPacienteJpaRepository;

@Repository
public class CitaRepositoryAdapter implements CitaRepositoryPort {

	private final SpringDataCitaJpaRepository citaRepository;
	private final SpringDataPacienteJpaRepository pacienteRepository;
	private final SpringDataMedicoJpaRepository medicoRepository;

	public CitaRepositoryAdapter(
			SpringDataCitaJpaRepository citaRepository,
			SpringDataPacienteJpaRepository pacienteRepository,
			SpringDataMedicoJpaRepository medicoRepository) {
		this.citaRepository = citaRepository;
		this.pacienteRepository = pacienteRepository;
		this.medicoRepository = medicoRepository;
	}

	@Override
	public Cita save(Cita cita) {
		var entity = new CitaJpaEntity(
				pacienteRepository.getReferenceById(cita.pacienteId()),
				medicoRepository.getReferenceById(cita.medicoId()),
				cita.fechaHora(),
				cita.motivo(),
				cita.estado());
		return citaRepository.save(entity).toDomain();
	}

	@Override
	public boolean existsProgrammedForPatientBetween(Long pacienteId, Instant start, Instant end) {
		return citaRepository.existsByPaciente_IdAndEstadoAndFechaHoraGreaterThanAndFechaHoraLessThan(
				pacienteId, EstadoCita.PROGRAMADA, start, end);
	}

	@Override
	public boolean existsProgrammedForDoctorBetween(Long medicoId, Instant start, Instant end) {
		return citaRepository.existsByMedico_IdAndEstadoAndFechaHoraGreaterThanAndFechaHoraLessThan(
				medicoId, EstadoCita.PROGRAMADA, start, end);
	}

	@Override
	public List<CitaConDetalles> findAllWithDetails() {
		return citaRepository.findAllByOrderByFechaHoraAsc().stream()
				.map(CitaJpaEntity::toDomainWithDetails)
				.toList();
	}

	@Override
	public List<CitaConDetalles> findAllByPacienteCorreo(String correo) {
		return citaRepository.findByPaciente_Usuario_CorreoOrderByFechaHoraAsc(correo).stream()
				.map(CitaJpaEntity::toDomainWithDetails)
				.toList();
	}
}
