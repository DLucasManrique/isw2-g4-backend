package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.*;
import com.ligasantafesina.ligabackend.service.EquipoService;
import com.ligasantafesina.ligabackend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/equipos")
public class EquipoController {
    private final EquipoService service;
    private final UsuarioService usuarioService;

    public EquipoController(EquipoService service, UsuarioService usuarioService) {
        this.service = service;
        this.usuarioService = usuarioService;
    }

    @PostMapping
    public ResponseEntity<EquipoResponse> crear(@Valid @RequestBody EquipoRequest request,
                                               HttpServletRequest httpRequest) {
        validarAdministrador(httpRequest);
        EquipoResponse response = service.crear(request);
        return ResponseEntity.created(URI.create("/api/equipos/" + response.id())).body(response);
    }

    @GetMapping
    public List<EquipoResponse> listar() { return service.listar(); }

    @GetMapping("/{id}")
    public EquipoResponse buscar(@PathVariable Integer id) { return service.buscarPorId(id); }

    @PutMapping("/{id}")
    public EquipoResponse modificar(@PathVariable Integer id, @Valid @RequestBody EquipoRequest request,
                                     HttpServletRequest httpRequest) {
        validarAdministrador(httpRequest);
        return service.modificar(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> darDeBaja(@PathVariable Integer id, HttpServletRequest request) {
        validarAdministrador(request);
        service.darDeBaja(id);
        return ResponseEntity.noContent().build();
    }

    private void validarAdministrador(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object usuarioId = session != null ? session.getAttribute("usuarioId") : null;
        usuarioService.validarAdministrador(usuarioId instanceof Long id ? id : null);
    }
}
