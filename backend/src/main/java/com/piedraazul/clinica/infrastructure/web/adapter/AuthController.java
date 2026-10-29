package com.piedraazul.clinica.infrastructure.web.adapter;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piedraazul.clinica.application.port.in.IniciarSesionUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
import com.piedraazul.clinica.infrastructure.web.dto.LoginRequest;
import com.piedraazul.clinica.infrastructure.web.dto.LoginResponse;
import com.piedraazul.clinica.infrastructure.web.dto.RegistroPacienteRequest;
import com.piedraazul.clinica.infrastructure.web.dto.UsuarioRegistradoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final IniciarSesionUseCase iniciarSesion;
	private final RegistrarPacienteUseCase registrarPaciente;

	public AuthController(
			@Qualifier("iniciarSesionUseCase")
			IniciarSesionUseCase iniciarSesion,
			@Qualifier("registrarPacienteUseCase")
			RegistrarPacienteUseCase registrarPaciente) {
		this.iniciarSesion = iniciarSesion;
		this.registrarPaciente = registrarPaciente;
	}

	@PostMapping("/login")
	public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
		var session = iniciarSesion.ejecutar(
				new IniciarSesionUseCase.ComandoInicioSesion(request.correo(), request.password()));
		var user = session.usuario();
		return ResponseEntity.ok(new LoginResponse(
				session.token(),
				"Bearer",
				session.expiraEn(),
				new LoginResponse.UsuarioResponse(user.id(), user.nombre(), user.correo(), user.rol())));
	}

	@PostMapping("/register")
	public ResponseEntity<UsuarioRegistradoResponse> registerPatient(
			@Valid @RequestBody RegistroPacienteRequest request) {
		var user = registrarPaciente.registrar(new RegistrarPacienteUseCase.ComandoRegistroPaciente(
				request.nombre(),
				request.correo(),
				request.password(),
				request.documento(),
				request.telefono(),
				request.fechaNacimiento()));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				new UsuarioRegistradoResponse(user.id(), user.nombre(), user.correo(), user.rol()));
	}
}
