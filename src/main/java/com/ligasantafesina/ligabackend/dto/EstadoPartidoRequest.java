package com.ligasantafesina.ligabackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EstadoPartidoRequest(
        @NotBlank
        @Pattern(regexp = "PROGRAMADO|EN_JUEGO|FINALIZADO|SUSPENDIDO",
                message = "El estado debe ser PROGRAMADO, EN_JUEGO, FINALIZADO o SUSPENDIDO")
        String estado) {
}
