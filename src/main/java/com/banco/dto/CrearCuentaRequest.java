package com.banco.dto;

import com.banco.entity.TipoCuenta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record CrearCuentaRequest(
		@Schema(example = "CORRIENTE") @NotNull(message = "El tipo de cuenta es obligatorio") TipoCuenta tipo
) {
}
