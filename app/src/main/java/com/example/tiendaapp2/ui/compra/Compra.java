package com.example.tiendaapp2.ui.compra;

public class Compra {
    int id;
    String proveedor;
    String empleado;
    String comprobante;
    String numero;
    String fecha;

    public Compra(int id, String proveedor, String empleado, String comprobante, String numero, String fecha) {
        this.id = id;
        this.proveedor = proveedor;
        this.empleado = empleado;
        this.comprobante = comprobante;
        this.numero = numero;
        this.fecha = fecha;
    }

    public int getId() { return id; }
    public String getProveedor() { return proveedor; }
    public String getEmpleado() { return empleado; }
    public String getComprobante() { return comprobante; }
    public String getNumero() { return numero; }
    public String getFecha() { return fecha; }
}
