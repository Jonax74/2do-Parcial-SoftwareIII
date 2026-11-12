package com.piedraazul.clinica.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.piedraazul.clinica.application.port.in.GestionarCitasUseCase.ComandoCrearCita;
import com.piedraazul.clinica.domain.exception.HorarioCitaNoDisponibleException;
import com.piedraazul.clinica.domain.exception.RecursoNoEncontradoException;
import com.piedraazul.clinica.domain.model.Cita;
import com.piedraazul.clinica.domain.model.CitaConDetalles;
import com.piedraazul.clinica.domain.model.EstadoCita;
import com.piedraazul.clinica.domain.port.out.CitaRepositoryPort;
import com.piedraazul.clinica.domain.port.out.MedicoRepositoryPort;
import com.piedraazul.clinica.domain.port.out.PacienteRepositoryPort;

@ExtendWith(MockitoExtension.class)
class GestionarCitasApplicationServiceTest {

	@Mock
	private CitaRepositoryPort citaRepository;

	@Mock
	private PacienteRepositoryPort pacienteRepository;

	@Mock
	private MedicoRepositoryPort medicoRepository;

	@Test
	void createsAppointmentWhenPatientAndDoctorExist() {
		Instant appointmentTime = Instant.now().plusSeconds(3600);
		when(pacienteRepository.lockById(7L)).thenReturn(true);
		when(medicoRepository.lockById(3L)).thenReturn(true);
		when(citaRepository.save(any())).thenAnswer(invocation -> {
			Cita cita = invocation.getArgument(0);
			return new Cita(11L, cita.pacienteId(), cita.medicoId(), cita.fechaHora(), cita.motivo(), cita.estado());
		});

		var service = new GestionarCitasApplicationService(
				citaRepository, pacienteRepository, medicoRepository);
		var result = service.crear(new ComandoCrearCita(7L, 3L, appointmentTime, " Control "));

		assertEquals(11L, result.id());
		assertEquals("Control", result.motivo());
		assertEquals(EstadoCita.PROGRAMADA, result.estado());
		verify(citaRepository).save(any(Cita.class));
	}

	@Test
	void rejectsAppointmentForUnknownPatient() {
		when(pacienteRepository.lockById(7L)).thenReturn(false);

		var service = new GestionarCitasApplicationService(
				citaRepository, pacienteRepository, medicoRepository);
		assertThrows(RecursoNoEncontradoException.class, () -> service.crear(
				new ComandoCrearCita(7L, 3L, Instant.now().plusSeconds(3600), "Control")));
	}

	@Test
	void rejectsAppointmentsWithinThirtyMinutesForTheSamePatient() {
		Instant appointmentTime = Instant.now().plusSeconds(3600);
		when(pacienteRepository.lockById(7L)).thenReturn(true);
		when(medicoRepository.lockById(3L)).thenReturn(true);
		when(citaRepository.existsProgrammedForPatientBetween(
				any(), any(), any())).thenReturn(true);

		var service = new GestionarCitasApplicationService(
				citaRepository, pacienteRepository, medicoRepository);

		assertThrows(HorarioCitaNoDisponibleException.class, () -> service.crear(
				new ComandoCrearCita(7L, 3L, appointmentTime, "Control")));
		verify(citaRepository, never()).save(any());
	}

	@Test
	void rejectsAppointmentsWithinThirtyMinutesForTheSameDoctor() {
		Instant appointmentTime = Instant.now().plusSeconds(3600);
		when(pacienteRepository.lockById(7L)).thenReturn(true);
		when(medicoRepository.lockById(3L)).thenReturn(true);
		when(citaRepository.existsProgrammedForPatientBetween(
				any(), any(), any())).thenReturn(false);
		when(citaRepository.existsProgrammedForDoctorBetween(
				any(), any(), any())).thenReturn(true);

		var service = new GestionarCitasApplicationService(
				citaRepository, pacienteRepository, medicoRepository);

		assertThrows(HorarioCitaNoDisponibleException.class, () -> service.crear(
				new ComandoCrearCita(7L, 3L, appointmentTime, "Control")));
		verify(citaRepository, never()).save(any());
	}

	@Test
	void patientAppointmentQueryUsesAuthenticatedEmail() {
		when(citaRepository.findAllByPacienteCorreo("ana@example.com")).thenReturn(List.of(
				new CitaConDetalles(
						1L, 7L, "Ana Pérez", "123456", 3L, "Dr. Juan Ruiz", "Pediatría",
						Instant.parse("2027-01-10T10:00:00Z"), "Control", EstadoCita.PROGRAMADA)));

		var service = new GestionarCitasApplicationService(
				citaRepository, pacienteRepository, medicoRepository);
		var result = service.listarPropias("ana@example.com");

		assertEquals(1, result.size());
		assertEquals(7L, result.get(0).pacienteId());
		verify(citaRepository).findAllByPacienteCorreo("ana@example.com");
	}
}
