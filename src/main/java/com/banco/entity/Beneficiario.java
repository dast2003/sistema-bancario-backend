package com.banco.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/** Cuenta de otra persona que un usuario guarda con un alias para transferirle facilmente. */
@Entity
@Table(name = "beneficiarios", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "cuenta_id"}))
public class Beneficiario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 40)
	private String alias;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "cuenta_id")
	private Cuenta cuenta;

	@Column(nullable = false)
	private LocalDateTime fechaCreacion;

	protected Beneficiario() {
	}

	public Beneficiario(String alias, Usuario usuario, Cuenta cuenta) {
		this.alias = alias;
		this.usuario = usuario;
		this.cuenta = cuenta;
		this.fechaCreacion = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public String getAlias() {
		return alias;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Cuenta getCuenta() {
		return cuenta;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}
}
