package com.piedraazul.clinica.application.port.in;

import java.util.List;

public interface GestionarMedicosUseCase {
	MedicoRegistrado registrar(ComandoRegistrarMedico comando);

	List<MedicoListado> listar();

	record ComandoRegistrarMedico(
			String nombre,
			String especialidad,
			String correo,
			String telefono) {
	}

	record MedicoRegistrado(
			Long id,
			String nombre,
			String especialidad,
			String correo,
			String telefono) {
	}

	record MedicoListado(
			Long id,
			String nombre,
			String especialidad,
			String correo,
			String telefono) {
	}
}
