package com.ligasantafesina.ligabackend.dto;

import jakarta.validation.constraints.*;

public record EquipoRequest(
        @NotNull @Positive Integer torneoId,
        @NotBlank @Size(max = 80) String nombre,
        @Size(max = 80) String barrio) {
}
