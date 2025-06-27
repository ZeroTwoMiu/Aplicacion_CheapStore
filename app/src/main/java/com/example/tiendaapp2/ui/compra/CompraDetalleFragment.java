package com.example.tiendaapp2.ui.compra;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tiendaapp2.R;
import com.loopj.android.http.*;

import org.json.*;

import java.util.*;

import cz.msebera.android.httpclient.Header;

public class CompraDetalleFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    TextView lblNumeroCompraDetalle;
    ListView lstCompraDetalles;
    SwipeRefreshLayout swipeDetalleCompra;

    ArrayList<CompraDetalle> lista = new ArrayList<>();
    CompraDetalleAdapter adapter;

    int idCompra;
    String numeroCompra;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compra_detalle, container, false);

        lblNumeroCompraDetalle = v.findViewById(R.id.lblNumeroCompraDetalle);
        lstCompraDetalles = v.findViewById(R.id.lstCompraDetalles);
        swipeDetalleCompra = v.findViewById(R.id.swipeDetalleCompra);

        adapter = new CompraDetalleAdapter(getActivity(), lista);
        lstCompraDetalles.setAdapter(adapter);
        swipeDetalleCompra.setOnRefreshListener(this);

        if (getArguments() != null) {
            idCompra = getArguments().getInt("idCompra");
            numeroCompra = getArguments().getString("numeroCompra");
            lblNumeroCompraDetalle.setText("Número de Compra: " + numeroCompra);
            cargarDetallesCompra();
        }

        return v;
    }

    private void cargarDetallesCompra() {
        swipeDetalleCompra.setRefreshing(true);
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(servidor + "compra_detalle_mostrar.php?idCompra=" + idCompra, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    lista.clear();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        CompraDetalle detalle = new CompraDetalle(
                                obj.getInt("id_producto"),
                                obj.getString("nom_producto"),
                                obj.getDouble("cantidad"),
                                obj.getDouble("precio")
                        );
                        lista.add(detalle);
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error al procesar JSON", Toast.LENGTH_SHORT).show();
                }
                swipeDetalleCompra.setRefreshing(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                swipeDetalleCompra.setRefreshing(false);
                Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRefresh() {
        cargarDetallesCompra();
    }
}
