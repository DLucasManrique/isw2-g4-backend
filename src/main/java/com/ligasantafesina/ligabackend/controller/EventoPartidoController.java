package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.EventoRequest;
import com.ligasantafesina.ligabackend.dto.EventoResponse;
import com.ligasantafesina.ligabackend.service.EventoPartidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EventoPartidoController {
    private final EventoPartidoService eventoService;

    public EventoPartidoController(EventoPartidoService eventoService) {
        this.eventoService = eventoService;
    }

    @GetMapping({"/eventos/partido/{partidoId}", "/partidos/{partidoId}/eventos"})
    public ResponseEntity<List<EventoResponse>> listarPorPartido(@PathVariable Long partidoId) {
        return ResponseEntity.ok(eventoService.listarPorPartido(partidoId));
    }

    @PostMapping({"/eventos", "/partidos/{partidoId}/eventos"})
    public ResponseEntity<EventoResponse> registrarEvento(
            @PathVariable(required = false) Long partidoId, @RequestBody EventoRequest request) {
        if (partidoId != null) {
            if (request.getPartidoId() != null && !partidoId.equals(request.getPartidoId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El partido del cuerpo debe coincidir con el de la ruta.");
            }
            request.setPartidoId(partidoId);
        }
        return new ResponseEntity<>(eventoService.registrarEvento(request), HttpStatus.CREATED);
    }

    @PutMapping({"/eventos/{id}", "/partidos/{partidoId}/eventos/{id}"})
    public ResponseEntity<EventoResponse> corregirEvento(
            @PathVariable(required = false) Long partidoId, @PathVariable Long id,
            @RequestBody EventoRequest request) {
        return ResponseEntity.ok(eventoService.corregirEvento(partidoId, id, request));
    }

    @DeleteMapping({"/eventos/{id}", "/partidos/{partidoId}/eventos/{id}"})
    public ResponseEntity<Void> eliminarEvento(
            @PathVariable(required = false) Long partidoId, @PathVariable Long id) {
        eventoService.eliminarEvento(partidoId, id);
        return ResponseEntity.noContent().build();
    }
}
