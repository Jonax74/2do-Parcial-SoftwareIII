package com.piedraazul.clinica.domain.model;

public record Usuario(
		Long id,
		String nombre,
		String correo,
		String passwordHash,
		Rol rol) {

	public Usuario {
		if (nombre == null || nombre.isBlank()) {
			throw new IllegalArgumentException("El nombre es obligatorio");
		}
		if (correo == null || correo.isBlank()) {
			throw new IllegalArgumentException("El correo es obligatorio");
		}
		if (passwordHash == null || passwordHash.isBlank()) {
			throw new IllegalArgumentException("La contraseña cifrada es obligatoria");
		}
		if (rol == null) {
			throw new IllegalArgumentException("El rol es obligatorio");
		}
	}

	public static Usuario nuevo(String nombre, String correo, String passwordHash, Rol rol) {
		return new Usuario(null, nombre, correo, passwordHash, rol);
	}
}
