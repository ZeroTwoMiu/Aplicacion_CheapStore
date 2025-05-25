package com.example.tiendaapp2.ui.producto;

import static com.example.tiendaapp2.Login.servidor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tiendaapp2.R;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class ProductoFragment extends Fragment implements View.OnClickListener {


    ListView lista;
    Button agregar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_producto, container, false);

        lista = (ListView) rootView.findViewById(R.id.lstProductos);
        agregar = (Button) rootView.findViewById(R.id.btnAgregarProducto);
        agregar.setOnClickListener(this);

        ListarPoducto();

        return rootView;
    }

    private void ListarPoducto() {
        //Declarar la URL
        String url = servidor + "producto_mostrar.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();

        //Envio al web service y respuesta
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                //Toast.makeText(getApplicationContext(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();
                // Parsear el JSON
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);

                    // Crear una lista para almacenar los objetos
                    ArrayList<Producto> productosList = new ArrayList<>();

                    // Recorrer el array JSON y agregar cada contacto a la lista
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject contactoJson = jsonArray.getJSONObject(i);

                        int id_producto = contactoJson.getInt("id_producto");
                        String cod_producto = contactoJson.getString("cod_producto");
                        String nom_producto = contactoJson.getString("nom_producto");
                        String nom_categoria = contactoJson.getString("nom_categoria");
                        String nom_marca = contactoJson.getString("nom_marca");
                        String des_producto = contactoJson.getString("des_producto");
                        double pco_producto = contactoJson.getDouble("pco_producto");
                        double pve_producto = contactoJson.getDouble("pve_producto");
                        double stk_producto = contactoJson.getDouble("stk_producto");

                        // Crear un objeto Contact y agregarlo a la lista
                        Producto producto = new Producto(id_producto,
                                cod_producto,
                                nom_producto,
                                nom_categoria,
                                nom_marca,
                                des_producto,
                                pco_producto,
                                pve_producto,
                                stk_producto
                        );
                        productosList.add(producto);
                    }

                    Toast.makeText(getActivity(),String.valueOf(jsonArray.length()),Toast.LENGTH_SHORT).show();

                    // Crear el adaptador
                    ProductoAdapter adapter = new ProductoAdapter(getActivity(), productosList);
                    // Establecer el adaptador en el ListView
                    lista.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getActivity(),mensaje,Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void EliminarProducto(int id, Context context)
    {
        //Declarar la URL
        String url = servidor + "producto_eliminar.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("id",id);

        //Envio al web service y respuesta
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(context, "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();

                if (context instanceof AppCompatActivity) {
                    AppCompatActivity activity = (AppCompatActivity) context;

                    NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
                    navController.navigate(R.id.action_nav_producto_self);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(context,mensaje,Toast.LENGTH_LONG).show();
            }
        });

    }

    public static void EditarProducto(int id, Context context)
    {
        if (context instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) context;

            Bundle bundle = new Bundle();
            bundle.putInt("idProd", id);

            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_producto_to_nav_producto_editar,bundle);
        }
    }

    @Override
    public void onClick(View v) {
        if(v==agregar)
        {
            NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
            navController.navigate(R.id.action_nav_producto_to_nav_producto_agregar);
        }

    }
}