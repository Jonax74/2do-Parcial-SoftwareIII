package com.piedraazul.clinica.domain.port.out;

import java.util.List;

import com.piedraazul.clinica.domain.model.Paciente;
import com.piedraazul.clinica.domain.model.PacienteConUsuario;

public interface PacienteRepositoryPort {
	boolean existsByDocumento(String documento);

	Paciente save(Paciente paciente);

	List<PacienteConUsuario> findAllWithUsuario();
}
