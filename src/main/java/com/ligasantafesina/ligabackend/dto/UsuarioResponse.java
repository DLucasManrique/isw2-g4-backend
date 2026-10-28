package com.ligasantafesina.ligabackend.dto;

public class UsuarioResponse {
    private Long id;
    private String username;
    private String nombreCompleto;
    private String rol;
    private Boolean activo;
    private Integer equipoId;

    public UsuarioResponse(Long id, String username, String nombreCompleto, String rol, Boolean activo, Integer equipoId) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.rol = rol;
        this.activo = activo;
        this.equipoId = equipoId;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getRol() {
        return rol;
    }

    public Boolean getActivo() {
        return activo;
    }

    public Integer getEquipoId() {
        return equipoId;
    }
}
