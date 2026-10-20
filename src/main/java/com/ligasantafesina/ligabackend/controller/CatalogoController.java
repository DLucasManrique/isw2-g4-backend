package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.dto.ArbitroResponse;
import com.ligasantafesina.ligabackend.dto.CanchaResponse;
import com.ligasantafesina.ligabackend.dto.TorneoResponse;
import com.ligasantafesina.ligabackend.service.CatalogoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo")
public class CatalogoController {

    @Autowired
    private CatalogoService catalogoService;

    @GetMapping("/torneos")
    public List<TorneoResponse> obtenerTorneos() {
        return catalogoService.listarTorneos();
    }

    @GetMapping("/canchas")
    public List<CanchaResponse> obtenerCanchas() {
        return catalogoService.listarCanchas();
    }

    @GetMapping("/arbitros")
    public List<ArbitroResponse> obtenerArbitros() {
        return catalogoService.listarArbitros();
    }
}
