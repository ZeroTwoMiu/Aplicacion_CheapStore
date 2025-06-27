package com.example.tiendaapp2.ui.compra;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.BaseAdapter;
import com.example.tiendaapp2.R;
import java.util.ArrayList;

public class CompraAdapter extends BaseAdapter {
    Activity context;
    ArrayList<Compra> lista;

    public CompraAdapter(Activity context, ArrayList<Compra> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() { return lista.size(); }

    @Override
    public Object getItem(int i) { return lista.get(i); }

    @Override
    public long getItemId(int i) { return i; }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.item_compra, null);

        TextView lblProveedor = item.findViewById(R.id.lblProveedor);
        TextView lblEmpleado = item.findViewById(R.id.lblEmpleado);
        TextView lblComprobante = item.findViewById(R.id.lblComprobante);
        TextView lblNumero = item.findViewById(R.id.lblNumero);
        TextView lblFecha = item.findViewById(R.id.lblFecha);

        Compra c = lista.get(i);
        lblProveedor.setText("Proveedor: " + c.getProveedor());
        lblEmpleado.setText("Empleado: " + c.getEmpleado());
        lblComprobante.setText("Comprobante: " + c.getComprobante());
        lblNumero.setText("N°: " + c.getNumero());
        lblFecha.setText("Fecha: " + c.getFecha());

        return item;
    }
}
