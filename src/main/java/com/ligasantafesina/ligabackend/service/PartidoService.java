package com.ligasantafesina.ligabackend.service;

import com.ligasantafesina.ligabackend.dto.PartidoRequest;
import com.ligasantafesina.ligabackend.dto.PartidoResponse;
import com.ligasantafesina.ligabackend.dto.PartidoConsultaResponse;
import com.ligasantafesina.ligabackend.dto.MarcadorResponse;
import com.ligasantafesina.ligabackend.entity.Equipo;
import com.ligasantafesina.ligabackend.entity.Partido;
import com.ligasantafesina.ligabackend.exception.*;
import com.ligasantafesina.ligabackend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PartidoService {
    private final PartidoRepository partidoRepository;
    private final TorneoRepository torneoRepository;
    private final EquipoRepository equipoRepository;
    private final CanchaRepository canchaRepository;
    private final ArbitroRepository arbitroRepository;
    private final ResultadoPartidoRepository resultadoPartidoRepository;

    public PartidoService(PartidoRepository partidoRepository, TorneoRepository torneoRepository,
                          EquipoRepository equipoRepository, CanchaRepository canchaRepository,
                          ArbitroRepository arbitroRepository, ResultadoPartidoRepository resultadoPartidoRepository) {
        this.partidoRepository = partidoRepository;
        this.torneoRepository = torneoRepository;
        this.equipoRepository = equipoRepository;
        this.canchaRepository = canchaRepository;
        this.arbitroRepository = arbitroRepository;
        this.resultadoPartidoRepository = resultadoPartidoRepository;
    }

    @Transactional(readOnly = true)
    public List<PartidoConsultaResponse> listar() {
        return partidoRepository.findAll(Sort.by("fechaHoraInicio", "id")).stream()
                .map(this::aConsultaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PartidoConsultaResponse obtenerPorId(Integer id) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el partido indicado."));
        return aConsultaResponse(partido);
    }

    @Transactional
    public PartidoResponse cambiarEstado(Integer id, String estado) {
        Partido partido = partidoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe el partido indicado."));
        boolean permitida = "PROGRAMADO".equals(partido.getEstado())
                && ("EN_JUEGO".equals(estado) || "SUSPENDIDO".equals(estado))
                || "EN_JUEGO".equals(partido.getEstado())
                && ("FINALIZADO".equals(estado) || "SUSPENDIDO".equals(estado));
        if (!permitida) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "No se puede pasar de " + partido.getEstado() + " a " + estado + ".");
        }
        partido.cambiarEstado(estado);
        return PartidoResponse.desdeEntidad(partidoRepository.saveAndFlush(partido));
    }

    private PartidoConsultaResponse aConsultaResponse(Partido partido) {
        MarcadorResponse marcador = resultadoPartidoRepository.obtenerMarcador(
                partido.getId(), partido.getEquipoLocalId(), partido.getEquipoVisitanteId());
        return new PartidoConsultaResponse(partido.getId(), partido.getTorneoId(), partido.getNumeroFecha(),
                partido.getEquipoLocalId(), partido.getEquipoVisitanteId(), partido.getCanchaId(),
                partido.getArbitroId(), partido.getFechaHoraInicio(), partido.getEstado(),
                marcador.golesLocal(), marcador.golesVisitante());
    }

    @Transactional
    public PartidoResponse crear(PartidoRequest request) {
        if (request.equipoLocalId().equals(request.equipoVisitanteId())) {
            throw new PartidoInvalidoException("El equipo local y el visitante deben ser diferentes.");
        }
        if (!torneoRepository.existsById(request.torneoId())) {
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
        if (!canchaRepository.existsById(request.canchaId())) {
            throw new RecursoNoEncontradoException("No existe la cancha indicada.");
        }
        if (!arbitroRepository.existsById(request.arbitroId())) {
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
