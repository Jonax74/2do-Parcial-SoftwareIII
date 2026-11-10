package com.piedraazul.clinica.infrastructure.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.CitaJpaEntity;

public interface SpringDataCitaJpaRepository extends JpaRepository<CitaJpaEntity, Long> {
	@EntityGraph(attributePaths = { "paciente.usuario", "medico" })
	List<CitaJpaEntity> findAllByOrderByFechaHoraAsc();

	@EntityGraph(attributePaths = { "paciente.usuario", "medico" })
	List<CitaJpaEntity> findByPaciente_Usuario_CorreoOrderByFechaHoraAsc(String correo);
}
