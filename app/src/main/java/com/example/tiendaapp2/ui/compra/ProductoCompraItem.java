package com.example.tiendaapp2.ui.compra;

public class ProductoCompraItem {
    private int productoId;
    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public ProductoCompraItem(int productoId, String nombreProducto, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public int getProductoId() {
        return productoId;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getSubtotal() {
        return cantidad * precioUnitario;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    // Alias para evitar errores si antes usabas getPrecio()
    public double getPrecio() {
        return getPrecioUnitario();
    }
}
