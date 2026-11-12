package com.piedraazul.clinica.application.service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.piedraazul.clinica.application.port.in.GestionarCitasUseCase;
import com.piedraazul.clinica.domain.exception.HorarioCitaNoDisponibleException;
import com.piedraazul.clinica.domain.exception.RecursoNoEncontradoException;
import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;
import com.piedraazul.clinica.domain.port.out.CitaRepositoryPort;
import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;

public class GestionarCitasApplicationService implements GestionarCitasUseCase {

	private static final Duration MINIMUM_APPOINTMENT_GAP = Duration.ofMinutes(30);

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
	@Transactional
	public CitaCreada crear(ComandoCrearCita comando) {
		if (!pacienteRepository.lockById(comando.pacienteId())) {
			throw new RecursoNoEncontradoException("el paciente indicado");
		}
		if (!medicoRepository.lockById(comando.medicoId())) {
			throw new RecursoNoEncontradoException("el médico indicado");
		}
		Instant appointmentTime = comando.fechaHora().truncatedTo(ChronoUnit.MICROS);
		if (!appointmentTime.isAfter(Instant.now())) {
			throw new IllegalArgumentException("La fecha de la cita debe ser futura");
		}

		Instant intervalStart = appointmentTime.minus(MINIMUM_APPOINTMENT_GAP);
		Instant intervalEnd = appointmentTime.plus(MINIMUM_APPOINTMENT_GAP);
		if (citaRepository.existsProgrammedForPatientBetween(
				comando.pacienteId(), intervalStart, intervalEnd)) {
			throw new HorarioCitaNoDisponibleException(
					"El paciente debe tener al menos 30 minutos entre citas");
		}
		if (citaRepository.existsProgrammedForDoctorBetween(
				comando.medicoId(), intervalStart, intervalEnd)) {
			throw new HorarioCitaNoDisponibleException(
					"El médico ya tiene una cita dentro del intervalo de 30 minutos");
		}

		Cita cita = citaRepository.save(Cita.nueva(
				comando.pacienteId(),
				comando.medicoId(),
				appointmentTime,
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
