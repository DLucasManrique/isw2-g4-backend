package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "torneo")
@Data
public class Torneo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 80)
    private String nombre;
    @Column(nullable = false)
    private Integer anio;
    @Column(nullable = false)
    private Boolean activo;
}
