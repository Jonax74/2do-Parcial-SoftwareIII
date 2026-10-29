package com.piedraazul.clinica.application.service;

import java.util.List;

import com.piedraazul.clinica.application.port.in.ListarPacientesUseCase;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;

public class ListarPacientesApplicationService implements ListarPacientesUseCase {

	private final PacienteRepositoryPort pacienteRepository;

	public ListarPacientesApplicationService(PacienteRepositoryPort pacienteRepository) {
		this.pacienteRepository = pacienteRepository;
	}

	@Override
	public List<PacienteListado> listarPublicos() {
		return pacienteRepository.findAllWithUsuario().stream()
				.map(paciente -> new PacienteListado(
						null,
						paciente.nombre(),
						paciente.documento(),
						null,
						null,
						null))
				.toList();
	}

	@Override
	public List<PacienteListado> listarAdministrativos() {
		return pacienteRepository.findAllWithUsuario().stream()
				.map(paciente -> {
					return new PacienteListado(
							paciente.pacienteId(),
							paciente.nombre(),
							paciente.documento(),
							paciente.correo(),
							paciente.telefono(),
							paciente.fechaNacimiento());
				})
				.toList();
	}
}
