package com.piedraazul.clinica.application.port.in;

import java.time.LocalDate;

import com.piedraazul.clinica.domain.model.Rol;

public interface RegistrarPacienteUseCase {
	UsuarioRegistrado registrar(ComandoRegistroPaciente comando);

	record ComandoRegistroPaciente(
			String nombre,
			String correo,
			String password,
			String documento,
			String telefono,
			LocalDate fechaNacimiento) {
	}

	record UsuarioRegistrado(Long id, String nombre, String correo, Rol rol) {
	}
}
