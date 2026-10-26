package com.ligasantafesina.ligabackend.dto;

public class EventoResponse {
    private Long id;
    private Long partidoId;
    private Long jugadorId;
    private String tipo;
    private Integer minuto;

    public EventoResponse(Long id, Long partidoId, Long jugadorId, String tipo, Integer minuto) {
        this.id = id;
        this.partidoId = partidoId;
        this.jugadorId = jugadorId;
        this.tipo = tipo;
        this.minuto = minuto;
    }

    public Long getId() { return id; }
    public Long getPartidoId() { return partidoId; }
    public Long getJugadorId() { return jugadorId; }
    public String getTipo() { return tipo; }
    public Integer getMinuto() { return minuto; }
}
