package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.JugadorSuspendidoResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SuspendidosService {
    private final JdbcTemplate jdbcTemplate;

    public SuspendidosService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(readOnly = true)
    public List<JugadorSuspendidoResponse> obtenerJugadoresSuspendidos() {
        return jdbcTemplate.query("""
                SELECT j.id AS jugador_id, j.nombre, j.apellido, eq.nombre AS equipo_nombre,
                       p.id AS partido_id,
                       SUM(CASE WHEN e.tipo = 'ROJA' THEN 1 ELSE 0 END) AS rojas
                FROM evento_partido e
                JOIN partido p ON p.id = e.partido_id
                JOIN jugador j ON j.id = e.jugador_id
                JOIN equipo eq ON eq.id = j.equipo_id
                WHERE p.estado = 'FINALIZADO'
                GROUP BY p.id, j.id, j.nombre, j.apellido, eq.nombre
                HAVING SUM(CASE WHEN e.tipo = 'ROJA' THEN 1 ELSE 0 END) > 0
                    OR SUM(CASE WHEN e.tipo = 'AMARILLA' THEN 1 ELSE 0 END) >= 2
                ORDER BY p.id, j.apellido, j.nombre, j.id
                """, (rs, rowNum) -> new JugadorSuspendidoResponse(
                rs.getInt("jugador_id"), rs.getString("nombre"), rs.getString("apellido"),
                rs.getString("equipo_nombre"), rs.getInt("partido_id"),
                rs.getInt("rojas") > 0 ? "Tarjeta roja" : "Dos amarillas en el mismo partido"));
    }
}
