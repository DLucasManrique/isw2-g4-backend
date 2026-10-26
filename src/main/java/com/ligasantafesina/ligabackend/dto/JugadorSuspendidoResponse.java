package com.ligasantafesina.ligabackend.dto;

public record JugadorSuspendidoResponse(Integer jugadorId, String nombre, String apellido,
                                        String equipoNombre, Integer partidoId, String motivo) {
}
