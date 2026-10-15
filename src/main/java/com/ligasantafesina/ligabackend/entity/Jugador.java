package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "jugador")
public class Jugador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "equipo_id", nullable = false, updatable = false)
    private Integer equipoId;

    @Column(nullable = false, length = 60)
    private String nombre;

    @Column(nullable = false, length = 60)
    private String apellido;

    @Column(nullable = false, unique = true, length = 10)
    private String dni;

    @Column(name = "numero_camiseta")
    private Integer numeroCamiseta;

    @Column(nullable = false)
    private boolean activo = true;

    protected Jugador() {
        // Constructor que necesita JPA para leer desde la base.
    }

    public Jugador(Integer equipoId, String nombre, String apellido,
                   String dni, Integer numeroCamiseta) {
        this.equipoId = equipoId;
        actualizar(nombre, apellido, dni, numeroCamiseta);
    }

    public void actualizar(String nombre, String apellido,
                           String dni, Integer numeroCamiseta) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.numeroCamiseta = numeroCamiseta;
    }

    public void darDeBaja() {
        this.activo = false;
    }

    public Integer getId() {
        return id;
    }

    public Integer getEquipoId() {
        return equipoId;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getDni() {
        return dni;
    }

    public Integer getNumeroCamiseta() {
        return numeroCamiseta;
    }

    public boolean isActivo() {
        return activo;
    }
}