package com.example.tiendaapp2.ui.compra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.tiendaapp2.R;

import java.util.List;

public class ProductoCompraAdapter extends ArrayAdapter<ProductoCompraItem> {

    private final Context context;
    private final List<ProductoCompraItem> productos;

    public ProductoCompraAdapter(Context context, List<ProductoCompraItem> productos) {
        super(context, 0, productos);
        this.context = context;
        this.productos = productos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductoCompraItem item = productos.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_producto_compra, parent, false);
        }

        TextView txtNombre = convertView.findViewById(R.id.txtNombreProducto);
        TextView txtCantidad = convertView.findViewById(R.id.txtCantidadProducto);
        TextView txtPrecio = convertView.findViewById(R.id.txtPrecioProducto);
        TextView txtSubtotal = convertView.findViewById(R.id.txtSubtotalProducto);

        txtNombre.setText(item.getNombreProducto());
        txtCantidad.setText("Cant: " + item.getCantidad());
        txtPrecio.setText("S/ " + String.format("%.2f", item.getPrecio()));
        txtSubtotal.setText("Subtotal: S/ " + String.format("%.2f", item.getSubtotal()));

        return convertView;
    }
}
