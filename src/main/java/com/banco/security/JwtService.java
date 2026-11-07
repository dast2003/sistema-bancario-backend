package com.banco.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/** Genera y valida los tokens JWT (firmados con HMAC-SHA). */
@Service
public class JwtService {

	private final SecretKey clave;
	private final long expiracionMs;

	public JwtService(@Value("${app.jwt.secret}") String secretoBase64,
			@Value("${app.jwt.expiracion-ms}") long expiracionMs) {
		this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretoBase64));
		this.expiracionMs = expiracionMs;
	}

	public String generarToken(UserDetails usuario) {
		Date ahora = new Date();
		return Jwts.builder()
				.subject(usuario.getUsername())
				.claim("roles", usuario.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
				.issuedAt(ahora)
				.expiration(new Date(ahora.getTime() + expiracionMs))
				.signWith(clave)
				.compact();
	}

	/**
	 * Valida firma y expiracion y devuelve el email (subject).
	 * @throws JwtException si el token no es valido o expiro
	 */
	public String validarYObtenerEmail(String token) {
		Claims claims = Jwts.parser()
				.verifyWith(clave)
				.build()
				.parseSignedClaims(token)
				.getPayload();
		return claims.getSubject();
	}

	public long getExpiracionMs() {
		return expiracionMs;
	}
}
