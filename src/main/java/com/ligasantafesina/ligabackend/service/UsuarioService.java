package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.UsuarioRequest;
import com.ligasantafesina.ligabackend.dto.UsuarioResponse;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.exception.RecursoNoEncontradoException;
import com.ligasantafesina.ligabackend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
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
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        return new UsuarioResponse(u.getId(), u.getUsername(), u.getNombreCompleto(), u.getRol(), u.getActivo());
    }

    @Transactional
    public UsuarioResponse crearUsuario(UsuarioRequest request) {
        validarDatos(request);
        validarPassword(request.getPassword());

        String username = request.getUsername().trim();
        if (usuarioRepository.existsByUsername(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El username ya existe");
        }

        Usuario u = new Usuario();
        u.setUsername(username);
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        u.setNombreCompleto(request.getNombreCompleto().trim());
        u.setRol(request.getRol());
        u.setActivo(true);

        Usuario guardado = usuarioRepository.saveAndFlush(u);
        return new UsuarioResponse(guardado.getId(), guardado.getUsername(), guardado.getNombreCompleto(), guardado.getRol(), guardado.getActivo());
    }

    @Transactional
    public UsuarioResponse editarUsuario(Long id, UsuarioRequest request) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));

        validarDatos(request);
        if (!u.getUsername().equals(request.getUsername().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username no se puede modificar");
        }
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            validarPassword(request.getPassword());
            u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        }
        u.setNombreCompleto(request.getNombreCompleto().trim());
        u.setRol(request.getRol());

        Usuario actualizado = usuarioRepository.saveAndFlush(u);
        return new UsuarioResponse(actualizado.getId(), actualizado.getUsername(), actualizado.getNombreCompleto(), actualizado.getRol(), actualizado.getActivo());
    }

    @Transactional
    public void bajaLogica(Long id) {
        Usuario u = usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado"));
        u.setActivo(false);
        usuarioRepository.saveAndFlush(u);
    }

    private void validarDatos(UsuarioRequest request) {
        if (request.getUsername() == null || request.getUsername().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username es obligatorio");
        }
        if (request.getUsername().trim().length() > 40) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El username admite hasta 40 caracteres");
        }
        if (request.getNombreCompleto() == null || request.getNombreCompleto().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre completo es obligatorio");
        }
        if (request.getNombreCompleto().trim().length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre completo admite hasta 100 caracteres");
        }
        if (!"ADMIN".equals(request.getRol()) && !"DELEGADO".equals(request.getRol())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El rol debe ser ADMIN o DELEGADO");
        }
    }

    private void validarPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña es obligatoria");
        }
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La contraseña admite hasta 72 bytes en UTF-8 por BCrypt");
        }
    }
}
