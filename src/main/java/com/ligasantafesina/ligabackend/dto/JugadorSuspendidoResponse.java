package com.ligasantafesina.ligabackend.dto;

public class JugadorSuspendidoResponse {

    private Long jugadorId;
    private String nombre;
    private String apellido;
    private String equipoNombre;
    private String motivo; // Ej: "Acumulación de 5 tarjetas amarillas", "Tarjeta Roja Directa"
    private Integer fechasSancion; // Cantidad de fechas suspendido (ej: 1)

    public JugadorSuspendidoResponse() {}

    public JugadorSuspendidoResponse(Long jugadorId, String nombre, String apellido, String equipoNombre, String motivo, Integer fechasSancion) {
        this.jugadorId = jugadorId;
        this.nombre = nombre;
        this.apellido = apellido;
        this.equipoNombre = equipoNombre;
        this.motivo = motivo;
        this.fechasSancion = fechasSancion;
    }

    // Getters y Setters
    public Long getJugadorId() { return jugadorId; }
    public void setJugadorId(Long jugadorId) { this.jugadorId = jugadorId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEquipoNombre() { return equipoNombre; }
    public void setEquipoNombre(String equipoNombre) { this.equipoNombre = equipoNombre; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public Integer getFechasSancion() { return fechasSancion; }
    public void setFechasSancion(Integer fechasSancion) { this.fechasSancion = fechasSancion; }
}