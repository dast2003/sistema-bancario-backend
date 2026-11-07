package com.banco.controller;

import com.banco.dto.*;
import com.banco.entity.Usuario;
import com.banco.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas", description = "Cuentas del usuario autenticado, depositos, retiros y movimientos")
public class CuentaController {

	private final CuentaService cuentaService;

	public CuentaController(CuentaService cuentaService) {
		this.cuentaService = cuentaService;
	}

	@GetMapping
	@Operation(summary = "Listar mis cuentas")
	public List<CuentaResponse> misCuentas(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario) {
		return cuentaService.misCuentas(usuario.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Abrir una nueva cuenta", description = "Maximo 5 cuentas por persona")
	public CuentaResponse crear(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
			@Valid @RequestBody CrearCuentaRequest request) {
		return cuentaService.crearCuenta(usuario.getId(), request.tipo());
	}

	@PostMapping("/{numero}/depositos")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Depositar dinero en una cuenta propia")
	public TransaccionResponse depositar(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
			@PathVariable String numero, @Valid @RequestBody OperacionRequest request) {
		return cuentaService.depositar(usuario.getId(), numero, request);
	}

	@PostMapping("/{numero}/retiros")
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Retirar dinero de una cuenta propia", description = "Responde 422 si el saldo no alcanza")
	public TransaccionResponse retirar(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
			@PathVariable String numero, @Valid @RequestBody OperacionRequest request) {
		return cuentaService.retirar(usuario.getId(), numero, request);
	}

	@GetMapping("/{numero}/movimientos")
	@Operation(summary = "Historial de movimientos de una cuenta propia")
	public List<TransaccionResponse> movimientos(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
			@PathVariable String numero) {
		return cuentaService.movimientos(usuario.getId(), numero);
	}
}
