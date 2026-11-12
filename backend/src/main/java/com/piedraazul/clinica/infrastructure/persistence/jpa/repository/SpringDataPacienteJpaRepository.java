package com.piedraazul.clinica.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.PacienteJpaEntity;

public interface SpringDataPacienteJpaRepository extends JpaRepository<PacienteJpaEntity, Long> {
	boolean existsByDocumento(String documento);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select paciente from PacienteJpaEntity paciente where paciente.id = :id")
	Optional<PacienteJpaEntity> findByIdForUpdate(@Param("id") Long id);

	@EntityGraph(attributePaths = "usuario")
	List<PacienteJpaEntity> findAllByOrderByIdAsc();
}
