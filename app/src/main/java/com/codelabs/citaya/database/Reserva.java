package com.codelabs.citaya.database;

public class Reserva {

    private int id;
    private int usuarioId;
    private Integer horarioId; // Puede ser null si el horario fue borrado
    private String estado;
    private String fechaReserva;
    
    // Campos de backup para historial
    private String doctorNombre;
    private String especialidadNombre;
    private String fechaCita;
    private String horaCita;
    private String ubicacionCita;

    // Constructor para CREAR
    public Reserva(int usuarioId, Integer horarioId, String estado, String fechaReserva,
                   String doctorNombre, String especialidadNombre, String fechaCita,
                   String horaCita, String ubicacionCita) {
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
        this.doctorNombre = doctorNombre;
        this.especialidadNombre = especialidadNombre;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.ubicacionCita = ubicacionCita;
    }

    // Constructor para LEER desde BD
    public Reserva(int id, int usuarioId, Integer horarioId, String estado, String fechaReserva,
                   String doctorNombre, String especialidadNombre, String fechaCita,
                   String horaCita, String ubicacionCita) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.horarioId = horarioId;
        this.estado = estado;
        this.fechaReserva = fechaReserva;
        this.doctorNombre = doctorNombre;
        this.especialidadNombre = especialidadNombre;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.ubicacionCita = ubicacionCita;
    }

    // Getters y Setters
    public int getId() { return id; }
    public int getUsuarioId() { return usuarioId; }
    public Integer getHorarioId() { return horarioId; }
    public String getEstado() { return estado; }
    public String getFechaReserva() { return fechaReserva; }
    public String getDoctorNombre() { return doctorNombre; }
    public String getEspecialidadNombre() { return especialidadNombre; }
    public String getFechaCita() { return fechaCita; }
    public String getHoraCita() { return horaCita; }
    public String getUbicacionCita() { return ubicacionCita; }
    
    public void setDoctorNombre(String doctorNombre) { this.doctorNombre = doctorNombre; }
    public void setEspecialidadNombre(String especialidadNombre) { this.especialidadNombre = especialidadNombre; }
    public void setFechaCita(String fechaCita) { this.fechaCita = fechaCita; }
    public void setHoraCita(String horaCita) { this.horaCita = horaCita; }
    public void setUbicacionCita(String ubicacionCita) { this.ubicacionCita = ubicacionCita; }
}
