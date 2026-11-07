package com.banco.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones")
public class Transaccion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 30)
	private TipoTransaccion tipo;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal monto;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal saldoResultante;

	@Column(length = 200)
	private String descripcion;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cuenta_id")
	private Cuenta cuenta;

	protected Transaccion() {
	}

	public Transaccion(Cuenta cuenta, TipoTransaccion tipo, BigDecimal monto, String descripcion) {
		this.cuenta = cuenta;
		this.tipo = tipo;
		this.monto = monto;
		this.saldoResultante = cuenta.getSaldo();
		this.descripcion = descripcion;
		this.fecha = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public TipoTransaccion getTipo() {
		return tipo;
	}

	public BigDecimal getMonto() {
		return monto;
	}

	public BigDecimal getSaldoResultante() {
		return saldoResultante;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public LocalDateTime getFecha() {
		return fecha;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}
}
