package com.example.tiendaapp2.ui.cliente;

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

public class ClienteFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ListView lista;
    Button agregar;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;

    ClienteAdapter adapter;
    ArrayList<Cliente> listaOriginal = new ArrayList<>();
    ArrayList<Cliente> listaFiltrada = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cliente, container, false);

        lista = rootView.findViewById(R.id.lstClientes);
        agregar = rootView.findViewById(R.id.btnAgregarCliente);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefreshCliente);
        searchView = rootView.findViewById(R.id.searchViewCliente);

        agregar.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        configurarBuscador();
        listarClientes();

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
            for (Cliente c : listaOriginal) {
                String nombreCompleto = c.getNombreCompleto().toLowerCase();
                if (nombreCompleto.contains(texto.toLowerCase())) {
                    listaFiltrada.add(c);
                }
            }
        }

        adapter = new ClienteAdapter(getActivity(), listaFiltrada);
        lista.setAdapter(adapter);
    }

    private void listarClientes() {
        String url = servidor + "cliente_mostrar.php";
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

                        Cliente cliente = new Cliente(
                                obj.getInt("id_cliente"),
                                obj.getString("nom_cliente"),
                                obj.getString("apat_cliente"),
                                obj.getString("amat_cliente"),
                                obj.getString("ndoc_cliente"),
                                obj.getString("cel_cliente"),
                                obj.getString("em_cliente"),
                                obj.getString("dir_cliente"),
                                obj.getString("fn_cliente"),
                                obj.getInt("est_cliente")
                        );

                        listaOriginal.add(cliente);
                    }

                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaOriginal);

                    adapter = new ClienteAdapter(getActivity(), listaFiltrada);
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

    public static void editarCliente(int id, Context context) {
        if (context instanceof androidx.appcompat.app.AppCompatActivity) {
            Bundle bundle = new Bundle();
            bundle.putInt("idCliente", id);
            NavController navController = Navigation.findNavController((androidx.appcompat.app.AppCompatActivity) context, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_cliente_to_nav_cliente_editar, bundle);
        }
    }

    public static void eliminarCliente(int id, Context context) {
        String url = servidor + "cliente_eliminar.php";
        RequestParams params = new RequestParams();
        params.put("id", id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody).trim();
                Toast.makeText(context, respuesta, Toast.LENGTH_LONG).show();

                if (respuesta.equalsIgnoreCase("Cliente eliminado correctamente")) {
                    if (context instanceof androidx.appcompat.app.AppCompatActivity) {
                        NavController navController = Navigation.findNavController(
                                (androidx.appcompat.app.AppCompatActivity) context,
                                R.id.nav_host_fragment_content_main
                        );
                        navController.navigate(R.id.action_nav_cliente_self);
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
            navController.navigate(R.id.action_nav_cliente_to_nav_cliente_agregar);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            listarClientes();
            swipeRefreshLayout.setRefreshing(false);
        }, 1000);
    }
}
