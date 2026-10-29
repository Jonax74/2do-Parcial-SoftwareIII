package com.piedraazul.clinica.infrastructure.web.dto;

import java.time.Instant;

public record ApiErrorResponse(
		Instant timestamp,
		int status,
		String error,
		String message) {
}
