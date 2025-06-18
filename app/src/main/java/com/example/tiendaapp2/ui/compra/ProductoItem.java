package com.example.tiendaapp2.ui.compra;

public class ProductoItem {
    private int idProducto;
    private String nombre;

    public ProductoItem(int idProducto, String nombre) {
        this.idProducto = idProducto;
        this.nombre = nombre;
    }

    public int getIdProducto() {
        return idProducto;
    }

    @Override
    public String toString() {
        return nombre;
    }
}