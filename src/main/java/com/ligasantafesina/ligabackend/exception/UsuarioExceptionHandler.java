package com.ligasantafesina.ligabackend.exception;

import com.ligasantafesina.ligabackend.controller.UsuarioController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.Map;

@RestControllerAdvice(assignableTypes = UsuarioController.class)
public class UsuarioExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(UsuarioExceptionHandler.class);

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
        Throwable causa = ex;
        while (causa != null) {
            if (causa instanceof SQLException sql) {
                if ("23505".equals(sql.getSQLState())) {
                    return ResponseEntity.status(409).body(Map.of("estado", 409,
                            "mensaje", "Los datos del usuario entran en conflicto con un valor único existente"));
                }
                if ("23513".equals(sql.getSQLState())) {
                    return ResponseEntity.badRequest().body(Map.of("estado", 400,
                            "mensaje", "Los datos del usuario no cumplen las restricciones permitidas"));
                }
                break;
            }
            causa = causa.getCause();
        }
        logger.error("Error de persistencia al guardar el usuario", ex);
        return ResponseEntity.status(500).body(Map.of("estado", 500,
                "mensaje", "No se pudo guardar el usuario por un error interno"));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> formato(Exception ex) {
        return ResponseEntity.badRequest().body(Map.of("estado", 400,
                "mensaje", "El cuerpo JSON o el identificador tienen un formato inválido"));
    }
}
