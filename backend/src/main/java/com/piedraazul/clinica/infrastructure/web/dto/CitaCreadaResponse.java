package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.Instant;

import com.piedraazul.clinica.domain.model.EstadoCita;

public record CitaCreadaResponse(
		Long id,
		Long pacienteId,
		Long medicoId,
		Instant fechaHora,
		String motivo,
		EstadoCita estado) {
}
