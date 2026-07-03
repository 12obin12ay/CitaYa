package com.codelabs.citaya.database;

public class Reserva {

    private int id;
    private int usuarioId;
    private int horarioId;
    private String estado;
    private String fechaReserva;

    public Reserva(int usuarioId, int horarioId, String estado, String fechaReserva) {
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
    }


    // Constructor para LEER desde BD (nuevo)
    public Reserva(int id,
                   int usuarioId,
                   int horarioId,
                   String estado,
                   String fechaReserva) {

        this.id = id;
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
    }


    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public int getHorarioId() {
        return horarioId;
    }

    public String getEstado() {
        return estado;
    }

    public String getFechaReserva() {
        return fechaReserva;
    }
}
