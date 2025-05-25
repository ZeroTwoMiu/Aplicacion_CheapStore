package com.example.tiendaapp2.ui.producto;

import static com.example.tiendaapp2.ui.producto.ProductoFragment.EditarProducto;
import static com.example.tiendaapp2.ui.producto.ProductoFragment.EliminarProducto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tiendaapp2.R;

import java.util.List;

public class ProductoAdapter extends BaseAdapter {

    private Context context;
    private List<Producto> productos;

    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflar el layout del item
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_producto, parent, false);
        }

        // Obtener los datos del producto
        Producto producto = productos.get(position);

        // Referenciar las vistas
        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvCod = convertView.findViewById(R.id.tvCodigo);
        TextView tvNom = convertView.findViewById(R.id.tvNombre);
        TextView tvCat = convertView.findViewById(R.id.tvCategoria);
        TextView tvMar = convertView.findViewById(R.id.tvMarca);
        TextView tvDes = convertView.findViewById(R.id.tvDescripcion);
        TextView tvPcomp = convertView.findViewById(R.id.tvPreccomp);
        TextView tvPvent = convertView.findViewById(R.id.tvPrecvent);
        TextView tvStock = convertView.findViewById(R.id.tvStock);
        Button btnEditar = convertView.findViewById(R.id.btnEditar);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Editar "+producto.getId(),Toast.LENGTH_LONG).show();
                EditarProducto(producto.getId(), context);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Eliminar "+producto.getId(),Toast.LENGTH_LONG).show();
               EliminarProducto(producto.getId(), context);
            }
        });

        // Establecer los valores
        tvId.setText(String.valueOf(producto.getId()));
        tvCod.setText("Código: "+producto.getCodigo());
        tvNom.setText("Nombre: "+producto.getNombre());
        tvCat.setText("Categoría: "+producto.getCategoria());
        tvMar.setText("Marca:"+producto.getMarca());
        tvDes.setText("Descripción: "+producto.getDescripcion());
        tvPcomp.setText("Precio Compra: "+String.valueOf(producto.getPcompra()));
        tvPvent.setText("Precio Venta: "+String.valueOf(producto.getPventa()));
        tvStock.setText("Stock: "+producto.getStock());

        return convertView;
    }
}
