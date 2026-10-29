package com.piedraazul.clinica.domain.port.out;

import java.time.Instant;

import com.piedraazul.clinica.domain.model.Usuario;

public interface TokenIssuerPort {
	IssuedToken issueFor(Usuario usuario);

	record IssuedToken(String value, Instant expiresAt) {
	}
}
