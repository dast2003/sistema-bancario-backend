package com.banco.dto;

import com.banco.entity.Rol;
import com.banco.entity.Usuario;

public record UsuarioResponse(Long id, String nombre, String apellido, String documento, String email, Rol rol) {

	public static UsuarioResponse de(Usuario u) {
		return new UsuarioResponse(u.getId(), u.getNombre(), u.getApellido(), u.getDocumento(), u.getEmail(), u.getRol());
	}
}
