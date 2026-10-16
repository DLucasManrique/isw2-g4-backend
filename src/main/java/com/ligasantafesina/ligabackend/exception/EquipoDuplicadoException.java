package com.ligasantafesina.ligabackend.exception;

public class EquipoDuplicadoException extends RuntimeException {
    public EquipoDuplicadoException() {
        super("Ya existe un equipo con ese nombre en el torneo indicado.");
    }
}
