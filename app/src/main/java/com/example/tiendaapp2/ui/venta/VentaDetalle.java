package com.example.tiendaapp2.ui.venta;

public class VentaDetalle {
    private int idProducto;
    private String nombreProducto;
    private double cantidad;
    private double precioVenta;

    public VentaDetalle(int idProducto, String nombreProducto, double cantidad, double precioVenta) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioVenta = precioVenta;
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

    public double getPrecioVenta() {
        return precioVenta;
    }

    public double getSubtotal() {
        return cantidad * precioVenta;
    }
}
