package com.piedraazul.clinica.domain.port.out;

import java.util.Optional;

import com.piedraazul.clinica.domain.model.Usuario;

public interface UsuarioRepositoryPort {
	Optional<Usuario> findByCorreo(String correo);

	boolean existsByCorreo(String correo);

	Usuario save(Usuario usuario);
}
