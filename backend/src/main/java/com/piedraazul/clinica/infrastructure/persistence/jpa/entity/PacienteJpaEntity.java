package com.piedraazul.clinica.infrastructure.persistence.jpa.entity;

import java.time.LocalDate;

import com.piedraazul.clinica.domain.model.Paciente;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "pacientes")
public class PacienteJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false, unique = true)
	private UsuarioJpaEntity usuario;

	@Column(nullable = false, unique = true, length = 30)
	private String documento;

	@Column(length = 30)
	private String telefono;

	@Column(name = "fecha_nacimiento")
	private LocalDate fechaNacimiento;

	protected PacienteJpaEntity() {
	}

	public PacienteJpaEntity(
			UsuarioJpaEntity usuario,
			String documento,
			String telefono,
			LocalDate fechaNacimiento) {
		this.usuario = usuario;
		this.documento = documento;
		this.telefono = telefono;
		this.fechaNacimiento = fechaNacimiento;
	}

	public Long getId() {
		return id;
	}

	public UsuarioJpaEntity getUsuario() {
		return usuario;
	}

	public String getDocumento() {
		return documento;
	}

	public String getTelefono() {
		return telefono;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public Paciente toDomain() {
		return new Paciente(id, usuario.getId(), documento, telefono, fechaNacimiento);
	}
}
