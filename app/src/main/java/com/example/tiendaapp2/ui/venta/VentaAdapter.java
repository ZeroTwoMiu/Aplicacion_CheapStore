package com.example.tiendaapp2.ui.venta;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.*;
import android.widget.*;

import com.example.tiendaapp2.R;

import java.util.ArrayList;
import java.util.List;

public class VentaAdapter extends BaseAdapter {

    private Activity contexto;
    private ArrayList<Venta> listaVentas;

    public VentaAdapter(Activity contexto, ArrayList<Venta> listaVentas) {
        this.contexto = contexto;
        this.listaVentas = listaVentas;
    }

    @Override
    public int getCount() {
        return listaVentas.size();
    }

    @Override
    public Object getItem(int i) {
        return listaVentas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return listaVentas.get(i).getIdVenta();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = contexto.getLayoutInflater().inflate(R.layout.item_venta, null);

        TextView txtCliente = view.findViewById(R.id.txtClienteVenta);
        TextView txtComprobante = view.findViewById(R.id.txtComprobanteVenta);
        TextView txtFecha = view.findViewById(R.id.txtFechaVenta);
        TextView txtNumero = view.findViewById(R.id.txtNumeroVenta);

        Venta v = listaVentas.get(i);

        txtCliente.setText("Cliente: " + v.getCliente());
        txtComprobante.setText("Comprobante: " + v.getComprobante());
        txtFecha.setText("Fecha: " + v.getFecha());
        txtNumero.setText("N° Venta: " + v.getNumVenta());

        return view;
    }
}

