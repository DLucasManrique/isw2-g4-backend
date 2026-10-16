package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.Equipo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EquipoRepository extends JpaRepository<Equipo, Integer> {
    boolean existsByTorneoIdAndNombre(Integer torneoId, String nombre);
    boolean existsByTorneoIdAndNombreAndIdNot(Integer torneoId, String nombre, Integer id);

    // Solo valida la referencia requerida por RF-03; no expone un catalogo de torneos.
    @Query(value = "SELECT COUNT(*) FROM torneo WHERE id = :id", nativeQuery = true)
    long contarTorneoPorId(@Param("id") Integer id);
}
