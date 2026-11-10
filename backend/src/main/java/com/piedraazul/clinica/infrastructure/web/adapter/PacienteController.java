package com.piedraazul.clinica.infrastructure.web.adapter;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piedraazul.clinica.application.port.in.ListarPacientesUseCase;
import com.piedraazul.clinica.application.port.in.RegistrarPacienteUseCase;
import com.piedraazul.clinica.infrastructure.web.dto.PacienteResponse;
import com.piedraazul.clinica.infrastructure.web.dto.RegistroPacienteRequest;
import com.piedraazul.clinica.infrastructure.web.dto.UsuarioRegistradoResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class PacienteController {

	private final ListarPacientesUseCase listarPacientes;
	private final RegistrarPacienteUseCase registrarPaciente;

	public PacienteController(
			ListarPacientesUseCase listarPacientes,
			@Qualifier("registrarPacienteUseCase") RegistrarPacienteUseCase registrarPaciente) {
		this.listarPacientes = listarPacientes;
		this.registrarPaciente = registrarPaciente;
	}

	@GetMapping("/admin/pacientes")
	public List<PacienteResponse> listarAdministrativos() {
		return listarPacientes.listarAdministrativos().stream()
				.map(paciente -> new PacienteResponse(
						paciente.id(),
						paciente.nombre(),
						paciente.documento(),
						paciente.correo(),
						paciente.telefono(),
						paciente.fechaNacimiento()))
				.toList();
	}

	@GetMapping("/admin/pacientes/existe")
	public boolean existeDocumento(@RequestParam String documento) {
		return listarPacientes.existeDocumento(documento);
	}

	@PostMapping("/admin/pacientes")
	public ResponseEntity<UsuarioRegistradoResponse> registrar(@Valid @RequestBody RegistroPacienteRequest request) {
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
