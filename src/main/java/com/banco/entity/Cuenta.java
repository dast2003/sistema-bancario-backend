package com.banco.entity;

import com.banco.exception.ReglaNegocioException;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cuentas")
public class Cuenta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 10)
	private String numero;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private TipoCuenta tipo;

	@Column(nullable = false, precision = 19, scale = 2)
	private BigDecimal saldo = BigDecimal.ZERO;

	@Column(nullable = false)
	private LocalDateTime fechaCreacion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	/** Bloqueo optimista: evita que dos operaciones simultaneas pisen el saldo. */
	@Version
	private Long version;

	protected Cuenta() {
	}

	public Cuenta(String numero, TipoCuenta tipo, Usuario usuario) {
		this.numero = numero;
		this.tipo = tipo;
		this.usuario = usuario;
		this.fechaCreacion = LocalDateTime.now();
	}

	public void acreditar(BigDecimal monto) {
		this.saldo = this.saldo.add(monto);
	}

	public void debitar(BigDecimal monto) {
		if (saldo.compareTo(monto) < 0) {
			throw new ReglaNegocioException("Saldo insuficiente en la cuenta " + numero);
		}
		this.saldo = this.saldo.subtract(monto);
	}

	public Long getId() {
		return id;
	}

	public String getNumero() {
		return numero;
	}

	public TipoCuenta getTipo() {
		return tipo;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

	public Usuario getUsuario() {
		return usuario;
	}
}
