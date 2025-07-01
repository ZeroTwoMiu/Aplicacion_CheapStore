package com.example.tiendaapp2.ui.proveedor;

import static com.example.tiendaapp2.ui.proveedor.ProveedorFragment.editarProveedor;
import static com.example.tiendaapp2.ui.proveedor.ProveedorFragment.eliminarProveedor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.tiendaapp2.R;

import java.util.List;

public class ProveedorAdapter extends BaseAdapter {

    private Context context;
    private List<Proveedor> proveedores;

    public ProveedorAdapter(Context context, List<Proveedor> proveedores) {
        this.context = context;
        this.proveedores = proveedores;
    }

    @Override
    public int getCount() {
        return proveedores.size();
    }

    @Override
    public Object getItem(int position) {
        return proveedores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_proveedor, parent, false);
        }

        Proveedor proveedor = proveedores.get(position);

        // Referencias
        TextView tvId = convertView.findViewById(R.id.tvIdProveedor);
        TextView tvNombre = convertView.findViewById(R.id.tvNombreProveedor);
        TextView tvDni = convertView.findViewById(R.id.tvDniProveedor);
        TextView tvEmail = convertView.findViewById(R.id.tvEmailProveedor);
        Button btnEditar = convertView.findViewById(R.id.btnEditarProveedor);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminarProveedor);

        // Datos
        tvId.setText(String.valueOf(proveedor.getId()));
        tvNombre.setText("Nombre: " + proveedor.getNombre());
        tvDni.setText("N° Doc: " + proveedor.getnDoc());
        tvEmail.setText("Email: " + proveedor.getEmail());

        // Acciones
        btnEditar.setOnClickListener(v -> editarProveedor(proveedor.getId(), context));
        btnEliminar.setOnClickListener(v -> eliminarProveedor(proveedor.getId(), context));

        return convertView;
    }
}
