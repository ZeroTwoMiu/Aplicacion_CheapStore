package com.example.tiendaapp2.ui.venta;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tiendaapp2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class VentaListarFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView lstVentas;
    Button btnRegistrarVenta;
    SwipeRefreshLayout swipeRefreshVentas;

    ArrayList<Venta> listaVentas = new ArrayList<>();
    VentaAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_venta_listar, container, false);

        lstVentas = v.findViewById(R.id.lstVentas);
        btnRegistrarVenta = v.findViewById(R.id.btnRegistrarVentaDesdeLista);
        swipeRefreshVentas = v.findViewById(R.id.swipeRefreshVentas);

        adapter = new VentaAdapter(getActivity(), listaVentas);
        lstVentas.setAdapter(adapter);

        swipeRefreshVentas.setOnRefreshListener(this);

        btnRegistrarVenta.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_venta_agregar);
        });

        lstVentas.setOnItemClickListener((parent, view1, position, id) -> {
            Venta venta = listaVentas.get(position);
            Bundle bundle = new Bundle();
            bundle.putInt("idVenta", venta.getIdVenta());
            bundle.putString("cliente", venta.getCliente());
            bundle.putString("empleado", venta.getEmpleado());
            bundle.putString("fecha", venta.getFecha());

            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_venta_detalle, bundle);
        });

        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listarVentas();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("VENTA_FRAGMENT", "onResume llamado");
        listarVentas();
    }

    private void listarVentas() {
        swipeRefreshVentas.setRefreshing(true);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "venta_mostrar.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    listaVentas.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Venta venta = new Venta(
                                obj.getInt("id_venta"),
                                obj.getString("cliente"),
                                obj.getString("empleado"),
                                obj.getString("comprobante"),
                                obj.getString("num_venta"),
                                obj.getString("fh_venta")
                        );
                        listaVentas.add(venta);
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    Log.e("VENTA_FRAGMENT", "Error JSON: " + e.getMessage());
                    Toast.makeText(getContext(), "Error al procesar ventas", Toast.LENGTH_SHORT).show();
                } finally {
                    swipeRefreshVentas.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("VENTA_FRAGMENT", "Error HTTP: " + error.getMessage());
                Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                swipeRefreshVentas.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh() {
        listarVentas();
    }
}

