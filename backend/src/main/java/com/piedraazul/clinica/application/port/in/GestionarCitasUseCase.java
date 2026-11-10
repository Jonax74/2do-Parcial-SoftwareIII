package com.piedraazul.clinica.application.port.in;

import java.time.Instant;
import java.util.List;

import com.piedraazul.clinica.domain.model.EstadoCita;

public interface GestionarCitasUseCase {
	CitaCreada crear(ComandoCrearCita comando);

	List<CitaListado> listarTodas();

	List<CitaListado> listarPropias(String correoPaciente);

	record ComandoCrearCita(
			Long pacienteId,
			Long medicoId,
			Instant fechaHora,
			String motivo) {
	}

	record CitaCreada(
			Long id,
			Long pacienteId,
			Long medicoId,
			Instant fechaHora,
			String motivo,
			EstadoCita estado) {
	}

	record CitaListado(
			Long id,
			Long pacienteId,
			String pacienteNombre,
			String pacienteDocumento,
			Long medicoId,
			String medicoNombre,
			String especialidad,
			Instant fechaHora,
			String motivo,
			EstadoCita estado) {
	}
}
