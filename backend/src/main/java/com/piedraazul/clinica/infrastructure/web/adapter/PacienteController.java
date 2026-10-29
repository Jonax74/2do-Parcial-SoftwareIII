package com.piedraazul.clinica.infrastructure.web.adapter;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piedraazul.clinica.application.port.in.ListarPacientesUseCase;
import com.piedraazul.clinica.infrastructure.web.dto.PacienteResponse;
import com.piedraazul.clinica.infrastructure.web.dto.PacientePublicoResponse;

@RestController
@RequestMapping("/api")
public class PacienteController {

	private final ListarPacientesUseCase listarPacientes;

	public PacienteController(ListarPacientesUseCase listarPacientes) {
		this.listarPacientes = listarPacientes;
	}

	@GetMapping("/pacientes/publico")
	public List<PacientePublicoResponse> listarPublicos() {
		return listarPacientes.listarPublicos().stream()
				.map(paciente -> new PacientePublicoResponse(paciente.nombre(), paciente.documento()))
				.toList();
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
}
