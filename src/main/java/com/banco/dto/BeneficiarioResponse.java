package com.banco.dto;

import com.banco.entity.Beneficiario;
import com.banco.entity.Cuenta;
import com.banco.entity.TipoCuenta;
import com.banco.entity.Usuario;

/** "titular" va parcial (ej. "Luis P."): confirma la cuenta sin exponer el nombre completo. */
public record BeneficiarioResponse(Long id, String alias, String numeroCuenta, TipoCuenta tipoCuenta, String titular) {

	public static BeneficiarioResponse de(Beneficiario b) {
		Cuenta cuenta = b.getCuenta();
		return new BeneficiarioResponse(b.getId(), b.getAlias(), cuenta.getNumero(), cuenta.getTipo(),
				titularParcial(cuenta.getUsuario()));
	}

	private static String titularParcial(Usuario u) {
		return u.getNombre().split("\\s+")[0] + " " + u.getApellido().charAt(0) + ".";
	}
}
