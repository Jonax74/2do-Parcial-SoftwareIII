package com.piedraazul.clinica.domain.exception;

public class RecursoNoEncontradoException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RecursoNoEncontradoException(String recurso) {
		super("No se encontró " + recurso);
	}
}
