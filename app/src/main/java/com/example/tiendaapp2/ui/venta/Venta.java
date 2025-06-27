package com.example.tiendaapp2.ui.venta;

public class Venta {
    private int idVenta;
    private String cliente;
    private String empleado;
    private String comprobante;
    private String numVenta;
    private String fecha;

    public Venta(int idVenta, String cliente, String empleado, String comprobante, String numVenta, String fecha) {
        this.idVenta = idVenta;
        this.cliente = cliente;
        this.empleado = empleado;
        this.comprobante = comprobante;
        this.numVenta = numVenta;
        this.fecha = fecha;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public String getCliente() {
        return cliente;
    }

    public String getEmpleado() {
        return empleado;
    }

    public String getComprobante() {
        return comprobante;
    }

    public String getNumVenta() {
        return numVenta;
    }

    public String getFecha() {
        return fecha;
    }
}