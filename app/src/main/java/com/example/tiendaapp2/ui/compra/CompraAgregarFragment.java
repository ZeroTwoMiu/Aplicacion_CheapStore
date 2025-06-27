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
import com.example.tiendaapp2.ui.compra.CompraDetalle;
import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.producto.Producto;
import com.example.tiendaapp2.ui.proveedor.Proveedor;
import com.loopj.android.http.*;

import org.json.*;

import java.text.SimpleDateFormat;
import java.util.*;

import cz.msebera.android.httpclient.Header;

public class CompraAgregarFragment extends Fragment {

    Spinner spnProveedor, spnProducto;
    TextView txtPrecio;
    EditText edtCantidad;
    Button btnAgregarDetalle, btnFinalizar;
    ListView lstDetalles;

    ArrayList<Proveedor> listaProveedores = new ArrayList<>();
    ArrayList<Producto> listaProductos = new ArrayList<>();
    ArrayList<CompraDetalle> listaDetalles = new ArrayList<>();

    CompraDetalleAdapter adapter;

    int idEmpleado = 1; // Debería obtenerse del login

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_compra_agregar, container, false);

        spnProveedor = v.findViewById(R.id.spnProveedor);
        spnProducto = v.findViewById(R.id.spnProducto);
        edtCantidad = v.findViewById(R.id.edtCantidad);
        txtPrecio = v.findViewById(R.id.txtPrecio);

        btnAgregarDetalle = v.findViewById(R.id.btnAgregarDetalle);
        btnFinalizar = v.findViewById(R.id.btnFinalizarCompra);
        lstDetalles = v.findViewById(R.id.lstCompraDetalle);

        adapter = new CompraDetalleAdapter(getActivity(), listaDetalles);
        lstDetalles.setAdapter(adapter);

        listarProveedores();
        listarProductos();

        btnAgregarDetalle.setOnClickListener(v1 -> agregarDetalle());
        btnFinalizar.setOnClickListener(v12 -> guardarCompra());

        return v;
    }

    private void listarProveedores() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "proveedor_mostrar.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONArray array = new JSONArray(new String(bytes));
                    listaProveedores.clear();
                    ArrayList<String> nombres = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject obj = array.getJSONObject(j);
                        Proveedor proveedor = new Proveedor(
                                obj.getInt("id_proveedor"),
                                obj.getString("nom_proveedor"),
                                "", "", "", "", 1
                        );
                        listaProveedores.add(proveedor);
                        nombres.add(proveedor.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, nombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnProveedor.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar proveedores", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getContext(), "Error al cargar proveedores", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void listarProductos() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "producto_mostrar.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                try {
                    JSONArray array = new JSONArray(new String(bytes));
                    listaProductos.clear();
                    ArrayList<String> nombres = new ArrayList<>();
                    for (int j = 0; j < array.length(); j++) {
                        JSONObject obj = array.getJSONObject(j);
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
                        listaProductos.add(producto);
                        nombres.add(producto.getNombre());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, nombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnProducto.setAdapter(adapter);
                    spnProducto.setAdapter(adapter);

                    spnProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            Producto producto = listaProductos.get(position);
                            txtPrecio.setText(String.valueOf(producto.getPcompra()));
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void agregarDetalle() {
        int index = spnProducto.getSelectedItemPosition();
        Producto producto = listaProductos.get(index);

        double cantidad = Double.parseDouble(edtCantidad.getText().toString());
        double precio = Double.parseDouble(txtPrecio.getText().toString());

        CompraDetalle detalle = new CompraDetalle(producto.getId(), producto.getNombre(), cantidad, precio);
        listaDetalles.add(detalle);
        adapter.notifyDataSetChanged();

        edtCantidad.setText("");
        txtPrecio.setText("");
    }

    private void guardarCompra() {
        int indexProv = spnProveedor.getSelectedItemPosition();
        int idProveedor = listaProveedores.get(indexProv).getId();

        int idComprobante = 1;
        String numeroCompra = "C" + System.currentTimeMillis();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        JSONArray jsonDetalles = new JSONArray();
        for (CompraDetalle detalle : listaDetalles) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id_producto", detalle.getIdProducto());
                obj.put("cantidad", detalle.getCantidad());
                obj.put("precio", detalle.getPrecio());
                jsonDetalles.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestParams params = new RequestParams();
        params.put("idProveedor", idProveedor);
        params.put("idEmpleado", idEmpleado);
        params.put("idComprobante", idComprobante);
        params.put("numCompra", numeroCompra);
        params.put("fhCompra", fechaActual);
        params.put("detalles", jsonDetalles.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "compra_guardar.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), new String(responseBody), Toast.LENGTH_LONG).show();
                listaDetalles.clear();
                adapter.notifyDataSetChanged();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.nav_compra);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al guardar compra", Toast.LENGTH_SHORT).show();
            }
        });
    }
}