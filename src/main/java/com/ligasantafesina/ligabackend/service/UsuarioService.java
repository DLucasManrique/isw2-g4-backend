package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.UsuarioRequest;
import com.ligasantafesina.ligabackend.dto.UsuarioResponse;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listarUsuarios() {
        return usuarioRepository.findAll().stream()
                .map(u -> new UsuarioResponse(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol(), u.getActivo()))
                .collect(Collectors.toList());
    }

    public UsuarioResponse obtenerPorId(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol(), u.getActivo());
    }

    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        if (request.getUsername() == null || request.getUsername().isEmpty() ||
                request.getPassword() == null || request.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Username y password son obligatorios");
        }

        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("El username ya existe");
        }

        Usuario u = new Usuario();
        u.setUsername(request.getUsername());
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u.setNombreCompleto(request.getNombreCompleto());
        u.setRol(request.getRol() != null ? request.getRol() : "DELEGADO");
        u.setActivo(true);

        Usuario guardado = usuarioRepository.save(u);
        return new UsuarioResponse(guardado.getId(), guardado.getUsername(), guardado.getNombreCompleto(), guardado.getRol(), guardado.getActivo());
    }

    public UsuarioResponse editarUsuario(Long id, UsuarioRequest request) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (request.getNombreCompleto() != null) {
            u.setNombreCompleto(request.getNombreCompleto());
        }
        if (request.getRol() != null) {
            u.setRol(request.getRol());
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }

        Usuario actualizado = usuarioRepository.save(u);
        return new UsuarioResponse(actualizado.getId(), actualizado.getUsername(), actualizado.getNombreCompleto(), actualizado.getRol(), actualizado.getActivo());
    }

    public void bajaLogica(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        u.setActivo(false);
        usuarioRepository.save(u);
    }
}