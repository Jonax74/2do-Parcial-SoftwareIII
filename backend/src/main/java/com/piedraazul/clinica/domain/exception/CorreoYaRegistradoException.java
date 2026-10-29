package com.piedraazul.clinica.domain.exception;

public class CorreoYaRegistradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CorreoYaRegistradoException() {
		super("Ya existe una cuenta con ese correo electrónico");
	}
}
