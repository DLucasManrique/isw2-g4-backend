package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JugadorRepository extends JpaRepository<Jugador, Integer> {

    List<Jugador> findByActivoTrue();

    List<Jugador> findByEquipoIdAndActivoTrue(Integer equipoId);

    boolean existsByDni(String dni);

    boolean existsByDniAndIdNot(String dni, Integer id);
}