package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "arbitro")
@Data
public class Arbitro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false, length = 60)
    private String nombre;
    @Column(nullable = false, length = 60)
    private String apellido;
    @Column(nullable = false, unique = true, length = 10)
    private String dni;
    @Column(nullable = false)
    private Boolean activo;
}
