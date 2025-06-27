package com.example.tiendaapp2.ui.compra;

public class CompraDetalle {
    private int idProducto;
    private String nombreProducto;
    private double cantidad;
    private double precio;

    public CompraDetalle(int idProducto, String nombreProducto, double cantidad, double precio) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precio = precio;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public double getPrecio() {
        return precio;
    }
}
