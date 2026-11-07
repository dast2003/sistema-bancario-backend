package com.banco.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
		@Schema(example = "daniela@correo.com") @NotBlank(message = "El email es obligatorio") @Email String email,
		@Schema(example = "Clave123*") @NotBlank(message = "La contrasena es obligatoria") String password
) {
}
