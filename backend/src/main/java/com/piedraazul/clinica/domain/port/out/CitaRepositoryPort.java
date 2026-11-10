package com.piedraazul.clinica.domain.port.out;

import java.util.List;

import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;

public interface CitaRepositoryPort {
	Cita save(Cita cita);

	List<CitaConDetalles> findAllWithDetails();

	List<CitaConDetalles> findAllByPacienteCorreo(String correo);
}
