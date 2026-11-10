package com.piedraazul.clinica.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegistrarMedicoRequest(
		@NotBlank @Size(max = 120) String nombre,
		@NotBlank @Size(max = 100) String especialidad,
		@NotBlank @Email @Size(max = 150) String correo,
		@Size(max = 30) String telefono) {
}
