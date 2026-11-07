package com.banco.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ErrorResponse(int status, String error, String mensaje, Map<String, String> detalles,
		LocalDateTime timestamp) {

	public static ErrorResponse of(int status, String error, String mensaje) {
		return new ErrorResponse(status, error, mensaje, Map.of(), LocalDateTime.now());
	}
}
