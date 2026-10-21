package com.ligasantafesina.ligabackend.repository;

import com.ligasantafesina.ligabackend.dto.MarcadorResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ResultadoPartidoRepository {
    private final JdbcTemplate jdbcTemplate;

    public ResultadoPartidoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public MarcadorResponse obtenerMarcador(Integer partidoId, Integer equipoLocalId, Integer equipoVisitanteId) {
        return jdbcTemplate.queryForObject("""
                SELECT COALESCE(SUM(CASE WHEN j.equipo_id = ? THEN 1 ELSE 0 END), 0) AS goles_local,
                       COALESCE(SUM(CASE WHEN j.equipo_id = ? THEN 1 ELSE 0 END), 0) AS goles_visitante
                FROM evento_partido e
                JOIN jugador j ON j.id = e.jugador_id
                WHERE e.partido_id = ? AND e.tipo = 'GOL'
                """,
                (rs, rowNum) -> new MarcadorResponse(rs.getInt("goles_local"), rs.getInt("goles_visitante")),
                equipoLocalId, equipoVisitanteId, partidoId);
    }
}
