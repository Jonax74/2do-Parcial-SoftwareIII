package com.piedraazul.clinica.application.port.in;

import java.time.LocalDate;
import java.util.List;

public interface ListarPacientesUseCase {
	List<PacienteListado> listarPublicos();

	List<PacienteListado> listarAdministrativos();

	record PacienteListado(
			Long id,
			String nombre,
			String documento,
			String correo,
			String telefono,
			LocalDate fechaNacimiento) {
	}
}
