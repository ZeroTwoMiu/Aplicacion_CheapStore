package com.example.tiendaapp2.ui.compra;

public class ProveedorItem {
    private int idProveedor;
    private String nombre;

    public ProveedorItem(int idProveedor, String nombre) {
        this.idProveedor = idProveedor;
        this.nombre = nombre;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public String getNombre() {
        return nombre;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
