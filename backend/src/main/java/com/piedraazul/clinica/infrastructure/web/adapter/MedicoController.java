package com.piedraazul.clinica.infrastructure.web.adapter;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piedraazul.clinica.application.port.in.GestionarMedicosUseCase;
import com.piedraazul.clinica.infrastructure.web.dto.MedicoResponse;
import com.piedraazul.clinica.infrastructure.web.dto.RegistrarMedicoRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class MedicoController {

	private final GestionarMedicosUseCase gestionarMedicos;

	public MedicoController(GestionarMedicosUseCase gestionarMedicos) {
		this.gestionarMedicos = gestionarMedicos;
	}

	@GetMapping("/medicos/publico")
	public List<MedicoResponse> listarPublicos() {
		return gestionarMedicos.listar().stream()
				.map(medico -> new MedicoResponse(
						medico.id(),
						medico.nombre(),
						medico.especialidad(),
						medico.correo(),
						medico.telefono()))
				.toList();
	}

	@GetMapping("/admin/medicos")
	public List<MedicoResponse> listarAdministrativos() {
		return gestionarMedicos.listar().stream()
				.map(medico -> new MedicoResponse(
						medico.id(),
						medico.nombre(),
						medico.especialidad(),
						medico.correo(),
						medico.telefono()))
				.toList();
	}

	@PostMapping("/admin/medicos")
	public ResponseEntity<MedicoResponse> registrar(@Valid @RequestBody RegistrarMedicoRequest request) {
		var medico = gestionarMedicos.registrar(new GestionarMedicosUseCase.ComandoRegistrarMedico(
				request.nombre(),
				request.especialidad(),
				request.correo(),
				request.telefono()));
		return ResponseEntity.status(HttpStatus.CREATED).body(new MedicoResponse(
				medico.id(),
				medico.nombre(),
				medico.especialidad(),
				medico.correo(),
				medico.telefono()));
	}
}
