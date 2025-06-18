package com.example.tiendaapp2.ui.empleado;

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

public class EmpleadoFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ListView lista;
    Button agregar;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;

    EmpleadoAdapter adapter;
    ArrayList<Empleado> listaOriginal = new ArrayList<>();
    ArrayList<Empleado> listaFiltrada = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_empleado, container, false);

        lista = rootView.findViewById(R.id.lstEmpleados);
        agregar = rootView.findViewById(R.id.btnAgregarEmpleado);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        searchView = rootView.findViewById(R.id.searchView);

        agregar.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        configurarBuscador();
        ListarEmpleado();

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
            for (Empleado e : listaOriginal) {
                if (e.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(e);
                }
            }
        }

        adapter = new EmpleadoAdapter(getActivity(), listaFiltrada);
        lista.setAdapter(adapter);
    }

    private void ListarEmpleado() {
        String url = servidor + "empleado_mostrar.php";
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

                        Empleado empleado = new Empleado(
                                obj.getInt("id_empleado"),
                                obj.getInt("id_cargo"),
                                obj.getString("nom_empleado"),
                                obj.getString("apat_empleado"),
                                obj.getString("amat_empleado"),
                                obj.getString("ndoc_empleado"),
                                obj.getString("cel_empleado"),
                                obj.getString("em_empleado"),
                                obj.getString("dir_empleado"),
                                obj.getString("fn_empleado"),
                                obj.getString("usu_empleado"),
                                obj.getString("pass_empleado"),
                                obj.getString("est_empleado")
                        );

                        listaOriginal.add(empleado);
                    }

                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaOriginal);

                    adapter = new EmpleadoAdapter(getActivity(), listaFiltrada);
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

    public static void EditarEmpleado(int id, Context context) {
        if (context instanceof androidx.appcompat.app.AppCompatActivity) {
            Bundle bundle = new Bundle();
            bundle.putInt("idEmpleado", id);
            NavController navController = Navigation.findNavController((androidx.appcompat.app.AppCompatActivity) context, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_empleado_to_nav_empleado_editar, bundle);
        }
    }

    public static void EliminarEmpleado(int id, Context context) {
        String url = servidor + "empleado_eliminar.php";
        RequestParams params = new RequestParams();
        params.put("id", id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, "Empleado eliminado", Toast.LENGTH_SHORT).show();
                if (context instanceof androidx.appcompat.app.AppCompatActivity) {
                    NavController navController = Navigation.findNavController((androidx.appcompat.app.AppCompatActivity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_empleado_self);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == agregar) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_empleado_to_nav_empleado_agregar);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            ListarEmpleado();
            swipeRefreshLayout.setRefreshing(false);
        }, 1000);
    }
}
