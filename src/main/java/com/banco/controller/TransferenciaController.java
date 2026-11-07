package com.banco.controller;

import com.banco.dto.TransaccionResponse;
import com.banco.dto.TransferenciaRequest;
import com.banco.entity.Usuario;
import com.banco.service.CuentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferencias")
@Tag(name = "Transferencias", description = "Envio de dinero entre cuentas del banco")
public class TransferenciaController {

	private final CuentaService cuentaService;

	public TransferenciaController(CuentaService cuentaService) {
		this.cuentaService = cuentaService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Transferir desde una cuenta propia a otra cuenta",
			description = "Devuelve el movimiento registrado en la cuenta de origen")
	public TransaccionResponse transferir(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
			@Valid @RequestBody TransferenciaRequest request) {
		return cuentaService.transferir(usuario.getId(), request);
	}
}
