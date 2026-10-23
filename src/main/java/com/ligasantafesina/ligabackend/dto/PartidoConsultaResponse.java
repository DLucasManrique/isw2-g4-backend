package com.ligasantafesina.ligabackend.dto;

import java.time.LocalDateTime;

public record PartidoConsultaResponse(Integer id, Integer torneoId, Integer numeroFecha,
                                      Integer equipoLocalId, Integer equipoVisitanteId,
                                      Integer canchaId, Integer arbitroId,
                                      LocalDateTime fechaHoraInicio, String estado,
                                      Integer golesLocal, Integer golesVisitante) {}
