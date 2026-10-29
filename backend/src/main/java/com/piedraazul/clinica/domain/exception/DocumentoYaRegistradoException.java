package com.piedraazul.clinica.domain.exception;

public class DocumentoYaRegistradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DocumentoYaRegistradoException() {
		super("Ya existe una cuenta con ese documento");
	}
}
