package com.piedraazul.clinica.infrastructure.security.adapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import com.piedraazul.clinica.domain.model.Usuario;
import com.piedraazul.clinica.domain.port.out.TokenIssuerPort;

@Component
public class JwtTokenIssuerAdapter implements TokenIssuerPort {

	private static final long TOKEN_LIFETIME_MINUTES = 60;

	private final JwtEncoder jwtEncoder;

	public JwtTokenIssuerAdapter(JwtEncoder jwtEncoder) {
		this.jwtEncoder = jwtEncoder;
	}

	@Override
	public IssuedToken issueFor(Usuario usuario) {
		Instant issuedAt = Instant.now();
		Instant expiresAt = issuedAt.plus(TOKEN_LIFETIME_MINUTES, ChronoUnit.MINUTES);
		JwtClaimsSet claims = JwtClaimsSet.builder()
				.issuer("piedra-azul-api")
				.issuedAt(issuedAt)
				.expiresAt(expiresAt)
				.subject(usuario.correo())
				.claim("rol", usuario.rol().name())
				.claim("nombre", usuario.nombre())
				.build();
		JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = jwtEncoder.encode(JwtEncoderParameters.from(header, claims)).getTokenValue();
		return new IssuedToken(token, expiresAt);
	}
}
