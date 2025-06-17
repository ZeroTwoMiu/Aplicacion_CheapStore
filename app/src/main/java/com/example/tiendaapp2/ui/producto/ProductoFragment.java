package com.example.tiendaapp2.ui.producto;

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

public class ProductoFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    ListView lista;
    Button agregar;
    SwipeRefreshLayout swipeRefreshLayout;
    SearchView searchView;

    ProductoAdapter adapter;
    ArrayList<Producto> listaProductosOriginal = new ArrayList<>();
    ArrayList<Producto> listaFiltrada = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_producto, container, false);

        lista = rootView.findViewById(R.id.lstProductos);
        agregar = rootView.findViewById(R.id.btnAgregarProducto);
        swipeRefreshLayout = rootView.findViewById(R.id.swipeRefresh);
        searchView = rootView.findViewById(R.id.searchView); // Asegúrate de tenerlo en tu XML

        agregar.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        configurarBuscador();
        ListarProducto();

        return rootView;
    }

    private void configurarBuscador() {
        searchView.setQueryHint("Buscar por nombre");
        searchView.setIconifiedByDefault(false); // Para que esté expandido

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // No usado
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
            listaFiltrada.addAll(listaProductosOriginal);
        } else {
            for (Producto p : listaProductosOriginal) {
                if (p.getNombre().toLowerCase().contains(texto.toLowerCase())) {
                    listaFiltrada.add(p);
                }
            }
        }

        adapter = new ProductoAdapter(getActivity(), listaFiltrada);
        lista.setAdapter(adapter);
    }

    private void ListarProducto() {
        String url = servidor + "producto_mostrar.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new RequestParams(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);

                try {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    listaProductosOriginal.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        Producto producto = new Producto(
                                obj.getInt("id_producto"),
                                obj.getString("cod_producto"),
                                obj.getString("nom_producto"),
                                obj.getString("nom_categoria"),
                                obj.getString("nom_marca"),
                                obj.getString("des_producto"),
                                obj.getDouble("pco_producto"),
                                obj.getDouble("pve_producto"),
                                obj.getDouble("stk_producto")
                        );
                        listaProductosOriginal.add(producto);
                    }

                    listaFiltrada.clear();
                    listaFiltrada.addAll(listaProductosOriginal);

                    adapter = new ProductoAdapter(getActivity(), listaFiltrada);
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

    public static void EliminarProducto(int id, Context context) {
        String url = servidor + "producto_eliminar.php";
        RequestParams params = new RequestParams();
        params.put("id", id);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                if (context instanceof androidx.appcompat.app.AppCompatActivity) {
                    NavController navController = Navigation.findNavController((androidx.appcompat.app.AppCompatActivity) context, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_producto_self);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Error al eliminar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void EditarProducto(int id, Context context) {
        if (context instanceof androidx.appcompat.app.AppCompatActivity) {
            Bundle bundle = new Bundle();
            bundle.putInt("idProd", id);
            NavController navController = Navigation.findNavController((androidx.appcompat.app.AppCompatActivity) context, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_producto_to_nav_producto_editar, bundle);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == agregar) {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_producto_to_nav_producto_agregar);
        }
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(() -> {
            ListarProducto();
            swipeRefreshLayout.setRefreshing(false);
        }, 1000);
    }
}
