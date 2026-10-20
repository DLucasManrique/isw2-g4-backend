package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.CanchaResponse;
import com.ligasantafesina.ligabackend.dto.ArbitroResponse;
import com.ligasantafesina.ligabackend.dto.TorneoResponse;
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

    public List<TorneoResponse> listarTorneos() {
        return torneoRepository.findAll().stream()
                .map(torneo -> new TorneoResponse(torneo.getId(), torneo.getNombre(),
                        torneo.getAnio(), torneo.getActivo())).toList();
    }

    public List<CanchaResponse> listarCanchas() {
        return canchaRepository.findAll().stream()
                .map(cancha -> new CanchaResponse(cancha.getId(), cancha.getNombre(),
                        cancha.getDireccion(), cancha.getActivo())).toList();
    }

    public List<ArbitroResponse> listarArbitros() {
        return arbitroRepository.findAll().stream()
                .map(arbitro -> new ArbitroResponse(arbitro.getId(), arbitro.getNombre(),
                        arbitro.getApellido(), arbitro.getDni(), arbitro.getActivo())).toList();
    }
}
