package com.example.tiendaapp2.ui.cliente;

import static com.example.tiendaapp2.ui.cliente.ClienteFragment.editarCliente;
import static com.example.tiendaapp2.ui.cliente.ClienteFragment.eliminarCliente;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.example.tiendaapp2.R;

import java.util.List;

public class ClienteAdapter extends BaseAdapter {

    private Context context;
    private List<Cliente> clientes;

    public ClienteAdapter(Context context, List<Cliente> clientes) {
        this.context = context;
        this.clientes = clientes;
    }

    @Override
    public int getCount() {
        return clientes.size();
    }

    @Override
    public Object getItem(int position) {
        return clientes.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_cliente, parent, false);
        }

        Cliente cliente = clientes.get(position);

        // Referencias
        TextView tvId = convertView.findViewById(R.id.tvIdCliente);
        TextView tvNombre = convertView.findViewById(R.id.tvNombreCliente);
        TextView tvApat = convertView.findViewById(R.id.tvApellidoPaternoCliente);
        TextView tvAmat = convertView.findViewById(R.id.tvApellidoMaternoCliente);
        TextView tvDoc = convertView.findViewById(R.id.tvDniCliente);
        TextView tvCel = convertView.findViewById(R.id.tvCelularCliente);
        TextView tvCorreo = convertView.findViewById(R.id.tvEmailCliente);
        TextView tvDir = convertView.findViewById(R.id.tvDireccionCliente);
        TextView tvFn = convertView.findViewById(R.id.tvFechaNacCliente);
        TextView tvEst = convertView.findViewById(R.id.tvEstadoCliente);

        Button btnEditar = convertView.findViewById(R.id.btnEditarCliente);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminarCliente);

        // Mostrar datos
        tvId.setText(String.valueOf(cliente.getId()));
        tvNombre.setText("Nombre: " + cliente.getNombreCompleto());
        tvDoc.setText("N° Documento: " + cliente.getNumeroDocumento());
        tvCel.setText("Celular: " + cliente.getCelular());
        tvCorreo.setText("Correo: " + cliente.getEmail());
        tvDir.setText("Dirección: " + cliente.getDireccion());
        tvFn.setText("Fecha Nacimiento: " + cliente.getFechaNacimiento());
        tvEst.setText("Estado: " + (cliente.getEstado() == 1 ? "Activo" : "Inactivo"));

        // Acciones
        btnEditar.setOnClickListener(v -> editarCliente(cliente.getId(), context));
        btnEliminar.setOnClickListener(v -> eliminarCliente(cliente.getId(), context));

        return convertView;
    }
}
