package com.example.tiendaapp2.ui;

public class Item {
    private int id;
    private String nombre;

    public Item(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

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

    @Override
    public String toString() {
        return nombre; // Esto es lo que se mostrará en el Spinner
    }
}
