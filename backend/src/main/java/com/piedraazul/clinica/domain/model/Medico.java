package com.piedraazul.clinica.domain.model;

public record Medico(
		Long id,
		String nombre,
		String especialidad,
		String correo,
		String telefono) {

	public Medico {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre del médico es obligatorio");
		}
		if (especialidad == null || especialidad.isBlank()) {
			throw new IllegalArgumentException("La especialidad del médico es obligatoria");
		}
		if (correo == null || correo.isBlank()) {
			throw new IllegalArgumentException("El correo del médico es obligatorio");
		}
	}

	public static Medico nuevo(String nombre, String especialidad, String correo, String telefono) {
		return new Medico(null, nombre, especialidad, correo, telefono);
	}
}
