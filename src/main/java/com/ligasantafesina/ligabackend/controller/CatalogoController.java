package com.ligasantafesina.ligabackend.controller;

import com.ligasantafesina.ligabackend.entity.Arbitro;
import com.ligasantafesina.ligabackend.entity.Cancha;
import com.ligasantafesina.ligabackend.entity.Torneo;
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
    public List<Torneo> obtenerTorneos() {
        return catalogoService.listarTorneos();
    }

    @GetMapping("/canchas")
    public List<Cancha> obtenerCanchas() {
        return catalogoService.listarCanchas(); // O ajustado a tu instancia
    }

    @GetMapping("/arbitros")
    public List<Arbitro> obtenerArbitros() {
        return catalogoService.listarArbitros();
    }
}