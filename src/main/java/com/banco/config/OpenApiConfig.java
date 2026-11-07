package com.banco.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger UI: http://localhost:8080/swagger-ui.html
 * Usa el boton "Authorize" y pega el token que devuelve /api/auth/login (sin la palabra Bearer).
 */
@Configuration
public class OpenApiConfig {

	public static final String ESQUEMA_JWT = "bearerAuth";

	@Bean
	public OpenAPI bancoOpenApi() {
		return new OpenAPI()
				.info(new Info()
						.title("API Sistema Bancario")
						.version("1.0.0")
						.description("Registro y login de personas con JWT, cuentas, depositos, retiros y transferencias."))
				.addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT))
				.components(new Components().addSecuritySchemes(ESQUEMA_JWT, new SecurityScheme()
						.name(ESQUEMA_JWT)
						.type(SecurityScheme.Type.HTTP)
						.scheme("bearer")
						.bearerFormat("JWT")));
	}
}
