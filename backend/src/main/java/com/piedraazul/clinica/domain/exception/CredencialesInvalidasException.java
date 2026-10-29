package com.piedraazul.clinica.domain.exception;

public class CredencialesInvalidasException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CredencialesInvalidasException() {
		super("Correo o contraseña incorrectos");
	}
}
