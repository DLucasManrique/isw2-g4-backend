package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.JugadorRequest;
import com.ligasantafesina.ligabackend.dto.JugadorResponse;
import com.ligasantafesina.ligabackend.service.JugadorService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    private final JugadorService jugadorService;

    public JugadorController(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @PostMapping
    public ResponseEntity<JugadorResponse> crear(
            @Valid @RequestBody JugadorRequest request) {

        JugadorResponse jugador = jugadorService.crear(request);

        URI ubicacion = URI.create("/api/jugadores/" + jugador.id());

        return ResponseEntity.created(ubicacion).body(jugador);
    }

    @GetMapping
    public List<JugadorResponse> listar(
            @RequestParam(name = "equipoId", required = false)
            Integer equipoId) {

        return jugadorService.listar(equipoId);
    }

    @GetMapping("/{id}")
    public JugadorResponse obtenerPorId(@PathVariable("id") Integer id) {
        return jugadorService.obtenerPorId(id);
    }

    @PutMapping("/{id}")
    public JugadorResponse modificar(
            @PathVariable("id") Integer id,
            @Valid @RequestBody JugadorRequest request) {

        return jugadorService.modificar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable("id") Integer id) {
        jugadorService.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}