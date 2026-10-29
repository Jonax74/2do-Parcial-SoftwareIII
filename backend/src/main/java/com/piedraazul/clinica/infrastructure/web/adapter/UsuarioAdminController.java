package com.piedraazul.clinica.infrastructure.web.adapter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piedraazul.clinica.application.port.in.CrearUsuarioUseCase;
import com.piedraazul.clinica.infrastructure.web.dto.CrearUsuarioRequest;
import com.piedraazul.clinica.infrastructure.web.dto.UsuarioRegistradoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/admin/usuarios")
public class UsuarioAdminController {

	private final CrearUsuarioUseCase crearUsuario;

	public UsuarioAdminController(CrearUsuarioUseCase crearUsuario) {
		this.crearUsuario = crearUsuario;
	}

	@PostMapping
	public ResponseEntity<UsuarioRegistradoResponse> crear(@Valid @RequestBody CrearUsuarioRequest request) {
		var user = crearUsuario.crear(new CrearUsuarioUseCase.ComandoCrearUsuario(
				request.nombre(),
				request.correo(),
				request.password(),
				request.rol(),
				request.documento(),
				request.telefono(),
				request.fechaNacimiento()));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				new UsuarioRegistradoResponse(user.id(), user.nombre(), user.correo(), user.rol()));
	}
}
