package com.piedraazul.clinica.application.service;

import java.time.Instant;
import java.util.List;

import com.piedraazul.clinica.application.port.in.GestionarCitasUseCase;
import com.piedraazul.clinica.domain.exception.RecursoNoEncontradoException;
import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;
import com.piedraazul.clinica.domain.port.out.CitaRepositoryPort;
import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;

public class GestionarCitasApplicationService implements GestionarCitasUseCase {

	private final CitaRepositoryPort citaRepository;
	private final PacienteRepositoryPort pacienteRepository;
	private final MedicoRepositoryPort medicoRepository;

	public GestionarCitasApplicationService(
			CitaRepositoryPort citaRepository,
			PacienteRepositoryPort pacienteRepository,
			MedicoRepositoryPort medicoRepository) {
		this.citaRepository = citaRepository;
		this.pacienteRepository = pacienteRepository;
		this.medicoRepository = medicoRepository;
	}

	@Override
	public CitaCreada crear(ComandoCrearCita comando) {
		if (!pacienteRepository.existsById(comando.pacienteId())) {
			throw new RecursoNoEncontradoException("el paciente indicado");
		}
		if (!medicoRepository.existsById(comando.medicoId())) {
			throw new RecursoNoEncontradoException("el médico indicado");
		}
		if (!comando.fechaHora().isAfter(Instant.now())) {
			throw new IllegalArgumentException("La fecha de la cita debe ser futura");
		}

		Cita cita = citaRepository.save(Cita.nueva(
				comando.pacienteId(),
				comando.medicoId(),
				comando.fechaHora(),
				comando.motivo().trim()));
		return new CitaCreada(
				cita.id(),
				cita.pacienteId(),
				cita.medicoId(),
				cita.fechaHora(),
				cita.motivo(),
				cita.estado());
	}

	@Override
	public List<CitaListado> listarTodas() {
		return citaRepository.findAllWithDetails().stream()
				.map(GestionarCitasApplicationService::toListado)
				.toList();
	}

	@Override
	public List<CitaListado> listarPropias(String correoPaciente) {
		return citaRepository.findAllByPacienteCorreo(correoPaciente).stream()
				.map(GestionarCitasApplicationService::toListado)
				.toList();
	}

	private static CitaListado toListado(CitaConDetalles cita) {
		return new CitaListado(
				cita.id(),
				cita.pacienteId(),
				cita.pacienteNombre(),
				cita.pacienteDocumento(),
				cita.medicoId(),
				cita.medicoNombre(),
				cita.especialidad(),
				cita.fechaHora(),
				cita.motivo(),
				cita.estado());
	}
}
