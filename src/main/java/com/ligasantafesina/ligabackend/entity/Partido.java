package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "partido")
public class Partido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "torneo_id", nullable = false)
    private Integer torneoId;

    @Column(name = "numero_fecha", nullable = false)
    private Integer numeroFecha;

    @Column(name = "equipo_local_id", nullable = false)
    private Integer equipoLocalId;

    @Column(name = "equipo_visitante_id", nullable = false)
    private Integer equipoVisitanteId;

    @Column(name = "cancha_id", nullable = false)
    private Integer canchaId;

    @Column(name = "arbitro_id", nullable = false)
    private Integer arbitroId;

    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @Column(nullable = false, length = 20)
    private String estado = "PROGRAMADO";

    protected Partido() {}

    public Partido(Integer torneoId, Integer numeroFecha, Integer equipoLocalId,
                   Integer equipoVisitanteId, Integer canchaId, Integer arbitroId,
                   LocalDateTime fechaHoraInicio) {
        this.torneoId = torneoId;
        this.numeroFecha = numeroFecha;
        this.equipoLocalId = equipoLocalId;
        this.equipoVisitanteId = equipoVisitanteId;
        this.canchaId = canchaId;
        this.arbitroId = arbitroId;
        this.fechaHoraInicio = fechaHoraInicio;
    }

    public Integer getId() { return id; }
    public Integer getTorneoId() { return torneoId; }
    public Integer getNumeroFecha() { return numeroFecha; }
    public Integer getEquipoLocalId() { return equipoLocalId; }
    public Integer getEquipoVisitanteId() { return equipoVisitanteId; }
    public Integer getCanchaId() { return canchaId; }
    public Integer getArbitroId() { return arbitroId; }
    public LocalDateTime getFechaHoraInicio() { return fechaHoraInicio; }
    public String getEstado() { return estado; }
}
