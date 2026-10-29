package com.piedraazul.clinica;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.piedraazul.clinica.domain.port.out.UsuarioRepositoryPort;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthFlowIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepositoryPort usuarioRepository;

	@Test
	void patientCanRegisterAndThenLoginWithThePersistedAccount() throws Exception {
		String email = "patient-" + UUID.randomUUID() + "@example.com";
		String document = UUID.randomUUID().toString().replace("-", "").substring(0, 20);
		String requestBody = """
				{
				  "nombre":"Paciente de Prueba",
				  "correo":"%s",
				  "password":"password123",
				  "documento":"%s",
				  "telefono":"3001234567",
				  "fechaNacimiento":"1990-01-02"
				}
				""".formatted(email, document);

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.rol").value("PACIENTE"))
				.andExpect(jsonPath("$.correo").value(email));

		var storedUser = usuarioRepository.findByCorreo(email).orElseThrow();
		assertNotEquals("password123", storedUser.passwordHash());

		mockMvc.perform(post("/api/auth/login")
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{"correo":"%s","password":"password123"}
								""".formatted(email)))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.usuario.rol").value("PACIENTE"));

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(requestBody))
				.andExpect(status().isConflict());
	}
}
