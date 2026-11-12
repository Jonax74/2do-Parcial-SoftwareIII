package com.piedraazul.clinica.domain.port.out;

import java.time.Instant;
import java.util.List;

import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;

public interface CitaRepositoryPort {
	Cita save(Cita cita);

	boolean existsProgrammedForPatientBetween(Long pacienteId, Instant start, Instant end);

	boolean existsProgrammedForDoctorBetween(Long medicoId, Instant start, Instant end);

	List<CitaConDetalles> findAllWithDetails();

	List<CitaConDetalles> findAllByPacienteCorreo(String correo);
}
