package com.banco.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record BeneficiarioRequest(
		@Schema(example = "Arrendador")
		@NotBlank(message = "El alias es obligatorio")
		@Size(max = 40, message = "El alias tiene maximo 40 caracteres") String alias,
		@Schema(example = "0987654321")
		@NotBlank(message = "El numero de cuenta es obligatorio")
		@Pattern(regexp = "\\d{10}", message = "El numero de cuenta tiene 10 digitos") String numeroCuenta
) {
}
