package com.example.tiendaapp2.ui.proveedor;

import androidx.annotation.NonNull;

public class Proveedor {
    private int id;
    private String nombre;
    private String nDoc;
    private String celular;
    private String email;
    private String direccion;
    private int estado;

    // Constructor completo
    public Proveedor(int id, String nombre, String nDoc, String celular, String email, String direccion, int estado) {
        this.id = id;
        this.nombre = nombre;
        this.nDoc = nDoc;
        this.celular = celular;
        this.email = email;
        this.direccion = direccion;
        this.estado = estado;
    }

    // Constructor simplificado (solo id y nombre)
    public Proveedor(int id, String nombre) {
        this(id, nombre, "", "", "", "", 1);
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getnDoc() {
        return nDoc;
    }

    public void setnDoc(String nDoc) {
        this.nDoc = nDoc;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    @NonNull
    @Override
    public String toString() {
        return nombre;
    }
}