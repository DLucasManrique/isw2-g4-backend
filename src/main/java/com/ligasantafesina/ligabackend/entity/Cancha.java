package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cancha")
@Data
public class Cancha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, unique = true, length = 60)
    private String nombre;
    @Column(length = 120)
    private String direccion;
    @Column(nullable = false)
    private Boolean activo;
}
