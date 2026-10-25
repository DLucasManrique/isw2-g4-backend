package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.JugadorSuspendidoResponse;
import com.ligasantafesina.ligabackend.service.SuspendidosService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suspendidos")
public class SuspendidosController {

    private final SuspendidosService suspendidosService;

    public SuspendidosController(SuspendidosService suspendidosService) {
        this.suspendidosService = suspendidosService;
    }

    // RF-12: Consultar lista de jugadores suspendidos
    @GetMapping
    public ResponseEntity<List<JugadorSuspendidoResponse>> listarSuspendidos() {
        return ResponseEntity.ok(suspendidosService.obtenerJugadoresSuspendidos());
    }
}