package com.banco.service;

import com.banco.dto.AuthResponse;
import com.banco.dto.LoginRequest;
import com.banco.dto.RegistroRequest;
import com.banco.dto.UsuarioResponse;
import com.banco.entity.TipoCuenta;
import com.banco.entity.Usuario;
import com.banco.exception.ConflictoException;
import com.banco.repository.UsuarioRepository;
import com.banco.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UsuarioRepository usuarioRepository;
	private final CuentaService cuentaService;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(UsuarioRepository usuarioRepository, CuentaService cuentaService,
			PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
		this.usuarioRepository = usuarioRepository;
		this.cuentaService = cuentaService;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	/** Registra a la persona, le abre una cuenta de ahorros y devuelve su token. */
	@Transactional
	public AuthResponse registrar(RegistroRequest req) {
		String email = req.email().trim().toLowerCase();
		if (usuarioRepository.existsByEmail(email)) {
			throw new ConflictoException("Ya existe un usuario con el email " + email);
		}
		if (usuarioRepository.existsByDocumento(req.documento())) {
			throw new ConflictoException("Ya existe un usuario con el documento " + req.documento());
		}
		Usuario usuario = usuarioRepository.save(new Usuario(req.nombre().trim(), req.apellido().trim(),
				req.documento(), email, passwordEncoder.encode(req.password())));
		cuentaService.abrirCuenta(usuario, TipoCuenta.AHORROS);
		return respuesta(usuario);
	}

	public AuthResponse login(LoginRequest req) {
		Authentication auth = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(req.email().trim().toLowerCase(), req.password()));
		return respuesta((Usuario) auth.getPrincipal());
	}

	private AuthResponse respuesta(Usuario usuario) {
		return AuthResponse.bearer(jwtService.generarToken(usuario), jwtService.getExpiracionMs(),
				UsuarioResponse.de(usuario));
	}
}
