package com.ligasantafesina.ligabackend.exception;

import com.ligasantafesina.ligabackend.controller.UsuarioController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestControllerAdvice(assignableTypes = UsuarioController.class)
public class UsuarioExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, Object>> negocio(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of(
                "estado", ex.getStatusCode().value(), "mensaje", ex.getReason()));
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(404).body(Map.of("estado", 404, "mensaje", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> conflicto(DataIntegrityViolationException ex) {
        return ResponseEntity.status(409).body(Map.of("estado", 409,
                "mensaje", "Los datos del usuario entran en conflicto; verificá que el username sea único"));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> formato(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("estado", 400,
                "mensaje", "El cuerpo JSON o el identificador tienen un formato inválido"));
    }
}
