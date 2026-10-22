package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.entity.EventoPartido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventoPartidoRepository extends JpaRepository<EventoPartido, Long> {
    List<EventoPartido> findByPartidoId(Long partidoId);
}