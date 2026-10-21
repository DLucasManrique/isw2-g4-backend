package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.PartidoRequest;
import com.ligasantafesina.ligabackend.dto.PartidoResponse;
import com.ligasantafesina.ligabackend.service.PartidoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {
    private final PartidoService service;

    public PartidoController(PartidoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PartidoResponse> crear(@Valid @RequestBody PartidoRequest request) {
        PartidoResponse response = service.crear(request);
        return ResponseEntity.created(URI.create("/api/partidos/" + response.id())).body(response);
    }
}
