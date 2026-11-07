package com.banco.dto;

import com.banco.entity.TipoTransaccion;
import com.banco.entity.Transaccion;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransaccionResponse(Long id, TipoTransaccion tipo, BigDecimal monto, BigDecimal saldoResultante,
		String descripcion, LocalDateTime fecha) {

	public static TransaccionResponse de(Transaccion t) {
		return new TransaccionResponse(t.getId(), t.getTipo(), t.getMonto(), t.getSaldoResultante(),
				t.getDescripcion(), t.getFecha());
	}
}
