package com.banco.controller;

import com.banco.dto.UsuarioResponse;
import com.banco.entity.Usuario;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Datos del usuario autenticado")
public class UsuarioController {

	@GetMapping("/me")
	@Operation(summary = "Mi perfil")
	public UsuarioResponse me(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario) {
		return UsuarioResponse.de(usuario);
	}
}
