package com.ligasantafesina.ligabackend.dto;

public class EventoRequest {
    private Long partidoId;
    private Long jugadorId;
    private String tipo;
    private Integer minuto;

    public Long getPartidoId() { return partidoId; }
    public void setPartidoId(Long partidoId) { this.partidoId = partidoId; }

    public Long getJugadorId() { return jugadorId; }
    public void setJugadorId(Long jugadorId) { this.jugadorId = jugadorId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getMinuto() { return minuto; }
    public void setMinuto(Integer minuto) { this.minuto = minuto; }
}
