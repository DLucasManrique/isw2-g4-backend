package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.JugadorSuspendidoResponse;
import com.ligasantafesina.ligabackend.entity.EventoPartido;
import com.ligasantafesina.ligabackend.repository.EventoPartidoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SuspendidosService {

    private final EventoPartidoRepository eventoRepository;

    public SuspendidosService(EventoPartidoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public List<JugadorSuspendidoResponse> obtenerJugadoresSuspendidos() {
        List<EventoPartido> todosLosEventos = eventoRepository.findAll();
        List<JugadorSuspendidoResponse> suspendidos = new ArrayList<>();

        // 1. Agrupar eventos por Jugador
        Map<Long, List<EventoPartido>> eventosPorJugador = todosLosEventos.stream()
                .filter(e -> e.getJugadorId() != null)
                .collect(Collectors.groupingBy(EventoPartido::getJugadorId));

        // 2. Analizar las tarjetas de cada jugador
        for (Map.Entry<Long, List<EventoPartido>> entry : eventosPorJugador.entrySet()) {
            Long jugadorId = entry.getKey();
            List<EventoPartido> eventos = entry.getValue();

            long amarillas = eventos.stream()
                    .filter(e -> "TARJETA_AMARILLA".equalsIgnoreCase(e.getTipoEvento()))
                    .count();

            long rojas = eventos.stream()
                    .filter(e -> "TARJETA_ROJA".equalsIgnoreCase(e.getTipoEvento()))
                    .count();

            // Regla 1: Expulsión por tarjeta roja directa o doble amarilla
            if (rojas > 0) {
                suspendidos.add(new JugadorSuspendidoResponse(
                        jugadorId,
                        "Jugador #" + jugadorId,
                        "",
                        "Equipo",
                        "Expulsión (Tarjeta Roja)",
                        1
                ));
            }
            // Regla 2: Acumulación de tarjetas amarillas (cada 5 amarillas)
            else if (amarillas >= 5) {
                suspendidos.add(new JugadorSuspendidoResponse(
                        jugadorId,
                        "Jugador #" + jugadorId,
                        "",
                        "Equipo",
                        "Acumulación de " + amarillas + " tarjetas amarillas",
                        1
                ));
            }
        }

        return suspendidos;
    }
}
