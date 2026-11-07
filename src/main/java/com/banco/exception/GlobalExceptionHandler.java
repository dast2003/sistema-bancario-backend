package com.banco.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> validacion(MethodArgumentNotValidException ex) {
		Map<String, String> detalles = new LinkedHashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(e -> detalles.putIfAbsent(e.getField(), e.getDefaultMessage()));
		var body = new ErrorResponse(400, "Bad Request", "Datos invalidos", detalles, LocalDateTime.now());
		return ResponseEntity.badRequest().body(body);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> jsonInvalido(HttpMessageNotReadableException ex) {
		return respuesta(HttpStatus.BAD_REQUEST, "El cuerpo de la peticion no es valido");
	}

	@ExceptionHandler({BadCredentialsException.class, DisabledException.class})
	public ResponseEntity<ErrorResponse> credenciales(RuntimeException ex) {
		return respuesta(HttpStatus.UNAUTHORIZED, "Email o contrasena incorrectos");
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException ex) {
		return respuesta(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(ConflictoException.class)
	public ResponseEntity<ErrorResponse> conflicto(ConflictoException ex) {
		return respuesta(HttpStatus.CONFLICT, ex.getMessage());
	}

	@ExceptionHandler(ObjectOptimisticLockingFailureException.class)
	public ResponseEntity<ErrorResponse> concurrencia(ObjectOptimisticLockingFailureException ex) {
		return respuesta(HttpStatus.CONFLICT, "La cuenta fue modificada por otra operacion. Intenta de nuevo");
	}

	@ExceptionHandler(ReglaNegocioException.class)
	public ResponseEntity<ErrorResponse> reglaNegocio(ReglaNegocioException ex) {
		return respuesta(HttpStatus.UNPROCESSABLE_CONTENT, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> inesperado(Exception ex) {
		log.error("Error no controlado", ex);
		return respuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado");
	}

	private ResponseEntity<ErrorResponse> respuesta(HttpStatus status, String mensaje) {
		return ResponseEntity.status(status).body(ErrorResponse.of(status.value(), status.getReasonPhrase(), mensaje));
	}
}
