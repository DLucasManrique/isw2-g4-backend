package com.ligasantafesina.ligabackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "arbitro")
@Data
public class Arbitro {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String apellido;
    private String dni;
}