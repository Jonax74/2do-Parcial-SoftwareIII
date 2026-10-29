package com.piedraazul.clinica.infrastructure.web.dto;

import com.piedraazul.clinica.domain.model.Rol;

public record UsuarioRegistradoResponse(
		Long id,
		String nombre,
		String correo,
		Rol rol) {
}
