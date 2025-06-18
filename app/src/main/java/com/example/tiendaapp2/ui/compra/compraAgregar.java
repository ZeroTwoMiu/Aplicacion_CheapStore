package com.example.tiendaapp2.ui.compra;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

import java.net.URLEncoder;
import androidx.fragment.app.Fragment;

import com.example.tiendaapp2.R;
import com.loopj.android.http.JsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;
public class compraAgregar extends Fragment {

    private final String servidor = "http://10.0.2.2/cheapstore/";
    private EditText etNumeroCompra, etCantidad, etPrecioUnitario;
    private Spinner spinnerProveedor, spinnerProducto;
    private Button btnAgregarProducto, btnFinalizarCompra;
    private ListView lvProductosAgregados;
    private TextView tvTotal;
    private List<ProductoCompraItem> listaProductos;
    private ProductoCompraAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_compra_agregar, container, false);

        // Referencias
        etNumeroCompra = rootView.findViewById(R.id.etNumeroCompra);
        etCantidad = rootView.findViewById(R.id.etCantidad);
        etPrecioUnitario = rootView.findViewById(R.id.etPrecioUnitario);
        spinnerProveedor = rootView.findViewById(R.id.spinnerProveedor);
        spinnerProducto = rootView.findViewById(R.id.spinnerProducto);
        btnAgregarProducto = rootView.findViewById(R.id.btnAgregarProducto);
        btnFinalizarCompra = rootView.findViewById(R.id.btnFinalizarCompra);
        lvProductosAgregados = rootView.findViewById(R.id.lvProductosAgregados);
        tvTotal = rootView.findViewById(R.id.tvTotal);

        // Inicializar lista y adaptador
        listaProductos = new ArrayList<>();
        adapter = new ProductoCompraAdapter(getContext(), listaProductos);
        lvProductosAgregados.setAdapter(adapter);
        cargarProveedores();
        cargarProductos();


        // Eventos
        btnAgregarProducto.setOnClickListener(v -> agregarProducto());
        btnFinalizarCompra.setOnClickListener(v -> finalizarCompra());

        return rootView;
    }

    private void agregarProducto() {
        String nombreProducto = spinnerProducto.getSelectedItem().toString();

        int productoId = spinnerProducto.getSelectedItemPosition() + 1; // Simple ejemplo
        String cantidadStr = etCantidad.getText().toString().trim();
        String precioStr = etPrecioUnitario.getText().toString().trim();

        if (cantidadStr.isEmpty() || precioStr.isEmpty()) {
            Toast.makeText(getContext(), "Ingrese cantidad y precio", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad = Integer.parseInt(cantidadStr);
        double precioUnitario = Double.parseDouble(precioStr);

        ProductoCompraItem item = new ProductoCompraItem(productoId, nombreProducto, cantidad, precioUnitario);
        listaProductos.add(item);
        adapter.notifyDataSetChanged();
        actualizarTotal();


        // Limpiar campos
        etCantidad.setText("");
        etPrecioUnitario.setText("");
    }

    private void actualizarTotal() {
        double total = 0;
        for (ProductoCompraItem item : listaProductos) {
            total += item.getSubtotal();
        }
        tvTotal.setText("Total: S/ " + String.format("%.2f", total));
    }

    private void finalizarCompra() {
        String numeroCompra = etNumeroCompra.getText().toString().trim();
        if (numeroCompra.isEmpty()) {
            Toast.makeText(getContext(), "Ingrese el número de compra", Toast.LENGTH_SHORT).show();
            return;
        }

        if (spinnerProveedor.getSelectedItem() == null) {
            Toast.makeText(getContext(), "Seleccione un proveedor", Toast.LENGTH_SHORT).show();
            return;
        }

        ProveedorItem proveedorSeleccionado = (ProveedorItem) spinnerProveedor.getSelectedItem();
        int idProveedor = proveedorSeleccionado.getIdProveedor();

        if (listaProductos.isEmpty()) {
            Toast.makeText(getContext(), "Agregue al menos un producto", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonArray = new JSONArray();
        try {
            for (ProductoCompraItem item : listaProductos) {
                JSONObject obj = new JSONObject();
                obj.put("id_producto", item.getProductoId());
                obj.put("cantidad", item.getCantidad());
                obj.put("precio", item.getPrecio());
                jsonArray.put(obj);
            }

            String productosJson = URLEncoder.encode(jsonArray.toString(), "UTF-8");

            String url = "http://10.0.2.2/cheapstore/cheapstore/compra_guardar.php" +
                    "?numeroCompra=" + numeroCompra +
                    "&idProveedor=" + idProveedor +
                    "&productos=" + productosJson;

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(url, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String rpta = new String(responseBody);
                    Toast.makeText(getContext(), rpta, Toast.LENGTH_LONG).show();

                    if (rpta.contains("correctamente")) {
                        limpiarCampos();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    Toast.makeText(getContext(), "Error al registrar la compra", Toast.LENGTH_LONG).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
        }
    }
    private void cargarProveedores() {
        String url = "http://10.0.2.2/cheapstore/proveedor_mostrar.php";
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                ArrayList<ProveedorItem> lista = new ArrayList<>();
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        int id = obj.getInt("id_proveedor");
                        String nombre = obj.getString("nom_proveedor");
                        lista.add(new ProveedorItem(id, nombre));
                    }

                    ArrayAdapter<ProveedorItem> adapter = new ArrayAdapter<>(
                            getContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            lista
                    );
                    spinnerProveedor.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al procesar proveedores", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), "Error al obtener proveedores", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void cargarProductos() {
        String url = servidor + "producto_mostrar.php";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String contenido = new String(responseBody);
                    JSONArray array = new JSONArray(contenido);
                    ArrayList<ProductoItem> lista = new ArrayList<>();

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);

                        int idProducto = obj.getInt("id_producto");
                        String nombre = obj.getString("nom_producto");

                        lista.add(new ProductoItem(idProducto, nombre));
                    }

                    ArrayAdapter<ProductoItem> adaptador = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_dropdown_item, lista);
                    spinnerProducto.setAdapter(adaptador);

                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al parsear productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error de conexión al listar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void limpiarCampos() {
        etNumeroCompra.setText("");
        etCantidad.setText("");
        etPrecioUnitario.setText("");
        listaProductos.clear();
        adapter.notifyDataSetChanged();
        tvTotal.setText("S/ 0.00");
    }

}