package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.LoginRequest;
import com.ligasantafesina.ligabackend.dto.LoginResponse;
import com.ligasantafesina.ligabackend.service.AuthService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        LoginResponse response = service.login(request);
        HttpSession anterior = httpRequest.getSession(false);
        if (anterior != null) anterior.invalidate();
        httpRequest.getSession(true).setAttribute("usuarioId", response.id());
        return response;
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) session.invalidate();
        return ResponseEntity.noContent().build();
    }
}
