package com.ligasantafesina.ligabackend.dto;

public record LoginResponse(Long id, String username, String nombreCompleto, String rol, Integer equipoId) {}
