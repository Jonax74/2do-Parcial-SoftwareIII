package com.piedraazul.clinica.infrastructure.persistence.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.MedicoJpaEntity;

public interface SpringDataMedicoJpaRepository extends JpaRepository<MedicoJpaEntity, Long> {
	boolean existsByCorreo(String correo);

	List<MedicoJpaEntity> findAllByOrderByIdAsc();
}
