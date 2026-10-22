package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.LoginRequest;
import com.ligasantafesina.ligabackend.dto.LoginResponse;
import com.ligasantafesina.ligabackend.entity.Usuario;
import com.ligasantafesina.ligabackend.exception.CredencialesInvalidasException;
import com.ligasantafesina.ligabackend.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthService {
    private final UsuarioRepository repository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository repository, BCryptPasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = repository.findByUsername(request.username())
                .orElseThrow(CredencialesInvalidasException::new);
        if (!Boolean.TRUE.equals(usuario.getActivo()) || !passwordEncoder.matches(request.password(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException();
        }
        return new LoginResponse(usuario.getId(), usuario.getUsername(),
                usuario.getNombreCompleto(), usuario.getRol());
    }
}
