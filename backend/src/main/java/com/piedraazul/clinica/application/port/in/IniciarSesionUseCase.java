package com.piedraazul.clinica.application.port.in;

import java.time.Instant;

import com.piedraazul.clinica.domain.model.Rol;

public interface IniciarSesionUseCase {
	SesionIniciada ejecutar(ComandoInicioSesion comando);

	record ComandoInicioSesion(String correo, String password) {
	}

	record SesionIniciada(
			String token,
			Instant expiraEn,
			UsuarioAutenticado usuario) {
	}

	record UsuarioAutenticado(Long id, String nombre, String correo, Rol rol) {
	}
}
