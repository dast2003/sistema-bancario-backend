package com.banco.exception;

/** Operacion no permitida por las reglas del banco (saldo insuficiente, limites...). HTTP 422. */
public class ReglaNegocioException extends RuntimeException {

	public ReglaNegocioException(String mensaje) {
		super(mensaje);
	}
}
