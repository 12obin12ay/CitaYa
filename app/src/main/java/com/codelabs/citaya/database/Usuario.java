package com.codelabs.citaya.database;

public class Usuario {

    private int id;

    private String nombre;

    private String dni;

    private String telefono;

    private String correo;

    private String password;

    private String sexo;

    //CONSTRUCTOR
    public Usuario(String nombre,
                   String dni,
                   String telefono,
                   String correo,
                   String password,
                   String sexo) {

        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.password = password;
        this.sexo = sexo;
    }

    //CONSTRUCTOR
    public Usuario(int id,
                   String nombre,
                   String dni,
                   String telefono,
                   String correo,
                   String password,
                   String sexo) {

        this.id = id;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.password = password;
        this.sexo = sexo;
    }


    //METODOS GETTERS
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDni() {
        return dni;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public String getPassword() {
        return password;
    }

    public String getSexo() {
        return sexo;
    }


    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    //METODOS SETTERS
    public void setDni(String dni) {
        this.dni = dni;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }
}
