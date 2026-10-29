package com.piedraazul.clinica.infrastructure.persistence.jpa.entity;

import com.piedraazul.clinica.domain.model.Rol;
import com.piedraazul.clinica.domain.model.Usuario;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
public class UsuarioJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, unique = true, length = 150)
	private String correo;

	@Column(name = "password_hash", nullable = false)
	private String passwordHash;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Rol rol;

	protected UsuarioJpaEntity() {
	}

	public UsuarioJpaEntity(String nombre, String correo, String passwordHash, Rol rol) {
		this.nombre = nombre;
		this.correo = correo;
		this.passwordHash = passwordHash;
		this.rol = rol;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getCorreo() {
		return correo;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public Rol getRol() {
		return rol;
	}

	public Usuario toDomain() {
		return new Usuario(id, nombre, correo, passwordHash, rol);
	}
}
