package com.banco.exception;

/** Dato duplicado (email o documento ya registrado). HTTP 409. */
public class ConflictoException extends RuntimeException {

	public ConflictoException(String mensaje) {
		super(mensaje);
	}
}
