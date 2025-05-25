package com.example.tiendaapp2.ui.producto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.Callback;
import com.example.tiendaapp2.ui.Item;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ProductoEditar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    final String servidor = "http://10.0.2.2/tienda2/";
    int idCategoria = -1;
    int idMarca = -1;
    int idProd;
    ArrayList<Item> lista_categoria = new ArrayList<>();
    ArrayList<Item> lista_marca = new ArrayList<>();
    private EditText cod,nom,des,pv,pc;
    private Spinner cat,mar;
    private Button reg;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_producto_editar, container, false);

        cod = (EditText) rootView.findViewById(R.id.etCodProductoE);
        nom = (EditText) rootView.findViewById(R.id.etNomProductoE);
        des = (EditText) rootView.findViewById(R.id.etDesProductoE);
        pv = (EditText) rootView.findViewById(R.id.etPveProductoE);
        pc = (EditText) rootView.findViewById(R.id.etPcoProductoE);
        cat = (Spinner) rootView.findViewById(R.id.spCatProductoE);
        cat.setOnItemSelectedListener(this);
        mar = (Spinner) rootView.findViewById(R.id.spMarProductoE);
        mar.setOnItemSelectedListener(this);
        reg = (Button) rootView.findViewById(R.id.btnRegProductoE);
        reg.setOnClickListener(this);

        idProd = getArguments().getInt("idProd", 0); // o usa requireArguments().getInt("id")

        ObtenerCategoria(new Callback() {
            @Override
            public void onSuccess() {
                ObtenerMarca(new Callback() {
                    @Override
                    public void onSuccess() {
                        ConsultarProducto(idProd);
                    }
                });
            }
        });

        return rootView;
    }

    private void ConsultarProducto(int idProd) {
        //Declarar la URL
        String url = servidor + "producto_consultar.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("idProd",idProd);

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

                    // Recorrer el array JSON y agregar cada contacto a la lista
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject contactoJson = jsonArray.getJSONObject(i);

                        int id_producto = contactoJson.getInt("id_producto");
                        String cod_producto = contactoJson.getString("cod_producto");
                        String nom_producto = contactoJson.getString("nom_producto");
                        int id_categoria = contactoJson.getInt("id_categoria");
                        int id_marca = contactoJson.getInt("id_marca");
                        String des_producto = contactoJson.getString("des_producto");
                        double pco_producto = contactoJson.getDouble("pco_producto");
                        double pve_producto = contactoJson.getDouble("pve_producto");
                        double stk_producto = contactoJson.getDouble("stk_producto");

                        cod.setText(cod_producto);
                        nom.setText(nom_producto);
                        des.setText(des_producto);
                        pc.setText(String.valueOf(pco_producto));
                        pv.setText(String.valueOf(pve_producto));

                        for(int x=0; x<lista_categoria.size();x++)
                        {
                            if(lista_categoria.get(x).getId() == id_categoria)
                            {
                                cat.setSelection(x);
                            }
                        }

                        for(int x=0; x<lista_marca.size();x++)
                        {
                            if(lista_marca.get(x).getId() == id_marca)
                            {
                                mar.setSelection(x);
                            }
                        }

                    }

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

    private void ObtenerCategoria(Callback callback) {

        //Declarar la URL
        String url = servidor + "categoria_mostrar.php";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                try {

                    lista_categoria.add(new Item(-1, "Seleccione Categoría"));

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");

                        lista_categoria.add(new Item(id, nombre));
                    }

                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            lista_categoria
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    cat.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Llama al callback
                if (callback != null) {
                    callback.onSuccess();
                }


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void ObtenerMarca(Callback callback) {

        //Declarar la URL
        String url = servidor + "marca_mostrar.php";

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String response = new String(responseBody);
                try {

                    lista_marca.add(new Item(-1, "Seleccione Marca"));

                    JSONArray jsonArray = new JSONArray(response);

                    for (int i = 0; i < jsonArray.length(); i++)
                    {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        int id = obj.getInt("id");
                        String nombre = obj.getString("nombre");

                        lista_marca.add(new Item(id, nombre));
                    }

                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(
                            getActivity(),
                            android.R.layout.simple_spinner_item,
                            lista_marca
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mar.setAdapter(adapter);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // Llama al callback
                if (callback != null) {
                    callback.onSuccess();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v == reg) //si presiono el boton registrar
        {
            //Recogemos datos
            String codigo = cod.getText().toString();
            String nombre = nom.getText().toString();
            String descripcion =  des.getText().toString();
            double preComp = Double.parseDouble(pc.getText().toString());
            double preVent = Double.parseDouble(pv.getText().toString());

            //Validamos
            if(codigo.isEmpty() || nombre.isEmpty() || descripcion.isEmpty() || preComp<0 || preVent<0)
            {
               Toast.makeText(getActivity(),"Complete los campos requeridos",Toast.LENGTH_SHORT).show();
            }
            else if(preComp>preVent)
            {
                Toast.makeText(getActivity(),"El precio de compra debe ser menor o igual al precio de venta",Toast.LENGTH_SHORT).show();
            }
            else if(idCategoria==-1)
            {
                Toast.makeText(getActivity(),"Seleccione una categoría válida",Toast.LENGTH_SHORT).show();
            }
            else if(idMarca==-1)
            {
                Toast.makeText(getActivity(),"Seleccione una marca válida",Toast.LENGTH_SHORT).show();
            }
            else
            {
                ActualizarProducto(codigo,nombre,descripcion,preComp,preVent,idCategoria,idMarca);
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

    private void  ActualizarProducto(String codigo, String nombre, String descripcion, double preComp, double preVent, int idCategoria, int idMarca) {

        //Declarar la URL
        String url = servidor + "producto_actualizar.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("idProd",idProd);
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
                //LimpiarCampos();

                //otra forma de regresar a la vista anterior
                //volver a la vista anterior
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_producto_editar_to_nav_producto);
                //otra forma de regresar a la vista anterior
                
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