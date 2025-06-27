package com.example.tiendaapp2.ui.venta;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.example.tiendaapp2.R;

import java.util.ArrayList;

public class VentaDetalleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VentaDetalle> lista;

    public VentaDetalleAdapter(Context context, ArrayList<VentaDetalle> lista) {
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

    static class ViewHolder {
        TextView txtProducto, txtCantidad, txtPrecio, txtSubtotal;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VentaDetalleAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_venta_detalle, parent, false);
            holder = new VentaDetalleAdapter.ViewHolder();
            holder.txtProducto = convertView.findViewById(R.id.txtVentaProducto);
            holder.txtCantidad = convertView.findViewById(R.id.txtVentaCantidad);
            holder.txtPrecio = convertView.findViewById(R.id.txtVentaPrecio);
            holder.txtSubtotal = convertView.findViewById(R.id.txtVentaSubtotal);
            convertView.setTag(holder);
        } else {
            holder = (VentaDetalleAdapter.ViewHolder) convertView.getTag();
        }

        VentaDetalle detalle = lista.get(position);
        holder.txtProducto.setText(detalle.getNombreProducto());
        holder.txtCantidad.setText("Cantidad: " + detalle.getCantidad());
        holder.txtPrecio.setText("Precio: S/ " + detalle.getPrecioVenta());
        holder.txtSubtotal.setText("Subtotal: S/ " + detalle.getSubtotal());

        return convertView;
    }
}
