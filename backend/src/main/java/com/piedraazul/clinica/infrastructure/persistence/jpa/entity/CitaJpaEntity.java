package com.piedraazul.clinica.infrastructure.persistence.jpa.entity;

import java.time.Instant;

import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;
import com.piedraazul.clinica.domain.model.EstadoCita;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "citas")
public class CitaJpaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "paciente_id", nullable = false)
	private PacienteJpaEntity paciente;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "medico_id", nullable = false)
	private MedicoJpaEntity medico;

	@Column(name = "fecha_hora", nullable = false)
	private Instant fechaHora;

	@Column(nullable = false, length = 500)
	private String motivo;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoCita estado;

	protected CitaJpaEntity() {
	}

	public CitaJpaEntity(
			PacienteJpaEntity paciente,
			MedicoJpaEntity medico,
			Instant fechaHora,
			String motivo,
			EstadoCita estado) {
		this.paciente = paciente;
		this.medico = medico;
		this.fechaHora = fechaHora;
		this.motivo = motivo;
		this.estado = estado;
	}

	public Long getId() {
		return id;
	}

	public PacienteJpaEntity getPaciente() {
		return paciente;
	}

	public MedicoJpaEntity getMedico() {
		return medico;
	}

	public Instant getFechaHora() {
		return fechaHora;
	}

	public String getMotivo() {
		return motivo;
	}

	public EstadoCita getEstado() {
		return estado;
	}

	public Cita toDomain() {
		return new Cita(id, paciente.getId(), medico.getId(), fechaHora, motivo, estado);
	}

	public CitaConDetalles toDomainWithDetails() {
		return new CitaConDetalles(
				id,
				paciente.getId(),
				paciente.getUsuario().getNombre(),
				paciente.getDocumento(),
				medico.getId(),
				medico.getNombre(),
				medico.getEspecialidad(),
				fechaHora,
				motivo,
				estado);
	}
}
