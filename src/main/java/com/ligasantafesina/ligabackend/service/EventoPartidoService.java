package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.EventoRequest;
import com.ligasantafesina.ligabackend.dto.EventoResponse;
import com.ligasantafesina.ligabackend.entity.EventoPartido;
import com.ligasantafesina.ligabackend.entity.Jugador;
import com.ligasantafesina.ligabackend.entity.Partido;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.repository.EventoPartidoRepository;
import com.ligasantafesina.ligabackend.repository.JugadorRepository;
import com.ligasantafesina.ligabackend.repository.PartidoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class EventoPartidoService {
    private final EventoPartidoRepository eventoRepository;
    private final PartidoRepository partidoRepository;
    private final JugadorRepository jugadorRepository;
    private final UsuarioService usuarioService;

    public EventoPartidoService(EventoPartidoRepository eventoRepository,
                                PartidoRepository partidoRepository,
                                JugadorRepository jugadorRepository, UsuarioService usuarioService) {
        this.eventoRepository = eventoRepository;
        this.partidoRepository = partidoRepository;
        this.jugadorRepository = jugadorRepository;
        this.usuarioService = usuarioService;
    }

    public List<EventoResponse> listarPorPartido(Long partidoId) {
        obtenerPartido(partidoId);
        return eventoRepository.findByPartidoId(partidoId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional
    public EventoResponse registrarEvento(EventoRequest request, Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioActivo(usuarioId);
        validarEvento(usuario, request.getPartidoId(), request.getJugadorId(), request.getTipo(), request.getMinuto());
        EventoPartido evento = new EventoPartido();
        evento.setPartidoId(request.getPartidoId());
        evento.setJugadorId(request.getJugadorId());
        evento.setTipo(request.getTipo());
        evento.setMinuto(request.getMinuto());
        return mapToResponse(eventoRepository.saveAndFlush(evento));
    }

    @Transactional
    public EventoResponse corregirEvento(Long partidoId, Long id, EventoRequest request, Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioActivo(usuarioId);
        EventoPartido evento = obtenerEvento(partidoId, id);
        if (request.getPartidoId() != null && !request.getPartidoId().equals(evento.getPartidoId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "No se puede cambiar el partido de un evento.");
        }
        Long jugadorId = request.getJugadorId() != null ? request.getJugadorId() : evento.getJugadorId();
        String tipo = request.getTipo() != null ? request.getTipo() : evento.getTipo();
        Integer minuto = request.getMinuto() != null ? request.getMinuto() : evento.getMinuto();
        validarEvento(usuario, evento.getPartidoId(), jugadorId, tipo, minuto);
        evento.setJugadorId(jugadorId);
        evento.setTipo(tipo);
        evento.setMinuto(minuto);
        return mapToResponse(eventoRepository.saveAndFlush(evento));
    }

    @Transactional
    public void eliminarEvento(Long partidoId, Long id, Long usuarioId) {
        Usuario usuario = usuarioService.obtenerUsuarioActivo(usuarioId);
        EventoPartido evento = obtenerEvento(partidoId, id);
        Partido partido = obtenerPartido(evento.getPartidoId());
        usuarioService.validarOperacionPartido(usuario, partido);
        validarEnJuego(partido);
        eventoRepository.delete(evento);
    }

    private EventoPartido obtenerEvento(Long partidoId, Long id) {
        if (partidoId != null) obtenerPartido(partidoId);
        EventoPartido evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el evento indicado."));
        if (partidoId != null && !partidoId.equals(evento.getPartidoId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "El evento no pertenece al partido indicado.");
        }
        return evento;
    }

    private Partido obtenerPartido(Long partidoId) {
        Integer id = validarId(partidoId, "partido");
        return partidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el partido indicado."));
    }

    private Integer validarId(Long id, String recurso) {
        if (id == null || id <= 0 || id > Integer.MAX_VALUE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El ID de " + recurso + " debe ser un entero positivo valido.");
        }
        return id.intValue();
    }

    private void validarEnJuego(Partido partido) {
        if (!"EN_JUEGO".equals(partido.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Solo se pueden registrar, corregir o eliminar eventos de un partido EN_JUEGO.");
        }
    }

    private void validarEvento(Usuario usuario, Long partidoId, Long jugadorId, String tipo, Integer minuto) {
        Partido partido = obtenerPartido(partidoId);
        usuarioService.validarOperacionPartido(usuario, partido);
        validarEnJuego(partido);
        Jugador jugador = jugadorRepository.findById(validarId(jugadorId, "jugador"))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "No existe el jugador indicado."));
        if (!jugador.isActivo()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El jugador debe estar activo.");
        }
        if (!jugador.getEquipoId().equals(partido.getEquipoLocalId())
                && !jugador.getEquipoId().equals(partido.getEquipoVisitanteId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El jugador debe pertenecer a uno de los dos equipos del partido.");
        }
        if (minuto == null || minuto < 1 || minuto > 120) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El minuto debe estar entre 1 y 120.");
        }
        if (!"GOL".equals(tipo) && !"AMARILLA".equals(tipo) && !"ROJA".equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "El tipo de evento debe ser GOL, AMARILLA o ROJA.");
        }
    }

    private EventoResponse mapToResponse(EventoPartido evento) {
        return new EventoResponse(evento.getId(), evento.getPartidoId(), evento.getJugadorId(),
                evento.getTipo(), evento.getMinuto());
    }
}
