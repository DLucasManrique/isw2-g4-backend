package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.EventoRequest;
import com.ligasantafesina.ligabackend.dto.EventoResponse;
import com.ligasantafesina.ligabackend.entity.EventoPartido;
import com.ligasantafesina.ligabackend.repository.EventoPartidoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoPartidoService {

    private final EventoPartidoRepository eventoRepository;

    public EventoPartidoService(EventoPartidoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    // RF-08: Listar eventos de un partido
    public List<EventoResponse> listarPorPartido(Long partidoId) {
        return eventoRepository.findByPartidoId(partidoId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // RF-08: Registrar un gol o tarjeta
    public EventoResponse registrarEvento(EventoRequest request) {
        if (request.getPartidoId() == null || request.getJugadorId() == null || request.getTipoEvento() == null) {
            throw new IllegalArgumentException("El partido, jugador y tipo de evento son obligatorios.");
        }

        EventoPartido evento = new EventoPartido();
        evento.setPartidoId(request.getPartidoId());
        evento.setJugadorId(request.getJugadorId());
        evento.setEquipoId(request.getEquipoId());
        evento.setTipoEvento(request.getTipoEvento());
        evento.setMinuto(request.getMinuto());
        evento.setObservacion(request.getObservacion());

        EventoPartido guardado = eventoRepository.save(evento);
        return mapToResponse(guardado);
    }

    // RF-09: Editar / Corregir un evento existente
    public EventoResponse corregirEvento(Long id, EventoRequest request) {
        EventoPartido evento = eventoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evento no encontrado con ID: " + id));

        if (request.getJugadorId() != null) evento.setJugadorId(request.getJugadorId());
        if (request.getTipoEvento() != null) evento.setTipoEvento(request.getTipoEvento());
        if (request.getMinuto() != null) evento.setMinuto(request.getMinuto());
        if (request.getObservacion() != null) evento.setObservacion(request.getObservacion());

        EventoPartido actualizado = eventoRepository.save(evento);
        return mapToResponse(actualizado);
    }

    // RF-09: Eliminar un evento registrado por error
    public void eliminarEvento(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new RuntimeException("Evento no encontrado con ID: " + id);
        }
        eventoRepository.deleteById(id);
    }

    private EventoResponse mapToResponse(EventoPartido e) {
        return new EventoResponse(
                e.getId(),
                e.getPartidoId(),
                e.getJugadorId(),
                e.getEquipoId(),
                e.getTipoEvento(),
                e.getMinuto(),
                e.getObservacion()
        );
    }
}