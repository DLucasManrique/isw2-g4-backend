package com.ligasantafesina.ligabackend.dto;

import jakarta.validation.constraints.*;

public record JugadorRequest(

    @NotNull(message = "El equipo es obligatorio")
    @Positive(message = "El identificador del equipo debe ser positivo")
    Integer equipoId,

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 60, message = "El nombre no puede superar los 60 caracteres")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 60, message = "El apellido no puede superar los 60 caracteres")
    String apellido,

    @NotBlank(message = "El DNI es obligatorio")
    @Size(max = 10, message = "El DNI no puede superar los 10 caracteres")
    String dni,

    @Min(value = 1, message = "La camiseta debe estar entre 1 y 99")
    @Max(value = 99, message = "La camiseta debe estar entre 1 y 99")
    Integer numeroCamiseta

) {
}