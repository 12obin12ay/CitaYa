package com.codelabs.citaya.database;

public class Horario {

    private int id;
    private int medicoId;
    private String fecha;
    private String hora;
    private String estado;
    private String ubicacion;

    public Horario(int medicoId,
                   String fecha,
                   String hora,
                   String estado,
                   String ubicacion) {

        this.medicoId = medicoId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
        this.ubicacion = ubicacion;
    }

    //CONSTRUCTOR PARA LEER DE LA BD
    public Horario(int id,
                   int medicoId,
                   String fecha,
                   String hora,
                   String estado) {

        this.id = id;
        this.medicoId = medicoId;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = estado;
    }


    public int getId() {
        return id;
    }

    public int getMedicoId() {
        return medicoId;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getEstado() {
        return estado;
    }

    public String getUbicacion() {
        return ubicacion;
    }
}
