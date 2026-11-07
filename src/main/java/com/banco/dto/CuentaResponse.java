package com.banco.dto;

import com.banco.entity.Cuenta;
import com.banco.entity.TipoCuenta;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CuentaResponse(Long id, String numero, TipoCuenta tipo, BigDecimal saldo, LocalDateTime fechaCreacion) {

	public static CuentaResponse de(Cuenta c) {
		return new CuentaResponse(c.getId(), c.getNumero(), c.getTipo(), c.getSaldo(), c.getFechaCreacion());
	}
}
