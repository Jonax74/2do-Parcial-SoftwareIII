package com.piedraazul.clinica.infrastructure.persistence.jpa.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.MedicoJpaEntity;

public interface SpringDataMedicoJpaRepository extends JpaRepository<MedicoJpaEntity, Long> {
	boolean existsByCorreo(String correo);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select medico from MedicoJpaEntity medico where medico.id = :id")
	Optional<MedicoJpaEntity> findByIdForUpdate(@Param("id") Long id);

	List<MedicoJpaEntity> findAllByOrderByIdAsc();
}
