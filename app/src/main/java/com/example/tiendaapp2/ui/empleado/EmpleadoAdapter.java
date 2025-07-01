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
        TextView tvApellidoPaterno = convertView.findViewById(R.id.tvApat);
        TextView tvApellidoMaterno = convertView.findViewById(R.id.tvAmat);
        TextView tvDni = convertView.findViewById(R.id.tvDni);
        TextView tvCelular = convertView.findViewById(R.id.tvCelular);
        TextView tvEmail = convertView.findViewById(R.id.tvEmail);
        TextView tvDireccion = convertView.findViewById(R.id.tvDireccion);
        TextView tvFechaNacimiento = convertView.findViewById(R.id.tvFechaNac);
        TextView tvUsuario = convertView.findViewById(R.id.tvUsuario);
        TextView tvPassword = convertView.findViewById(R.id.tvPassword);
        TextView tvCargo = convertView.findViewById(R.id.tvCargo);
        TextView tvEstado = convertView.findViewById(R.id.tvEstado);

        Button btnEditar = convertView.findViewById(R.id.btnEditar);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);

        // Acciones
        btnEditar.setOnClickListener(v -> EditarEmpleado(empleado.getId(), context));
        btnEliminar.setOnClickListener(v -> EliminarEmpleado(empleado.getId(), context));

        // Valores
        tvId.setText(String.valueOf(empleado.getId()));
        tvNombre.setText("Nombre: " + empleado.getNombre());
        tvApellidoPaterno.setText("Apellido Paterno: " + empleado.getApellidoPaterno());
        tvApellidoMaterno.setText("Apellido Materno: " + empleado.getApellidoMaterno());
        tvDni.setText("DNI: " + empleado.getNumDocumento());
        tvCelular.setText("Celular: " + empleado.getCelular());
        tvEmail.setText("Email: " + empleado.getCorreo());
        tvDireccion.setText("Dirección: " + empleado.getDireccion());
        tvFechaNacimiento.setText("Fecha de nacimiento: " + empleado.getFechaNacimiento());
        tvUsuario.setText("Usuario: " + empleado.getUsuario());
        tvPassword.setText("Contraseña: " + empleado.getContrasena());
        tvCargo.setText("Cargo: " + empleado.getIdCargo());
        tvEstado.setText("Estado: " + empleado.getEstado());

        return convertView;
    }
}
