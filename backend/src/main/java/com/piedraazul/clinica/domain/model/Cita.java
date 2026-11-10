package com.piedraazul.clinica.domain.model;

import java.time.Instant;

public record Cita(
		Long id,
		Long pacienteId,
		Long medicoId,
		Instant fechaHora,
		String motivo,
		EstadoCita estado) {

	public Cita {
		if (pacienteId == null) {
			throw new IllegalArgumentException("El paciente es obligatorio");
		}
		if (medicoId == null) {
			throw new IllegalArgumentException("El médico es obligatorio");
		}
		if (fechaHora == null) {
			throw new IllegalArgumentException("La fecha y hora de la cita son obligatorias");
		}
		if (motivo == null || motivo.isBlank()) {
			throw new IllegalArgumentException("El motivo de la cita es obligatorio");
		}
		if (estado == null) {
			throw new IllegalArgumentException("El estado de la cita es obligatorio");
		}
	}

	public static Cita nueva(Long pacienteId, Long medicoId, Instant fechaHora, String motivo) {
		return new Cita(null, pacienteId, medicoId, fechaHora, motivo, EstadoCita.PROGRAMADA);
	}
}
