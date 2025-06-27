package com.example.tiendaapp2.ui.venta;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.venta.VentaDetalle;
import com.example.tiendaapp2.ui.venta.VentaDetalleAdapter;
import com.loopj.android.http.*;

import org.json.*;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static com.example.tiendaapp2.Login.servidor;

public class VentaDetalleFragment extends Fragment {

    ListView lstDetalle;
    TextView tvClienteVenta, tvEmpleadoVenta, tvFechaVenta;
    ArrayList<VentaDetalle> listaDetalle = new ArrayList<>();
    VentaDetalleAdapter adapter;

    int idVenta; // Se pasará desde el fragmento anterior

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_venta_detalle, container, false);

        lstDetalle = v.findViewById(R.id.lvVentaDetalle);
        tvClienteVenta = v.findViewById(R.id.tvClienteVenta);
        tvEmpleadoVenta = v.findViewById(R.id.tvEmpleadoVenta);
        tvFechaVenta = v.findViewById(R.id.tvFechaVenta);

        adapter = new VentaDetalleAdapter(getActivity(), listaDetalle);
        lstDetalle.setAdapter(adapter);

        if (getArguments() != null) {
            idVenta = getArguments().getInt("idVenta", -1);
            String cliente = getArguments().getString("cliente", "Desconocido");
            String empleado = getArguments().getString("empleado", "Desconocido");
            String fecha = getArguments().getString("fecha", "Sin fecha");

            tvClienteVenta.setText("Cliente: " + cliente);
            tvEmpleadoVenta.setText("Empleado: " + empleado);
            tvFechaVenta.setText("Fecha: " + fecha);

            if (idVenta != -1) {
                cargarDetalleVenta(idVenta);
            } else {
                Toast.makeText(getContext(), "ID de venta no válido", Toast.LENGTH_SHORT).show();
            }
        }

        return v;
    }


    private void cargarDetalleVenta(int idVenta) {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        params.put("idVenta", idVenta);

        client.get(servidor + "venta_detalle.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    listaDetalle.clear();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        VentaDetalle detalle = new VentaDetalle(
                                obj.getInt("id_producto"),
                                obj.getString("nom_producto"),
                                obj.getDouble("cant_venta_detalle"),
                                obj.getDouble("pve_venta_detalle")
                        );
                        listaDetalle.add(detalle);
                    }

                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar detalle", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar detalle", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
