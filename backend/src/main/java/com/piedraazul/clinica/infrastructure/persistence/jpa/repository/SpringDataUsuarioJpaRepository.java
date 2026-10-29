package com.piedraazul.clinica.infrastructure.persistence.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;

public interface SpringDataUsuarioJpaRepository extends JpaRepository<UsuarioJpaEntity, Long> {
	Optional<UsuarioJpaEntity> findByCorreoIgnoreCase(String correo);

	boolean existsByCorreoIgnoreCase(String correo);
}
