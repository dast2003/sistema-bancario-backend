package com.banco.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

/**
 * Persona registrada en el banco. Implementa UserDetails para que Spring Security
 * la use directamente como usuario autenticado (el "username" es el email).
 */
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 80)
	private String nombre;

	@Column(nullable = false, length = 80)
	private String apellido;

	@Column(nullable = false, unique = true, length = 20)
	private String documento;

	@Column(nullable = false, unique = true, length = 120)
	private String email;

	@Column(nullable = false)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Rol rol = Rol.CLIENTE;

	@Column(nullable = false)
	private LocalDateTime fechaRegistro;

	protected Usuario() {
	}

	public Usuario(String nombre, String apellido, String documento, String email, String password) {
		this.nombre = nombre;
		this.apellido = apellido;
		this.documento = documento;
		this.email = email;
		this.password = password;
		this.fechaRegistro = LocalDateTime.now();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
	}

	@Override
	public String getUsername() {
		return email;
	}

	@Override
	public String getPassword() {
		return password;
	}

	public Long getId() {
		return id;
	}

	public String getNombre() {
		return nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public String getDocumento() {
		return documento;
	}

	public String getEmail() {
		return email;
	}

	public Rol getRol() {
		return rol;
	}

	public LocalDateTime getFechaRegistro() {
		return fechaRegistro;
	}
}
