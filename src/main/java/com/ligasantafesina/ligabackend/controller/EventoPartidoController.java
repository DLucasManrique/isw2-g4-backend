package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.EventoRequest;
import com.ligasantafesina.ligabackend.dto.EventoResponse;
import com.ligasantafesina.ligabackend.service.EventoPartidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoPartidoController {

    private final EventoPartidoService eventoService;

    public EventoPartidoController(EventoPartidoService eventoService) {
        this.eventoService = eventoService;
    }

    // Listar eventos de un partido
    @GetMapping("/partido/{partidoId}")
    public ResponseEntity<List<EventoResponse>> listarPorPartido(@PathVariable Long partidoId) {
        return ResponseEntity.ok(eventoService.listarPorPartido(partidoId));
    }

    // RF-08: Registrar gol/tarjeta
    @PostMapping
    public ResponseEntity<EventoResponse> registrarEvento(@RequestBody EventoRequest request) {
        return new ResponseEntity<>(eventoService.registrarEvento(request), HttpStatus.CREATED);
    }

    // RF-09: Corregir evento
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponse> corregirEvento(@PathVariable Long id, @RequestBody EventoRequest request) {
        return ResponseEntity.ok(eventoService.corregirEvento(id, request));
    }

    // RF-09: Eliminar evento (ej: gol anulado o mal cobrado)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEvento(@PathVariable Long id) {
        eventoService.eliminarEvento(id);
        return ResponseEntity.noContent().build();
    }
}