package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.entity.Cancha;
import com.ligasantafesina.ligabackend.entity.Arbitro;
import com.ligasantafesina.ligabackend.entity.Torneo;
import com.ligasantafesina.ligabackend.repository.TorneoRepository;
import com.ligasantafesina.ligabackend.repository.ArbitroRepository;
import com.ligasantafesina.ligabackend.repository.CanchaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CatalogoService {

    @Autowired
    private TorneoRepository torneoRepository;

    @Autowired
    private CanchaRepository canchaRepository;

    @Autowired
    private ArbitroRepository arbitroRepository;

    public List<Torneo> listarTorneos() {
        return torneoRepository.findAll();
    }

    public List<Cancha> listarCanchas() {
        return canchaRepository.findAll();
    }

    public List<Arbitro> listarArbitros() {
        return arbitroRepository.findAll();
    }
}