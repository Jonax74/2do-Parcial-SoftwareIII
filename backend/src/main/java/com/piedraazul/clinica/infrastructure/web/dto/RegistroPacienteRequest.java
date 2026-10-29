package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

public record RegistroPacienteRequest(
		@NotBlank @Size(max = 120) String nombre,
		@NotBlank @Email @Size(max = 150) String correo,
		@NotBlank @Size(min = 8, max = 72) String password,
		@NotBlank @Size(max = 30) String documento,
		@Size(max = 30) String telefono,
		@Past LocalDate fechaNacimiento) {
}
