package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.Instant;

import com.piedraazul.clinica.domain.model.EstadoCita;

public record CitaResponse(
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
