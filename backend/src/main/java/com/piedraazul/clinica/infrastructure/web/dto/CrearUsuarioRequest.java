package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.LocalDate;

import com.piedraazul.clinica.domain.model.Rol;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
		@NotBlank @Size(max = 120) String nombre,
		@NotBlank @Email @Size(max = 150) String correo,
		@NotBlank @Size(min = 8, max = 72) String password,
		@NotNull Rol rol,
		@Size(max = 30) String documento,
		@Size(max = 30) String telefono,
		@Past LocalDate fechaNacimiento) {
}
