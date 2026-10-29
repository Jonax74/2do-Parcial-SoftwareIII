package com.piedraazul.clinica.infrastructure.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.PacienteJpaEntity;

public interface SpringDataPacienteJpaRepository extends JpaRepository<PacienteJpaEntity, Long> {
	boolean existsByDocumento(String documento);

	@EntityGraph(attributePaths = "usuario")
	List<PacienteJpaEntity> findAllByOrderByIdAsc();
}
