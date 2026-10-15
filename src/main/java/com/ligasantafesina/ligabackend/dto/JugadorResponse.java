package com.ligasantafesina.ligabackend.dto;

import com.ligasantafesina.ligabackend.entity.Jugador;

public record JugadorResponse(
    Integer id,
    Integer equipoId,
    String nombre,
    String apellido,
    String dni,
    Integer numeroCamiseta,
    boolean activo
) {

    public static JugadorResponse desdeEntidad(Jugador jugador) {
        return new JugadorResponse(
            jugador.getId(),
            jugador.getEquipoId(),
            jugador.getNombre(),
            jugador.getApellido(),
            jugador.getDni(),
            jugador.getNumeroCamiseta(),
            jugador.isActivo()
        );
    }
}