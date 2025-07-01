package com.example.tiendaapp2.ui.proveedor;

import static com.example.tiendaapp2.Login.servidor;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.tiendaapp2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ProveedorFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ListView lista;
    Button agregar;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;

    ProveedorAdapter adapter;
    ArrayList<Proveedor> listaOriginal = new ArrayList<>();
    ArrayList<Proveedor> listaFiltrada = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_proveedor, container, false);

        lista = rootView.findViewById(R.id.lstProveedores);
        agregar = rootView.findViewById(R.id.btnAgregarProveedor);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        searchView = rootView.findViewById(R.id.searchView);

        agregar.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        configurarBuscador();
        listarProveedores();

        return rootView;
    }

    private void configurarBuscador() {
        searchView.setQueryHint("Buscar por nombre");
        searchView.setIconifiedByDefault(false);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filtrarLista(newText);
                return true;
            }
        });
    }

    private void filtrarLista(String texto) {
        listaFiltrada.clear();

        if (texto.isEmpty()) {
            listaFiltrada.addAll(listaOriginal);
        } else {
            for (Proveedor p : listaOriginal) {
                if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(p);
                }
            }
        }

        adapter = new ProveedorAdapter(getActivity(), listaFiltrada);
        lista.setAdapter(adapter);
    }

    private void listarProveedores() {
        String url = servidor + "proveedor_mostrar.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    listaOriginal.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Proveedor proveedor = new Proveedor(
                                obj.getInt("id_proveedor"),
                                obj.getString("nom_proveedor"),
                                obj.getString("ndoc_proveedor"),
                                obj.getString("cel_proveedor"),
                                obj.getString("em_proveedor"),
                                obj.getString("dir_proveedor"),
                                obj.getInt("est_proveedor")
                        );

                        listaOriginal.add(proveedor);
                    }

                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaOriginal);

                    adapter = new ProveedorAdapter(getActivity(), listaFiltrada);
                    lista.setAdapter(adapter);
                    swipeRefreshLayout.setRefreshing(false);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public static void editarProveedor(int id, Context context) {
        if (context instanceof androidx.appcompat.app.AppCompatActivity) {
            Bundle bundle = new Bundle();
            bundle.putInt("idProveedor", id);
            NavController navController = Navigation.findNavController((androidx.appcompat.app.AppCompatActivity) context, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_proveedor_to_nav_proveedor_editar, bundle);
        }
    }

    public static void eliminarProveedor(int id, Context context) {
        String url = servidor + "proveedor_eliminar.php";
        RequestParams params = new RequestParams();
        params.put("id", id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                // Leer y mostrar el mensaje que envía el servidor PHP
                String respuesta = new String(responseBody).trim();
                Toast.makeText(context, respuesta, Toast.LENGTH_LONG).show();

                if (respuesta.equalsIgnoreCase("Proveedor eliminado correctamente")) {
                    if (context instanceof androidx.appcompat.app.AppCompatActivity) {
                        NavController navController = Navigation.findNavController(
                                (androidx.appcompat.app.AppCompatActivity) context,
                                R.id.nav_host_fragment_content_main
                        );
                        navController.navigate(R.id.action_nav_proveedor_self); // Recargar lista
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == agregar) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_proveedor_to_nav_proveedor_agregar);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            listarProveedores();
            swipeRefreshLayout.setRefreshing(false);
        }, 1000);
    }
}
