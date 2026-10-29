package com.piedraazul.clinica.application.port.in;

import java.time.LocalDate;

import com.piedraazul.clinica.domain.model.Rol;

public interface CrearUsuarioUseCase {
	RegistrarPacienteUseCase.UsuarioRegistrado crear(ComandoCrearUsuario comando);

	record ComandoCrearUsuario(
			String nombre,
			String correo,
			String password,
			Rol rol,
			String documento,
			String telefono,
			LocalDate fechaNacimiento) {
	}
}
