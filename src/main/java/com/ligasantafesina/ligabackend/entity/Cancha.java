package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "cancha")
@Data
public class Cancha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String direccion;
}