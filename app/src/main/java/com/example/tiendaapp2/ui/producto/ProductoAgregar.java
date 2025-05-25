package com.example.tiendaapp2.ui.producto;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ProductoAgregar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {


    int idCategoria = -1;
    int idMarca = -1;
    private EditText cod,nom,des,pv,pc;
    private Spinner cat,mar;
    private Button reg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_producto_agregar, container, false);

        cod = (EditText) rootView.findViewById(R.id.etCodProducto);
        nom = (EditText) rootView.findViewById(R.id.etNomProducto);
        des = (EditText) rootView.findViewById(R.id.etDesProducto);
        pv = (EditText) rootView.findViewById(R.id.etPveProducto);
        pc = (EditText) rootView.findViewById(R.id.etPcoProducto);
        cat = (Spinner) rootView.findViewById(R.id.spCatProducto);
        cat.setOnItemSelectedListener(this);
        mar = (Spinner) rootView.findViewById(R.id.spMarProducto);
        mar.setOnItemSelectedListener(this);
        reg = (Button) rootView.findViewById(R.id.btnRegProducto);
        reg.setOnClickListener(this);

        ObtenerCategoria();
        ObtenerMarca();

        return rootView;
    }

    private void ObtenerCategoria() {

        //Declarar la URL
        String url = servidor + "categoria_mostrar.php";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                try {
                    ArrayList<Item> lista = new ArrayList<>();
                    lista.add(new Item(-1, "Seleccione Categoría"));

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");

                        lista.add(new Item(id, nombre));
                    }

                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            lista
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cat.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ObtenerMarca() {

        //Declarar la URL
        String url = servidor + "marca_mostrar.php";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                try {
                    ArrayList<Item> lista = new ArrayList<>();
                    lista.add(new Item(-1, "Seleccione Marca"));

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");

                        lista.add(new Item(id, nombre));
                    }

                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            lista
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mar.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if (v == reg) {
            // Obtener datos en forma de texto
            String codigo = cod.getText().toString().trim();
            String nombre = nom.getText().toString().trim();
            String descripcion = des.getText().toString().trim();
            String strPreComp = pc.getText().toString().trim();
            String strPreVent = pv.getText().toString().trim();

            // Validar campos vacíos
            if (codigo.isEmpty() || nombre.isEmpty() || descripcion.isEmpty() ||
                    strPreComp.isEmpty() || strPreVent.isEmpty()) {
                Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Convertir precios a double
            double preComp, preVent;
            try {
                preComp = Double.parseDouble(strPreComp);
                preVent = Double.parseDouble(strPreVent);
            } catch (NumberFormatException e) {
                Toast.makeText(getActivity(), "Los precios deben ser números válidos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validar lógica de precios y selección de opciones
            if (preComp < 0 || preVent < 0) {
                Toast.makeText(getActivity(), "Los precios no pueden ser negativos", Toast.LENGTH_SHORT).show();
            } else if (preComp > preVent) {
                Toast.makeText(getActivity(), "El precio de compra debe ser menor o igual al precio de venta", Toast.LENGTH_SHORT).show();
            } else if (idCategoria == -1) {
                Toast.makeText(getActivity(), "Seleccione una categoría válida", Toast.LENGTH_SHORT).show();
            } else if (idMarca == -1) {
                Toast.makeText(getActivity(), "Seleccione una marca válida", Toast.LENGTH_SHORT).show();
            } else
            {
                // Verificamos si el código ya existe en la base de datos
                String url = servidor + "producto_existe.php";
                RequestParams params = new RequestParams();
                params.put("codigo", codigo);

                AsyncHttpClient client = new AsyncHttpClient();
                client.get(url, params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String respuesta = new String(responseBody).trim();

                        if (respuesta.equalsIgnoreCase("existe")) {
                            Toast.makeText(getActivity(), "El código ya está registrado. Use uno diferente.", Toast.LENGTH_SHORT).show();
                        } else {
                            // Código no existe, registrar el producto
                            RegistrarProducto(codigo, nombre, descripcion, preComp, preVent, idCategoria, idMarca);
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(getActivity(), "Error al verificar el código: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


    private void LimpiarCampos()
    {
        cod.setText("");
        nom.setText("");
        des.setText("");
        pc.setText("");
        pv.setText("");
        cat.setSelection(0);
        mar.setSelection(0);
        cod.requestFocus();
    }

    private void  RegistrarProducto(String codigo, String nombre, String descripcion, double preComp, double preVent, int idCategoria, int idMarca) {

        //Declarar la URL
        String url = servidor + "producto_registrar.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("codigo",codigo);
        requestParams.put("nombre",nombre);
        requestParams.put("descripcion",descripcion);
        requestParams.put("preComp",preComp);
        requestParams.put("preVent",preVent);
        requestParams.put("idCategoria",idCategoria);
        requestParams.put("idMarca",idMarca);

        //Envio al web service y respuesta
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                Toast.makeText(getActivity(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();
                LimpiarCampos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getActivity(),mensaje,Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(parent == cat) //Seleccionando item en spinner Categoria
        {
            Item itemSeleccionado = (Item) parent.getItemAtPosition(position);
            idCategoria = itemSeleccionado.getId();
            String nomCategoria = itemSeleccionado.getNombre();
            //Toast.makeText(getActivity(), "ID seleccionado: " + idCategoria+" "+nomCategoria, Toast.LENGTH_SHORT).show();
        }
        else if(parent == mar) //Seleccionando item en spinner Marca
        {
            Item itemSeleccionado = (Item) parent.getItemAtPosition(position);
            idMarca = itemSeleccionado.getId();
            String nomMarca = itemSeleccionado.getNombre();
            //Toast.makeText(getActivity(), "ID seleccionado: " + idMarca+" "+nomMarca, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}