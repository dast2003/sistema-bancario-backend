package com.banco.dto;

public record AuthResponse(String token, String tipo, long expiraEnMs, UsuarioResponse usuario) {

	public static AuthResponse bearer(String token, long expiraEnMs, UsuarioResponse usuario) {
		return new AuthResponse(token, "Bearer", expiraEnMs, usuario);
	}
}
