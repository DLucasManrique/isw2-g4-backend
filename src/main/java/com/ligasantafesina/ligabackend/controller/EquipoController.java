package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.*;
import com.ligasantafesina.ligabackend.service.EquipoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {
    private final EquipoService service;

    public EquipoController(EquipoService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@Valid @RequestBody EquipoRequest request) {
        EquipoResponse response = service.crear(request);
        return ResponseEntity.created(URI.create("/api/equipos/" + response.id())).body(response);
    }

    @GetMapping
    public List<EquipoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public EquipoResponse buscar(@PathVariable Integer id) { return service.buscarPorId(id); }

    @PutMapping("/{id}")
    public EquipoResponse modificar(@PathVariable Integer id, @Valid @RequestBody EquipoRequest request) {
        return service.modificar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Integer id) {
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }
}
