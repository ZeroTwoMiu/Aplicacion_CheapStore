package com.example.tiendaapp2.ui.compra;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;
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
import com.loopj.android.http.*;

import org.json.*;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class CompraListarFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    ListView lstCompras;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<Compra> lista = new ArrayList<>();
    CompraAdapter adapter;
    Button btnRegistrarCompraDesdeLista;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compra_listar, container, false);
        lstCompras = v.findViewById(R.id.lstCompras);
        swipeRefresh = v.findViewById(R.id.swipeRefreshCompras);
        btnRegistrarCompraDesdeLista = v.findViewById(R.id.btnRegistrarCompraDesdeLista);

        adapter = new CompraAdapter(getActivity(), lista);
        lstCompras.setAdapter(adapter);
        swipeRefresh.setOnRefreshListener(this);

        btnRegistrarCompraDesdeLista.setOnClickListener(view -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_compra_agregar);
        });

        // 🚀 Ir al detalle al hacer clic en una compra
        lstCompras.setOnItemClickListener((parent, view1, position, id) -> {
            Compra compraSeleccionada = lista.get(position);
            int idCompra = compraSeleccionada.getId();
            String numeroCompra = compraSeleccionada.getNumero(); // <- nuevo

            Bundle bundle = new Bundle();
            bundle.putInt("idCompra", idCompra);
            bundle.putString("numeroCompra", numeroCompra); // <- nuevo

            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.nav_compra_detalle, bundle);
        });

        listarCompras();
        return v;
    }

    private void listarCompras() {
        swipeRefresh.setRefreshing(true);
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "compra_mostrar.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONArray array = new JSONArray(new String(bytes));
                    lista.clear();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject obj = array.getJSONObject(j);
                        Compra compra = new Compra(
                                obj.getInt("id_compra"),
                                obj.getString("proveedor"),       // CAMBIADO
                                obj.getString("empleado"),        // CAMBIADO
                                obj.getString("comprobante"),     // CAMBIADO
                                obj.getString("num_compra"),
                                obj.getString("fh_compra")
                        );
                        lista.add(compra);
                    }
                    adapter.notifyDataSetChanged();
                    swipeRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(getContext(), "Error al procesar JSON", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                swipeRefresh.setRefreshing(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        listarCompras();
    }

}
