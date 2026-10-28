package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.PartidoRequest;
import com.ligasantafesina.ligabackend.dto.PartidoResponse;
import com.ligasantafesina.ligabackend.dto.PartidoConsultaResponse;
import com.ligasantafesina.ligabackend.dto.EstadoPartidoRequest;
import com.ligasantafesina.ligabackend.service.PartidoService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/partidos")
public class PartidoController {
    private final PartidoService service;

    public PartidoController(PartidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PartidoConsultaResponse> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public PartidoConsultaResponse obtenerPorId(@PathVariable("id") Integer id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public ResponseEntity<PartidoResponse> crear(@Valid @RequestBody PartidoRequest request,
                                                HttpServletRequest httpRequest) {
        PartidoResponse response = service.crear(request, usuarioId(httpRequest));
        return ResponseEntity.created(URI.create("/api/partidos/" + response.id())).body(response);
    }

    @PatchMapping("/{id}/estado")
    public PartidoResponse cambiarEstado(@PathVariable Integer id,
                                        @Valid @RequestBody EstadoPartidoRequest request,
                                        HttpServletRequest httpRequest) {
        return service.cambiarEstado(id, request.estado(), usuarioId(httpRequest));
    }

    private Long usuarioId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object usuarioId = session != null ? session.getAttribute("usuarioId") : null;
        return usuarioId instanceof Long id ? id : null;
    }
}
