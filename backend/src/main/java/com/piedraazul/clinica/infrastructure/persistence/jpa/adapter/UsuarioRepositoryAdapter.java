package com.piedraazul.clinica.infrastructure.persistence.jpa.adapter;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.piedraazul.clinica.domain.model.Usuario;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;
import com.piedraazul.clinica.infrastructure.persistence.jpa.entity.UsuarioJpaEntity;
import com.piedraazul.clinica.infrastructure.persistence.jpa.repository.SpringDataUsuarioJpaRepository;

@Repository
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

	private final SpringDataUsuarioJpaRepository repository;

	public UsuarioRepositoryAdapter(SpringDataUsuarioJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public Optional<Usuario> findByCorreo(String correo) {
		return repository.findByCorreoIgnoreCase(correo).map(UsuarioJpaEntity::toDomain);
	}

	@Override
	public boolean existsByCorreo(String correo) {
		return repository.existsByCorreoIgnoreCase(correo);
	}

	@Override
	public Usuario save(Usuario usuario) {
		UsuarioJpaEntity entity = new UsuarioJpaEntity(
				usuario.nombre(), usuario.correo(), usuario.passwordHash(), usuario.rol());
		return repository.save(entity).toDomain();
	}
}
