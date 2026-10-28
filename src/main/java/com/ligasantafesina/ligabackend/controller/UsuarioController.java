package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.UsuarioRequest;
import com.ligasantafesina.ligabackend.dto.UsuarioResponse;
import com.ligasantafesina.ligabackend.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar(HttpServletRequest request) {
        validarAdministrador(request);
        return ResponseEntity.ok(usuarioService.listarUsuarios());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id, HttpServletRequest request) {
        validarAdministrador(request);
        return ResponseEntity.ok(usuarioService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@RequestBody UsuarioRequest request, HttpServletRequest httpRequest) {
        validarAdministrador(httpRequest);
        return ResponseEntity.ok(usuarioService.crearUsuario(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> editar(@PathVariable Long id, @RequestBody UsuarioRequest request,
                                                 HttpServletRequest httpRequest) {
        validarAdministrador(httpRequest);
        return ResponseEntity.ok(usuarioService.editarUsuario(id, request));
    }

    @PatchMapping("/{id}/baja")
    public ResponseEntity<Void> bajaLogica(@PathVariable Long id, HttpServletRequest request) {
        validarAdministrador(request);
        usuarioService.bajaLogica(id);
        return ResponseEntity.noContent().build();
    }

    private void validarAdministrador(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object usuarioId = session != null ? session.getAttribute("usuarioId") : null;
        usuarioService.validarAdministrador(usuarioId instanceof Long id ? id : null);
    }
}
