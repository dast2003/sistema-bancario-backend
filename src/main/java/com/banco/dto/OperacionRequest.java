package com.banco.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Deposito o retiro sobre una cuenta propia. */
public record OperacionRequest(
		@Schema(example = "50000")
		@NotNull(message = "El monto es obligatorio")
		@DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
		@Digits(integer = 15, fraction = 2, message = "Monto con formato invalido") BigDecimal monto,
		@Schema(example = "Consignacion en efectivo") @Size(max = 200) String descripcion
) {
}
