package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.LocalDate;

public record PacienteResponse(
		Long id,
		String nombre,
		String documento,
		String correo,
		String telefono,
		LocalDate fechaNacimiento) {
}
