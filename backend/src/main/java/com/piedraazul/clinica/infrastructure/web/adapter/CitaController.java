package com.piedraazul.clinica.infrastructure.web.adapter;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.piedraazul.clinica.application.port.in.GestionarCitasUseCase;
import com.piedraazul.clinica.infrastructure.web.dto.CitaCreadaResponse;
import com.piedraazul.clinica.infrastructure.web.dto.CitaResponse;
import com.piedraazul.clinica.infrastructure.web.dto.CrearCitaRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CitaController {

	private final GestionarCitasUseCase gestionarCitas;

	public CitaController(GestionarCitasUseCase gestionarCitas) {
		this.gestionarCitas = gestionarCitas;
	}

	@PostMapping("/admin/citas")
	public ResponseEntity<CitaCreadaResponse> crear(@Valid @RequestBody CrearCitaRequest request) {
		var cita = gestionarCitas.crear(new GestionarCitasUseCase.ComandoCrearCita(
				request.pacienteId(),
				request.medicoId(),
				request.fechaHora(),
				request.motivo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(new CitaCreadaResponse(
				cita.id(),
				cita.pacienteId(),
				cita.medicoId(),
				cita.fechaHora(),
				cita.motivo(),
				cita.estado()));
	}

	@GetMapping("/admin/citas")
	public List<CitaResponse> listarTodas() {
		return gestionarCitas.listarTodas().stream()
				.map(CitaController::toResponse)
				.toList();
	}

	@GetMapping("/paciente/citas")
	public List<CitaResponse> listarPropias(@AuthenticationPrincipal Jwt jwt) {
		return gestionarCitas.listarPropias(jwt.getSubject()).stream()
				.map(CitaController::toResponse)
				.toList();
	}

	private static CitaResponse toResponse(GestionarCitasUseCase.CitaListado cita) {
		return new CitaResponse(
				cita.id(),
				cita.pacienteId(),
				cita.pacienteNombre(),
				cita.pacienteDocumento(),
				cita.medicoId(),
				cita.medicoNombre(),
				cita.especialidad(),
				cita.fechaHora(),
				cita.motivo(),
				cita.estado());
	}
}
