package com.banco.repository;

import com.banco.entity.Cuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaRepository extends JpaRepository<Cuenta, Long> {

	List<Cuenta> findByUsuarioIdOrderByIdAsc(Long usuarioId);

	Optional<Cuenta> findByNumero(String numero);

	Optional<Cuenta> findByNumeroAndUsuarioId(String numero, Long usuarioId);

	boolean existsByNumero(String numero);

	long countByUsuarioId(Long usuarioId);
}
