package com.banco.controller;

import com.banco.dto.AuthResponse;
import com.banco.dto.LoginRequest;
import com.banco.dto.RegistroRequest;
import com.banco.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticacion", description = "Registro e inicio de sesion (publico)")
@SecurityRequirements // sin candado en Swagger: no requiere token
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/registro")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Registrar persona", description = "Crea el usuario, le abre una cuenta de ahorros y devuelve un JWT")
	@ApiResponse(responseCode = "201", description = "Usuario registrado")
	@ApiResponse(responseCode = "400", description = "Datos invalidos")
	@ApiResponse(responseCode = "409", description = "Email o documento ya registrado")
	public AuthResponse registrar(@Valid @RequestBody RegistroRequest request) {
		return authService.registrar(request);
	}

	@PostMapping("/login")
	@Operation(summary = "Iniciar sesion", description = "Devuelve un JWT para usar en el header Authorization")
	@ApiResponse(responseCode = "200", description = "Login correcto")
	@ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
	public AuthResponse login(@Valid @RequestBody LoginRequest request) {
		return authService.login(request);
	}
}
