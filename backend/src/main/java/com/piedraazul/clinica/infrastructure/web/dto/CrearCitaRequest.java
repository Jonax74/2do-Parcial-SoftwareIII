package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.Instant;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearCitaRequest(
		@NotNull Long pacienteId,
		@NotNull Long medicoId,
		@NotNull @Future Instant fechaHora,
		@NotBlank @Size(max = 500) String motivo) {
}
