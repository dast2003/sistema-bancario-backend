package com.banco.controller;

import com.banco.dto.BeneficiarioRequest;
import com.banco.dto.BeneficiarioResponse;
import com.banco.entity.Usuario;
import com.banco.service.BeneficiarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/beneficiarios")
@Tag(name = "Beneficiarios", description = "Cuentas de terceros inscritas para transferir sin digitar el numero")
public class BeneficiarioController {

	private final BeneficiarioService beneficiarioService;

	public BeneficiarioController(BeneficiarioService beneficiarioService) {
		this.beneficiarioService = beneficiarioService;
	}

	@GetMapping
	@Operation(summary = "Listar mis beneficiarios")
	public List<BeneficiarioResponse> listar(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario) {
		return beneficiarioService.listar(usuario.getId());
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Inscribir un beneficiario",
			description = "La cuenta debe existir y ser de otra persona. Maximo 20 beneficiarios")
	public BeneficiarioResponse inscribir(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario,
			@Valid @RequestBody BeneficiarioRequest request) {
		return beneficiarioService.inscribir(usuario.getId(), request);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@Operation(summary = "Eliminar un beneficiario")
	public void eliminar(@Parameter(hidden = true) @AuthenticationPrincipal Usuario usuario, @PathVariable Long id) {
		beneficiarioService.eliminar(usuario.getId(), id);
	}
}
