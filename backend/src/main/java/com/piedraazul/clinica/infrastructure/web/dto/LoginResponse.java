package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.Instant;

import com.piedraazul.clinica.domain.model.Rol;

public record LoginResponse(
		String token,
		String tipo,
		Instant expiraEn,
		UsuarioResponse usuario) {

	public record UsuarioResponse(Long id, String nombre, String correo, Rol rol) {
	}
}
