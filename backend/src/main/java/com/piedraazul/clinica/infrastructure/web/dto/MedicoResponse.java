package com.piedraazul.clinica.infrastructure.web.dto;

public record MedicoResponse(
		Long id,
		String nombre,
		String especialidad,
		String correo,
		String telefono) {
}
