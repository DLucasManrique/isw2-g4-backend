package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "evento_partido")
public class EventoPartido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "partido_id", nullable = false)
    private Long partidoId;

    @Column(name = "jugador_id", nullable = false)
    private Long jugadorId;

    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;

    @Column(nullable = false)
    private Integer minuto;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPartidoId() { return partidoId; }
    public void setPartidoId(Long partidoId) { this.partidoId = partidoId; }

    public Long getJugadorId() { return jugadorId; }
    public void setJugadorId(Long jugadorId) { this.jugadorId = jugadorId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Integer getMinuto() { return minuto; }
    public void setMinuto(Integer minuto) { this.minuto = minuto; }
}
