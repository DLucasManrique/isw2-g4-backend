package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "equipo", uniqueConstraints = @UniqueConstraint(
        name = "uq_equipo_nombre_torneo", columnNames = {"torneo_id", "nombre"}))
public class Equipo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "torneo_id", nullable = false)
    private Integer torneoId;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(length = 80)
    private String barrio;

    @Column(nullable = false)
    private boolean activo = true;

    protected Equipo() {}

    public Equipo(Integer torneoId, String nombre, String barrio) {
        actualizar(torneoId, nombre, barrio);
    }

    public void actualizar(Integer torneoId, String nombre, String barrio) {
        this.torneoId = torneoId;
        this.nombre = nombre;
        this.barrio = barrio;
    }

    public void darDeBaja() { this.activo = false; }
    public Integer getId() { return id; }
    public Integer getTorneoId() { return torneoId; }
    public String getNombre() { return nombre; }
    public String getBarrio() { return barrio; }
    public boolean isActivo() { return activo; }
}
