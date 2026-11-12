package com.piedraazul.clinica.domain.exception;

public class HorarioCitaNoDisponibleException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HorarioCitaNoDisponibleException(String message) {
		super(message);
	}
}
