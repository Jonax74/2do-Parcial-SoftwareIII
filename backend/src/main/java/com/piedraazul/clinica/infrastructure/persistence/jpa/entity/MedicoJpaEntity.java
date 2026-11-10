package com.piedraazul.clinica.infrastructure.persistence.jpa.entity;

import com.piedraazul.clinica.domain.model.Medico;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "medicos")
public class MedicoJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 120)
	private String nombre;

	@Column(nullable = false, length = 100)
	private String especialidad;

	@Column(nullable = false, unique = true, length = 150)
	private String correo;

	@Column(length = 30)
	private String telefono;

	protected MedicoJpaEntity() {
	}

	public MedicoJpaEntity(String nombre, String especialidad, String correo, String telefono) {
		this.nombre = nombre;
		this.especialidad = especialidad;
		this.correo = correo;
		this.telefono = telefono;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getEspecialidad() {
		return especialidad;
	}

	public String getCorreo() {
		return correo;
	}

	public String getTelefono() {
		return telefono;
	}

	public Medico toDomain() {
		return new Medico(id, nombre, especialidad, correo, telefono);
	}
}
