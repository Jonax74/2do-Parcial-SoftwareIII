package com.piedraazul.clinica.domain.model;

import java.time.LocalDate;

public record Paciente(
		Long id,
		Long usuarioId,
		String documento,
		String telefono,
		LocalDate fechaNacimiento) {

	public Paciente {
		if (usuarioId == null) {
			throw new IllegalArgumentException("El paciente debe estar asociado a un usuario");
		}
		if (documento == null || documento.isBlank()) {
			throw new IllegalArgumentException("El documento es obligatorio");
		}
	}

	public static Paciente nuevo(
			Long usuarioId,
			String documento,
			String telefono,
			LocalDate fechaNacimiento) {
		return new Paciente(null, usuarioId, documento, telefono, fechaNacimiento);
	}
}
