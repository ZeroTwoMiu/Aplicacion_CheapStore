package com.example.tiendaapp2.ui.producto;

import com.itextpdf.text.pdf.PdfPCell;

public class Producto {

    int id;
    String codigo;
    String nombre;
    String categoria;
    String marca;
    String descripcion;
    double pcompra;
    double pventa;
    double stock;

    public Producto(int id, String codigo, String nombre, String categoria, String marca, String descripcion, double pcompra, double pventa, double stock) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.categoria = categoria;
        this.marca = marca;
        this.descripcion = descripcion;
        this.pcompra = pcompra;
        this.pventa = pventa;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPcompra() {
        return pcompra;
    }

    public void setPcompra(double pcompra) {
        this.pcompra = pcompra;
    }

    public double getPventa() {
        return pventa;
    }

    public void setPventa(double pventa) {
        this.pventa = pventa;
    }

    public double getStock() {
        return stock;
    }

    public void setStock(double stock) {
        this.stock = stock;
    }
}
