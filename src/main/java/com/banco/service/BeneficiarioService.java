package com.banco.service;

import com.banco.dto.BeneficiarioRequest;
import com.banco.dto.BeneficiarioResponse;
import com.banco.entity.Beneficiario;
import com.banco.entity.Cuenta;
import com.banco.exception.ConflictoException;
import com.banco.exception.RecursoNoEncontradoException;
import com.banco.exception.ReglaNegocioException;
import com.banco.repository.BeneficiarioRepository;
import com.banco.repository.CuentaRepository;
import com.banco.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BeneficiarioService {

	static final int MAX_BENEFICIARIOS_POR_USUARIO = 20;

	private final BeneficiarioRepository beneficiarioRepository;
	private final CuentaRepository cuentaRepository;
	private final UsuarioRepository usuarioRepository;

	public BeneficiarioService(BeneficiarioRepository beneficiarioRepository, CuentaRepository cuentaRepository,
			UsuarioRepository usuarioRepository) {
		this.beneficiarioRepository = beneficiarioRepository;
		this.cuentaRepository = cuentaRepository;
		this.usuarioRepository = usuarioRepository;
	}

	@Transactional(readOnly = true)
	public List<BeneficiarioResponse> listar(Long usuarioId) {
		return beneficiarioRepository.findByUsuarioIdOrderByAliasAsc(usuarioId).stream()
				.map(BeneficiarioResponse::de).toList();
	}

	@Transactional
	public BeneficiarioResponse inscribir(Long usuarioId, BeneficiarioRequest req) {
		if (beneficiarioRepository.countByUsuarioId(usuarioId) >= MAX_BENEFICIARIOS_POR_USUARIO) {
			throw new ReglaNegocioException("Solo puedes tener " + MAX_BENEFICIARIOS_POR_USUARIO + " beneficiarios");
		}
		Cuenta cuenta = cuentaRepository.findByNumero(req.numeroCuenta())
				.orElseThrow(() -> new RecursoNoEncontradoException("La cuenta " + req.numeroCuenta() + " no existe"));
		if (cuenta.getUsuario().getId().equals(usuarioId)) {
			throw new ReglaNegocioException("No puedes inscribir una cuenta propia como beneficiario");
		}
		if (beneficiarioRepository.existsByUsuarioIdAndCuentaId(usuarioId, cuenta.getId())) {
			throw new ConflictoException("La cuenta " + req.numeroCuenta() + " ya esta inscrita");
		}
		Beneficiario beneficiario = new Beneficiario(req.alias().trim(), usuarioRepository.getReferenceById(usuarioId), cuenta);
		return BeneficiarioResponse.de(beneficiarioRepository.save(beneficiario));
	}

	@Transactional
	public void eliminar(Long usuarioId, Long id) {
		Beneficiario beneficiario = beneficiarioRepository.findByIdAndUsuarioId(id, usuarioId)
				.orElseThrow(() -> new RecursoNoEncontradoException("Beneficiario no encontrado"));
		beneficiarioRepository.delete(beneficiario);
	}
}
