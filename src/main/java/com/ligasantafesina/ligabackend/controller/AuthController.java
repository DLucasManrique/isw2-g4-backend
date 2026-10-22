package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.LoginRequest;
import com.ligasantafesina.ligabackend.dto.LoginResponse;
import com.ligasantafesina.ligabackend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        return service.login(request);
    }
}
