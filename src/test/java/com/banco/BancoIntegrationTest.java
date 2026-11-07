package com.banco;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BancoIntegrationTest {

	@Autowired
	private WebApplicationContext context;

	private MockMvc mvc;

	@BeforeEach
	void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
	}

	@Test
	void flujoCompleto_registroLoginDepositoRetiroYTransferencia() throws Exception {
		String tokenAna = registrar("Ana", "1000000001", "ana@test.com");
		String tokenLuis = registrar("Luis", "1000000002", "luis@test.com");

		// Login con el email en mayusculas tambien funciona
		postJson("/api/auth/login", null, """
				{"email":"ANA@test.com","password":"Clave1234"}""")
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.token").isNotEmpty());

		String cuentaAna = primeraCuenta(tokenAna);
		String cuentaLuis = primeraCuenta(tokenLuis);

		postJson("/api/cuentas/" + cuentaAna + "/depositos", tokenAna, """
				{"monto":100000}""")
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.saldoResultante").value(100000));

		postJson("/api/cuentas/" + cuentaAna + "/retiros", tokenAna, """
				{"monto":500000}""")
				.andExpect(status().isUnprocessableContent());

		postJson("/api/transferencias", tokenAna, """
				{"cuentaOrigen":"%s","cuentaDestino":"%s","monto":30000}""".formatted(cuentaAna, cuentaLuis))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.saldoResultante").value(70000));

		mvc.perform(get("/api/cuentas").header("Authorization", "Bearer " + tokenLuis))
				.andExpect(jsonPath("$[0].saldo").value(30000));

		// Luis no puede operar la cuenta de Ana
		postJson("/api/cuentas/" + cuentaAna + "/retiros", tokenLuis, """
				{"monto":1000}""")
				.andExpect(status().isNotFound());
	}

	@Test
	void rutasProtegidas_sinTokenOTokenInvalido_responden401() throws Exception {
		mvc.perform(get("/api/cuentas")).andExpect(status().isUnauthorized());
		mvc.perform(get("/api/cuentas").header("Authorization", "Bearer token.falso.123"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void login_conClaveIncorrecta_responde401() throws Exception {
		registrar("Eva", "1000000003", "eva@test.com");
		postJson("/api/auth/login", null, """
				{"email":"eva@test.com","password":"otraClave99"}""")
				.andExpect(status().isUnauthorized());
	}

	@Test
	void registro_duplicadoYDatosInvalidos() throws Exception {
		registrar("Leo", "1000000004", "leo@test.com");
		postJson("/api/auth/registro", null, registroJson("Leo", "1000000005", "leo@test.com"))
				.andExpect(status().isConflict());
		postJson("/api/auth/registro", null, """
				{"nombre":"","apellido":"X","documento":"12","email":"malo","password":"corta"}""")
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.detalles.email").exists())
				.andExpect(jsonPath("$.detalles.password").exists());
	}

	@Test
	void swaggerEsPublico() throws Exception {
		mvc.perform(get("/v3/api-docs")).andExpect(status().isOk());
	}

	private String registrar(String nombre, String documento, String email) throws Exception {
		String body = postJson("/api/auth/registro", null, registroJson(nombre, documento, email))
				.andExpect(status().isCreated())
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$.token");
	}

	private String primeraCuenta(String token) throws Exception {
		String body = mvc.perform(get("/api/cuentas").header("Authorization", "Bearer " + token))
				.andExpect(status().isOk())
				.andReturn().getResponse().getContentAsString();
		return JsonPath.read(body, "$[0].numero");
	}

	private static String registroJson(String nombre, String documento, String email) {
		return """
				{"nombre":"%s","apellido":"Prueba","documento":"%s","email":"%s","password":"Clave1234"}"""
				.formatted(nombre, documento, email);
	}

	private ResultActions postJson(String url, String token, String json) throws Exception {
		var req = post(url).contentType(MediaType.APPLICATION_JSON).content(json);
		if (token != null) {
			req.header("Authorization", "Bearer " + token);
		}
		return mvc.perform(req);
	}
}
