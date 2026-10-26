
package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.PosicionResponse;
import com.ligasantafesina.ligabackend.entity.Equipo;
import com.ligasantafesina.ligabackend.repository.EquipoRepository;
import com.ligasantafesina.ligabackend.repository.TorneoRepository;
import com.ligasantafesina.ligabackend.exception.RecursoNoEncontradoException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TablaPosicionesService {

    private final EquipoRepository equipoRepository;
    private final TorneoRepository torneoRepository;
    private final JdbcTemplate jdbcTemplate;

    public TablaPosicionesService(
            EquipoRepository equipoRepository,
            TorneoRepository torneoRepository,
            JdbcTemplate jdbcTemplate) {
        this.equipoRepository = equipoRepository;
        this.torneoRepository = torneoRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PosicionResponse> obtenerTabla(Integer torneoId) {

        if (!torneoRepository.existsById(torneoId)) {
            throw new RecursoNoEncontradoException("Torneo no encontrado");
        }

        Map<Integer, Estadisticas> tabla = new HashMap<>();

        for (Equipo equipo : equipoRepository.findAll()) {
            if (equipo.getTorneoId().equals(torneoId)) {
                tabla.put(
                    equipo.getId(),
                    new Estadisticas(equipo.getId(), equipo.getNombre())
                );
            }
        }

        String sql = """
            SELECT p.equipo_local_id AS local_id,
                   p.equipo_visitante_id AS visitante_id,
                   SUM(CASE WHEN j.equipo_id = p.equipo_local_id
                       THEN 1 ELSE 0 END) AS goles_local,
                   SUM(CASE WHEN j.equipo_id = p.equipo_visitante_id
                       THEN 1 ELSE 0 END) AS goles_visitante
            FROM partido p
            LEFT JOIN evento_partido e
                ON e.partido_id = p.id AND e.tipo = 'GOL'
            LEFT JOIN jugador j ON j.id = e.jugador_id
            WHERE p.torneo_id = ?
              AND p.estado = 'FINALIZADO'
            GROUP BY p.id, p.equipo_local_id, p.equipo_visitante_id
            """;

        jdbcTemplate.query(sql, rs -> {
            Estadisticas local = tabla.get(rs.getInt("local_id"));
            Estadisticas visitante = tabla.get(rs.getInt("visitante_id"));

            if (local != null && visitante != null) {
                int golesLocal = rs.getInt("goles_local");
                int golesVisitante = rs.getInt("goles_visitante");

                local.registrar(golesLocal, golesVisitante);
                visitante.registrar(golesVisitante, golesLocal);
            }
        }, torneoId);

        List<Estadisticas> ordenados = new ArrayList<>(tabla.values());

        ordenados.sort((a, b) -> {
            int resultado = Integer.compare(b.puntos, a.puntos);
            if (resultado != 0) return resultado;

            resultado = Integer.compare(
                b.golesFavor - b.golesContra,
                a.golesFavor - a.golesContra
            );
            if (resultado != 0) return resultado;

            resultado = Integer.compare(b.golesFavor, a.golesFavor);
            if (resultado != 0) return resultado;

            return a.nombre.compareToIgnoreCase(b.nombre);
        });

        List<PosicionResponse> posiciones = new ArrayList<>();

        for (Estadisticas e : ordenados) {
            posiciones.add(new PosicionResponse(
                posiciones.size() + 1,
                e.id,
                e.nombre,
                e.jugados,
                e.ganados,
                e.empatados,
                e.perdidos,
                e.golesFavor,
                e.golesContra,
                e.golesFavor - e.golesContra,
                e.puntos
            ));
        }

        return posiciones;
    }

    private static class Estadisticas {
        int id;
        String nombre;
        int jugados;
        int ganados;
        int empatados;
        int perdidos;
        int golesFavor;
        int golesContra;
        int puntos;

        Estadisticas(int id, String nombre) {
            this.id = id;
            this.nombre = nombre;
        }

        void registrar(int favor, int contra) {
            jugados++;
            golesFavor += favor;
            golesContra += contra;

            if (favor > contra) {
                ganados++;
                puntos += 3;
            } else if (favor == contra) {
                empatados++;
                puntos++;
            } else {
                perdidos++;
            }
        }
    }
}
