package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "torneo")
@Data
public class Torneo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private Integer anio;
    private Boolean activo;
}
