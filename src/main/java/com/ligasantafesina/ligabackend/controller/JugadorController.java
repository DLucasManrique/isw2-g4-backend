package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.JugadorRequest;
import com.ligasantafesina.ligabackend.dto.JugadorResponse;
import com.ligasantafesina.ligabackend.service.JugadorService;

import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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
            @Valid @RequestBody JugadorRequest request, HttpServletRequest httpRequest) {

        JugadorResponse jugador = jugadorService.crear(request, usuarioId(httpRequest));

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
            @Valid @RequestBody JugadorRequest request, HttpServletRequest httpRequest) {

        return jugadorService.modificar(id, request, usuarioId(httpRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable("id") Integer id, HttpServletRequest request) {
        jugadorService.darDeBaja(id, usuarioId(request));
        return ResponseEntity.noContent().build();
    }

    private Long usuarioId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object usuarioId = session != null ? session.getAttribute("usuarioId") : null;
        return usuarioId instanceof Long id ? id : null;
    }
}
