package com.banco.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record TransferenciaRequest(
		@Schema(example = "1234567890")
		@NotBlank(message = "La cuenta de origen es obligatoria")
		@Pattern(regexp = "\\d{10}", message = "El numero de cuenta tiene 10 digitos") String cuentaOrigen,
		@Schema(example = "0987654321")
		@NotBlank(message = "La cuenta de destino es obligatoria")
		@Pattern(regexp = "\\d{10}", message = "El numero de cuenta tiene 10 digitos") String cuentaDestino,
		@Schema(example = "20000")
		@NotNull(message = "El monto es obligatorio")
		@DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
		@Digits(integer = 15, fraction = 2, message = "Monto con formato invalido") BigDecimal monto,
		@Schema(example = "Pago arriendo") @Size(max = 200) String descripcion
) {
}
