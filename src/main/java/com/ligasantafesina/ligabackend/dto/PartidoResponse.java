package com.ligasantafesina.ligabackend.dto;

import com.ligasantafesina.ligabackend.entity.Partido;
import java.time.LocalDateTime;

public record PartidoResponse(Integer id, Integer torneoId, Integer numeroFecha,
                              Integer equipoLocalId, Integer equipoVisitanteId,
                              Integer canchaId, Integer arbitroId,
                              LocalDateTime fechaHoraInicio, String estado) {
    public static PartidoResponse desdeEntidad(Partido partido) {
        return new PartidoResponse(partido.getId(), partido.getTorneoId(), partido.getNumeroFecha(),
                partido.getEquipoLocalId(), partido.getEquipoVisitanteId(), partido.getCanchaId(),
                partido.getArbitroId(), partido.getFechaHoraInicio(), partido.getEstado());
    }
}
