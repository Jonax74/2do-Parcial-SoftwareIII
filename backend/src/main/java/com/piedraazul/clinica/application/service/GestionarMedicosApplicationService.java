package com.piedraazul.clinica.application.service;

import java.util.List;
import java.util.Locale;

import com.piedraazul.clinica.application.port.in.GestionarMedicosUseCase;
import com.piedraazul.clinica.domain.exception.CorreoYaRegistradoException;
import com.piedraazul.clinica.domain.model.Medico;
import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;

public class GestionarMedicosApplicationService implements GestionarMedicosUseCase {

	private final MedicoRepositoryPort medicoRepository;

	public GestionarMedicosApplicationService(MedicoRepositoryPort medicoRepository) {
		this.medicoRepository = medicoRepository;
	}

	@Override
	public MedicoRegistrado registrar(ComandoRegistrarMedico comando) {
		String correo = comando.correo().trim().toLowerCase(Locale.ROOT);
		if (medicoRepository.existsByCorreo(correo)) {
			throw new CorreoYaRegistradoException("Ya existe un médico registrado con ese correo electrónico");
		}

		Medico medico = medicoRepository.save(Medico.nuevo(
				comando.nombre().trim(),
				comando.especialidad().trim(),
				correo,
				comando.telefono() == null ? null : comando.telefono().trim()));
		return new MedicoRegistrado(
				medico.id(),
				medico.nombre(),
				medico.especialidad(),
				medico.correo(),
				medico.telefono());
	}

	@Override
	public List<MedicoListado> listar() {
		return medicoRepository.findAll().stream()
				.map(medico -> new MedicoListado(
						medico.id(),
						medico.nombre(),
						medico.especialidad(),
						medico.correo(),
						medico.telefono()))
				.toList();
	}
}
