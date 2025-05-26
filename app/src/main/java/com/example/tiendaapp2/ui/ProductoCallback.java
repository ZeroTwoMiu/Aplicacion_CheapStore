package com.example.tiendaapp2.ui;

import com.example.tiendaapp2.ui.producto.Producto;

import java.util.ArrayList;

public interface ProductoCallback {
    void onProductosObtenidos(ArrayList<Producto> productosList);
    void onError(String mensajeError);
}
