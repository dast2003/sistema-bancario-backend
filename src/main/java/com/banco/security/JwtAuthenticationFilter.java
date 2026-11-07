package com.banco.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Lee el header "Authorization: Bearer <token>", lo valida y deja al usuario autenticado
 * en el SecurityContext. Si el token falta o es invalido, la peticion sigue sin usuario
 * y Spring Security respondera 401 en las rutas protegidas.
 *
 * No es un @Component a proposito: se registra solo dentro de la cadena de seguridad.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final String PREFIJO = "Bearer ";

	private final JwtService jwtService;
	private final UserDetailsService userDetailsService;

	public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		String header = request.getHeader(HttpHeaders.AUTHORIZATION);
		if (header != null && header.startsWith(PREFIJO)
				&& SecurityContextHolder.getContext().getAuthentication() == null) {
			try {
				String email = jwtService.validarYObtenerEmail(header.substring(PREFIJO.length()));
				UserDetails usuario = userDetailsService.loadUserByUsername(email);
				var auth = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
				auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(auth);
			} catch (JwtException | IllegalArgumentException | UsernameNotFoundException e) {
				SecurityContextHolder.clearContext();
			}
		}
		chain.doFilter(request, response);
	}
}
