
package com.ligasantafesina.ligabackend.dto;

public record PosicionResponse(
    int posicion,
    Integer equipoId,
    String equipo,
    int partidosJugados,
    int ganados,
    int empatados,
    int perdidos,
    int golesFavor,
    int golesContra,
    int diferenciaGol,
    int puntos
) {
}
