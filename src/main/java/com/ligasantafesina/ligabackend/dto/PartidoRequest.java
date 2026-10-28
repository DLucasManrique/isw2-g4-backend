package com.ligasantafesina.ligabackend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public record PartidoRequest(
        @NotNull @Positive Integer numeroFecha,
        @NotNull @Positive Integer equipoLocalId,
        @NotNull @Positive Integer equipoVisitanteId,
        @NotNull @Positive Integer canchaId,
        @NotNull @Positive Integer arbitroId,
        @NotNull LocalDateTime fechaHoraInicio) {
}
