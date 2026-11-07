package com.banco.repository;

import com.banco.entity.Beneficiario;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiarioRepository extends JpaRepository<Beneficiario, Long> {

	@EntityGraph(attributePaths = {"cuenta", "cuenta.usuario"})
	List<Beneficiario> findByUsuarioIdOrderByAliasAsc(Long usuarioId);

	Optional<Beneficiario> findByIdAndUsuarioId(Long id, Long usuarioId);

	boolean existsByUsuarioIdAndCuentaId(Long usuarioId, Long cuentaId);

	long countByUsuarioId(Long usuarioId);
}
