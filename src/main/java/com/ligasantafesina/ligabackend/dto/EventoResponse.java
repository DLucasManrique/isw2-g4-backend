package com.ligasantafesina.ligabackend.dto;

public class EventoResponse {
    private Long id;
    private Long partidoId;
    private Long jugadorId;
    private Long equipoId;
    private String tipoEvento;
    private Integer minuto;
    private String observacion;

    public EventoResponse(Long id, Long partidoId, Long jugadorId, Long equipoId, String tipoEvento, Integer minuto, String observacion) {
        this.id = id;
        this.partidoId = partidoId;
        this.jugadorId = jugadorId;
        this.equipoId = equipoId;
        this.tipoEvento = tipoEvento;
        this.minuto = minuto;
        this.observacion = observacion;
    }

    // Getters
    public Long getId() { return id; }
    public Long getPartidoId() { return partidoId; }
    public Long getJugadorId() { return jugadorId; }
    public Long getEquipoId() { return equipoId; }
    public String getTipoEvento() { return tipoEvento; }
    public Integer getMinuto() { return minuto; }
    public String getObservacion() { return observacion; }
}