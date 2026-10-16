package com.ligasantafesina.ligabackend.exception;

import com.ligasantafesina.ligabackend.controller.JugadorController;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = JugadorController.class)
public class JugadorExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> manejarErrorDeNegocio(
            ResponseStatusException exception) {

        String mensaje = exception.getReason() != null
            ? exception.getReason()
            : "No se pudo completar la operación";

        return ResponseEntity
            .status(exception.getStatusCode())
            .body(Map.of(
                "estado", exception.getStatusCode().value(),
                "mensaje", mensaje
            ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(
            MethodArgumentNotValidException exception) {

        Map<String, String> errores = new LinkedHashMap<>();

        exception.getBindingResult().getFieldErrors().forEach(error ->
            errores.putIfAbsent(
                error.getField(),
                error.getDefaultMessage() != null
                    ? error.getDefaultMessage()
                    : "Valor inválido"
            )
        );

        return ResponseEntity.badRequest().body(Map.of(
            "estado", 400,
            "mensaje", "Revisá los datos del jugador",
            "errores", errores
        ));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(
            HttpMessageNotReadableException exception) {

        return ResponseEntity.badRequest().body(Map.of(
            "estado", 400,
            "mensaje", "El cuerpo de la solicitud falta o tiene un formato inválido"
        ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(
            MethodArgumentTypeMismatchException exception) {

        return ResponseEntity.badRequest().body(Map.of(
            "estado", 400,
            "mensaje", "El parámetro '" + exception.getName()
                + "' debe ser un número entero válido"
        ));
    }
}