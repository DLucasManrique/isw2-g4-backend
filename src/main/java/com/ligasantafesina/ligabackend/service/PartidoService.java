package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.PartidoRequest;
import com.ligasantafesina.ligabackend.dto.PartidoResponse;
import com.ligasantafesina.ligabackend.entity.Equipo;
import com.ligasantafesina.ligabackend.entity.Partido;
import com.ligasantafesina.ligabackend.exception.*;
import com.ligasantafesina.ligabackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
public class PartidoService {
    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final CanchaRepository canchaRepository;
    private final ArbitroRepository arbitroRepository;

    public PartidoService(PartidoRepository partidoRepository, TorneoRepository torneoRepository,
                          EquipoRepository equipoRepository, CanchaRepository canchaRepository,
                          ArbitroRepository arbitroRepository) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.canchaRepository = canchaRepository;
        this.arbitroRepository = arbitroRepository;
    }

    @Transactional
    public PartidoResponse crear(PartidoRequest request) {
        if (request.equipoLocalId().equals(request.equipoVisitanteId())) {
            throw new PartidoInvalidoException("El equipo local y el visitante deben ser diferentes.");
        }
        if (!torneoRepository.existsById(request.torneoId().longValue())) {
            throw new RecursoNoEncontradoException("No existe el torneo indicado.");
        }
        Equipo local = equipoRepository.findById(request.equipoLocalId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el equipo local indicado."));
        Equipo visitante = equipoRepository.findById(request.equipoVisitanteId())
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el equipo visitante indicado."));
        if (!local.getTorneoId().equals(request.torneoId())) {
            throw new PartidoInvalidoException("El equipo local no pertenece al torneo indicado.");
        }
        if (!visitante.getTorneoId().equals(request.torneoId())) {
            throw new PartidoInvalidoException("El equipo visitante no pertenece al torneo indicado.");
        }
        if (!canchaRepository.existsById(request.canchaId().longValue())) {
            throw new RecursoNoEncontradoException("No existe la cancha indicada.");
        }
        if (!arbitroRepository.existsById(request.arbitroId().longValue())) {
            throw new RecursoNoEncontradoException("No existe el árbitro indicado.");
        }
        LocalDateTime desde = request.fechaHoraInicio().minusHours(2);
        LocalDateTime hasta = request.fechaHoraInicio().plusHours(2);
        if (partidoRepository.existeConflictoCancha(request.canchaId(), desde, hasta)) {
            throw new DisponibilidadConflictoException(
                    "La cancha tiene otro partido con una separación menor a 2 horas.");
        }
        if (partidoRepository.existeConflictoArbitro(request.arbitroId(), desde, hasta)) {
            throw new DisponibilidadConflictoException(
                    "El árbitro tiene otro partido con una separación menor a 2 horas.");
        }
        Partido partido = new Partido(request.torneoId(), request.numeroFecha(), request.equipoLocalId(),
                request.equipoVisitanteId(), request.canchaId(), request.arbitroId(), request.fechaHoraInicio());
        return PartidoResponse.desdeEntidad(partidoRepository.saveAndFlush(partido));
    }
}
