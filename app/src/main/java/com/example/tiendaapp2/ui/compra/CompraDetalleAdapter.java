package com.example.tiendaapp2.ui.compra;

import android.app.Activity;
import android.view.*;
import android.widget.*;
import com.example.tiendaapp2.R;

import java.util.ArrayList;

public class CompraDetalleAdapter extends BaseAdapter {

    Activity context;
    ArrayList<CompraDetalle> lista;

    public CompraDetalleAdapter(Activity context, ArrayList<CompraDetalle> lista) {
        this.context = context;
        this.lista = lista;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int i) {
        return lista.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = context.getLayoutInflater();
        View item = inflater.inflate(R.layout.item_compra_detalle, null);

        TextView lblProductoDetalle = item.findViewById(R.id.lblProductoDetalle);
        TextView lblCantidadPrecio = item.findViewById(R.id.lblCantidadPrecio);

        CompraDetalle detalle = lista.get(i);
        lblProductoDetalle.setText("Producto: " + detalle.getNombreProducto());
        lblCantidadPrecio.setText("Cantidad: " + detalle.getCantidad() + " | Precio: S/ " + detalle.getPrecio());

        return item;
    }
}
