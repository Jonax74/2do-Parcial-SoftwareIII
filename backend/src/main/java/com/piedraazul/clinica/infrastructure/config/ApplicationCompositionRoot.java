package com.piedraazul.clinica.infrastructure.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase;
import com.piedraazul.clinica.application.port.in.IniciarSesionUseCase;
import com.piedraazul.clinica.application.port.in.ListarPacientesUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
import com.piedraazul.clinica.application.service.AutenticacionApplicationService;
import com.piedraazul.clinica.application.service.CrearUsuarioApplicationService;
import com.piedraazul.clinica.application.service.ListarPacientesApplicationService;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PasswordHasherPort;
import com.piedraazul.clinica.domain.port.out.TokenIssuerPort;
import com.piedraazul.clinica.domain.port.out.UnidadDeTrabajoPort;
import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

@Configuration
public class ApplicationCompositionRoot {

	@Bean
	AutenticacionApplicationService autenticacionApplicationService(
			UsuarioRepositoryPort usuarioRepository,
			PacienteRepositoryPort pacienteRepository,
			PasswordHasherPort passwordHasher,
			TokenIssuerPort tokenIssuer,
			UnidadDeTrabajoPort unidadDeTrabajo) {
		return new AutenticacionApplicationService(
				usuarioRepository, pacienteRepository, passwordHasher, tokenIssuer, unidadDeTrabajo);
	}

	@Bean
	IniciarSesionUseCase iniciarSesionUseCase(
			@Qualifier("autenticacionApplicationService") AutenticacionApplicationService service) {
		return service;
	}

	@Bean
	RegistrarPacienteUseCase registrarPacienteUseCase(
			@Qualifier("autenticacionApplicationService") AutenticacionApplicationService service) {
		return service;
	}

	@Bean
	CrearUsuarioUseCase crearUsuarioUseCase(
			UsuarioRepositoryPort usuarioRepository,
			PacienteRepositoryPort pacienteRepository,
			PasswordHasherPort passwordHasher,
			UnidadDeTrabajoPort unidadDeTrabajo) {
		return new CrearUsuarioApplicationService(
				usuarioRepository, pacienteRepository, passwordHasher, unidadDeTrabajo);
	}

	@Bean
	ListarPacientesUseCase listarPacientesUseCase(PacienteRepositoryPort pacienteRepository) {
		return new ListarPacientesApplicationService(pacienteRepository);
	}
}
