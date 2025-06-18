package com.example.tiendaapp2.ui.empleado;

import static com.example.tiendaapp2.ui.empleado.EmpleadoFragment.EditarEmpleado;
import static com.example.tiendaapp2.ui.empleado.EmpleadoFragment.EliminarEmpleado;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.tiendaapp2.R;

import java.util.List;

public class EmpleadoAdapter extends BaseAdapter {

    private Context context;
    private List<Empleado> empleados;

    public EmpleadoAdapter(Context context, List<Empleado> empleados) {
        this.context = context;
        this.empleados = empleados;
    }

    @Override
    public int getCount() {
        return empleados.size();
    }

    @Override
    public Object getItem(int position) {
        return empleados.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_empleado, parent, false);
        }

        Empleado empleado = empleados.get(position);

        // Vistas
        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvNombre = convertView.findViewById(R.id.tvNombres);
        TextView tvDni = convertView.findViewById(R.id.tvDni);
        TextView tvUsuario = convertView.findViewById(R.id.tvUsuario);
        Button btnEditar = convertView.findViewById(R.id.btnEditar);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

        // Acciones
        btnEditar.setOnClickListener(v -> EditarEmpleado(empleado.getId(), context));
        btnEliminar.setOnClickListener(v -> EliminarEmpleado(empleado.getId(), context));

        // Valores
        tvId.setText(String.valueOf(empleado.getId()));
        tvNombre.setText("Nombre: " + empleado.getNombre() + " " + empleado.getApellidoPaterno() + " " + empleado.getApellidoMaterno());
        tvDni.setText("DNI: " + empleado.getNumDocumento());
        tvUsuario.setText("Usuario: " + empleado.getUsuario());

        return convertView;
    }
}
