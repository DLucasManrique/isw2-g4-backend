
package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.PosicionResponse;
import com.ligasantafesina.ligabackend.service.TablaPosicionesService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/torneos")
public class TablaPosicionesController {

    private final TablaPosicionesService tablaPosicionesService;

    public TablaPosicionesController(
            TablaPosicionesService tablaPosicionesService) {
        this.tablaPosicionesService = tablaPosicionesService;
    }

    @GetMapping("/{torneoId}/posiciones")
    public List<PosicionResponse> obtenerTabla(
            @PathVariable("torneoId") Integer torneoId) {

        return tablaPosicionesService.obtenerTabla(torneoId);
    }
}
