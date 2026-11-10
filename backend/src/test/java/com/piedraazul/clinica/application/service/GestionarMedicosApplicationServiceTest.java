package com.piedraazul.clinica.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piedraazul.clinica.application.port.in.GestionarMedicosUseCase.ComandoRegistrarMedico;
import com.piedraazul.clinica.domain.exception.CorreoYaRegistradoException;
import com.piedraazul.clinica.domain.model.Medico;
import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;

@ExtendWith(MockitoExtension.class)
class GestionarMedicosApplicationServiceTest {

	@Mock
	private MedicoRepositoryPort medicoRepository;

	@Test
	void registrationNormalizesEmailAndTextFields() {
		when(medicoRepository.existsByCorreo("medico@example.com")).thenReturn(false);
		when(medicoRepository.save(any())).thenAnswer(invocation -> {
			Medico medico = invocation.getArgument(0);
			return new Medico(1L, medico.nombre(), medico.especialidad(), medico.correo(), medico.telefono());
		});

		var service = new GestionarMedicosApplicationService(medicoRepository);
		var result = service.registrar(new ComandoRegistrarMedico(
				" Dra. Ana Pérez ", " Cardiología ", "MEDICO@EXAMPLE.COM ", " 3001234567 "));

		assertEquals("medico@example.com", result.correo());
		assertEquals("Dra. Ana Pérez", result.nombre());
		assertEquals("Cardiología", result.especialidad());
		verify(medicoRepository).existsByCorreo("medico@example.com");
	}

	@Test
	void registrationRejectsAnEmailAlreadyAssignedToAnotherDoctor() {
		when(medicoRepository.existsByCorreo("medico@example.com")).thenReturn(true);

		var service = new GestionarMedicosApplicationService(medicoRepository);
		var exception = assertThrows(CorreoYaRegistradoException.class, () -> service.registrar(
				new ComandoRegistrarMedico("Médico", "Medicina general", "MEDICO@EXAMPLE.COM", null)));

		assertEquals("Ya existe un médico registrado con ese correo electrónico", exception.getMessage());
	}
}
