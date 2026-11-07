package com.banco.config;

import com.banco.security.JwtAuthenticationFilter;
import com.banco.security.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private static final String[] RUTAS_PUBLICAS = {
			"/api/auth/**",
			"/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**",
			"/h2-console/**",
			"/error"
	};

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtService jwtService,
			UserDetailsService userDetailsService) throws Exception {
		http
				// API REST sin sesiones ni cookies: CSRF no aplica.
				.csrf(csrf -> csrf.disable())
				.cors(cors -> {})
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(RUTAS_PUBLICAS).permitAll()
						.anyRequest().authenticated())
				// La consola H2 se muestra dentro de un iframe.
				.headers(h -> h.frameOptions(f -> f.sameOrigin()))
				.exceptionHandling(ex -> ex
						.authenticationEntryPoint((req, res, e) -> escribirError(res, 401, "Unauthorized",
								"Debes iniciar sesion o el token expiro"))
						.accessDeniedHandler((req, res, e) -> escribirError(res, 403, "Forbidden",
								"No tienes permisos para este recurso")))
				.addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService),
						UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource(@Value("${app.cors.allowed-origins}") List<String> origenes) {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(origenes);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type"));
		config.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/api/**", config);
		return source;
	}

	private static void escribirError(jakarta.servlet.http.HttpServletResponse res, int status, String error,
			String mensaje) throws java.io.IOException {
		res.setStatus(status);
		res.setContentType(MediaType.APPLICATION_JSON_VALUE);
		res.setCharacterEncoding("UTF-8");
		res.getWriter().write("{\"status\":%d,\"error\":\"%s\",\"mensaje\":\"%s\",\"detalles\":{},\"timestamp\":\"%s\"}"
				.formatted(status, error, mensaje, java.time.LocalDateTime.now()));
	}
}
