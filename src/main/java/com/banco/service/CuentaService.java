package com.banco.service;

import com.banco.dto.*;
import com.banco.entity.*;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.exception.ReglaNegocioException;
import com.banco.repository.CuentaRepository;
import com.banco.repository.TransaccionRepository;
import com.banco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;

@Service
public class CuentaService {

	static final int MAX_CUENTAS_POR_USUARIO = 5;

	private final CuentaRepository cuentaRepository;
	private final TransaccionRepository transaccionRepository;
	private final UsuarioRepository usuarioRepository;
	private final SecureRandom random = new SecureRandom();

	public CuentaService(CuentaRepository cuentaRepository, TransaccionRepository transaccionRepository,
			UsuarioRepository usuarioRepository) {
		this.cuentaRepository = cuentaRepository;
		this.transaccionRepository = transaccionRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public List<CuentaResponse> misCuentas(Long usuarioId) {
		return cuentaRepository.findByUsuarioIdOrderByIdAsc(usuarioId).stream().map(CuentaResponse::de).toList();
	}

	@Transactional
	public CuentaResponse crearCuenta(Long usuarioId, TipoCuenta tipo) {
		if (cuentaRepository.countByUsuarioId(usuarioId) >= MAX_CUENTAS_POR_USUARIO) {
			throw new ReglaNegocioException("Solo puedes tener " + MAX_CUENTAS_POR_USUARIO + " cuentas");
		}
		return CuentaResponse.de(abrirCuenta(usuarioRepository.getReferenceById(usuarioId), tipo));
	}

	@Transactional
	Cuenta abrirCuenta(Usuario usuario, TipoCuenta tipo) {
		return cuentaRepository.save(new Cuenta(generarNumeroUnico(), tipo, usuario));
	}

	@Transactional
	public TransaccionResponse depositar(Long usuarioId, String numero, OperacionRequest req) {
		Cuenta cuenta = cuentaPropia(usuarioId, numero);
		cuenta.acreditar(req.monto());
		return registrar(cuenta, TipoTransaccion.DEPOSITO, req.monto(), descripcion(req.descripcion(), "Deposito"));
	}

	@Transactional
	public TransaccionResponse retirar(Long usuarioId, String numero, OperacionRequest req) {
		Cuenta cuenta = cuentaPropia(usuarioId, numero);
		cuenta.debitar(req.monto());
		return registrar(cuenta, TipoTransaccion.RETIRO, req.monto(), descripcion(req.descripcion(), "Retiro"));
	}

	/** Mueve dinero de una cuenta propia a cualquier cuenta del banco, en una sola transaccion. */
	@Transactional
	public TransaccionResponse transferir(Long usuarioId, TransferenciaRequest req) {
		if (req.cuentaOrigen().equals(req.cuentaDestino())) {
			throw new ReglaNegocioException("La cuenta de origen y destino no pueden ser la misma");
		}
		Cuenta origen = cuentaPropia(usuarioId, req.cuentaOrigen());
		Cuenta destino = cuentaRepository.findByNumero(req.cuentaDestino())
				.orElseThrow(() -> new RecursoNoEncontradoException("La cuenta destino " + req.cuentaDestino() + " no existe"));

		origen.debitar(req.monto());
		destino.acreditar(req.monto());

		String nota = req.descripcion() == null || req.descripcion().isBlank() ? "" : " - " + req.descripcion().trim();
		registrar(destino, TipoTransaccion.TRANSFERENCIA_RECIBIDA, req.monto(), "Desde " + origen.getNumero() + nota);
		return registrar(origen, TipoTransaccion.TRANSFERENCIA_ENVIADA, req.monto(), "Hacia " + destino.getNumero() + nota);
	}

	@Transactional(readOnly = true)
	public List<TransaccionResponse> movimientos(Long usuarioId, String numero) {
		Cuenta cuenta = cuentaPropia(usuarioId, numero);
		return transaccionRepository.findByCuentaIdOrderByFechaDescIdDesc(cuenta.getId()).stream()
				.map(TransaccionResponse::de).toList();
	}

	/** Solo el titular puede operar su cuenta: si no es suya, se responde 404 (no se revela que existe). */
	private Cuenta cuentaPropia(Long usuarioId, String numero) {
		return cuentaRepository.findByNumeroAndUsuarioId(numero, usuarioId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No tienes una cuenta con numero " + numero));
	}

	private TransaccionResponse registrar(Cuenta cuenta, TipoTransaccion tipo, BigDecimal monto, String descripcion) {
		return TransaccionResponse.de(transaccionRepository.save(new Transaccion(cuenta, tipo, monto, descripcion)));
	}

	private static String descripcion(String texto, String porDefecto) {
		return texto == null || texto.isBlank() ? porDefecto : texto.trim();
	}

	private String generarNumeroUnico() {
		String numero;
		do {
			numero = String.format("%010d", random.nextLong(1_000_000_000L, 10_000_000_000L));
		} while (cuentaRepository.existsByNumero(numero));
		return numero;
	}
}
