package com.example.tiendaapp2.ui.cliente;

public class Cliente {
    private int id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String numeroDocumento;
    private String celular;
    private String email;
    private String direccion;
    private String fechaNacimiento;
    private int estado;

    public Cliente(int id, String nombre, String apellidoPaterno, String apellidoMaterno,
                   String numeroDocumento, String celular, String email,
                   String direccion, String fechaNacimiento, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.apellidoPaterno = apellidoPaterno;
        this.apellidoMaterno = apellidoMaterno;
        this.numeroDocumento = numeroDocumento;
        this.celular = celular;
        this.email = email;
        this.direccion = direccion;
        this.fechaNacimiento = fechaNacimiento;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidoPaterno + " " + apellidoMaterno;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getCelular() {
        return celular;
    }

    public String getEmail() {
        return email;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public int getEstado() {
        return estado;
    }
}
