package com.piedraazul.clinica.infrastructure.web.adapter;

import java.time.Instant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.piedraazul.clinica.domain.exception.CorreoYaRegistradoException;
import com.piedraazul.clinica.domain.exception.CredencialesInvalidasException;
import com.piedraazul.clinica.domain.exception.DocumentoYaRegistradoException;
import com.piedraazul.clinica.infrastructure.web.dto.ApiErrorResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

	@ExceptionHandler(CredencialesInvalidasException.class)
	public ResponseEntity<ApiErrorResponse> invalidCredentials(CredencialesInvalidasException exception) {
		return error(HttpStatus.UNAUTHORIZED, exception.getMessage());
	}

	@ExceptionHandler({ CorreoYaRegistradoException.class, DocumentoYaRegistradoException.class })
	public ResponseEntity<ApiErrorResponse> duplicateRegistration(RuntimeException exception) {
		return error(HttpStatus.CONFLICT, exception.getMessage());
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiErrorResponse> invalidRequest(IllegalArgumentException exception) {
		return error(HttpStatus.BAD_REQUEST, exception.getMessage());
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ApiErrorResponse> duplicateData(DataIntegrityViolationException exception) {
		return error(HttpStatus.CONFLICT, "El correo o documento ya está registrado");
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> validationFailed(MethodArgumentNotValidException exception) {
		String message = exception.getBindingResult().getFieldErrors().stream()
				.findFirst()
				.map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
				.orElse("Los datos enviados no son válidos");
		return error(HttpStatus.BAD_REQUEST, message);
	}

	private ResponseEntity<ApiErrorResponse> error(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(new ApiErrorResponse(
				Instant.now(), status.value(), status.getReasonPhrase(), message));
	}
}
