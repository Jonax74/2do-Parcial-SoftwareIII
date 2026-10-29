package com.piedraazul.clinica.domain.model;

import java.time.LocalDate;

public record PacienteConUsuario(
		Long pacienteId,
		String nombre,
		String documento,
		String correo,
		String telefono,
		LocalDate fechaNacimiento) {
}
