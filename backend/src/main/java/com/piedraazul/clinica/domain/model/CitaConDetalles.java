package com.piedraazul.clinica.domain.model;

import java.time.Instant;

public record CitaConDetalles(
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
