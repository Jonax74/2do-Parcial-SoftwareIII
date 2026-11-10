package com.piedraazul.clinica.domain.port.out;

import java.util.List;

import com.piedraazul.clinica.domain.model.Medico;

public interface MedicoRepositoryPort {
	boolean existsByCorreo(String correo);

	boolean existsById(Long medicoId);

	Medico save(Medico medico);

	List<Medico> findAll();
}
