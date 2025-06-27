package com.example.tiendaapp2.ui.venta;

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

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.cliente.Cliente;
import com.example.tiendaapp2.ui.producto.Producto;
import com.loopj.android.http.*;

import org.json.*;

import java.text.SimpleDateFormat;
import java.util.*;

import cz.msebera.android.httpclient.Header;

public class VentaAgregarFragment extends Fragment {

    Spinner spnCliente, spnProducto;
    EditText edtCantidadVenta;
    TextView txtPrecioVenta;
    Button btnAgregarDetalleVenta, btnFinalizarVenta;
    ListView lstVentaDetalle;

    ArrayList<Cliente> listaClientes = new ArrayList<>();
    ArrayList<Producto> listaProductos = new ArrayList<>();
    ArrayList<VentaDetalle> listaDetalles = new ArrayList<>();

    VentaDetalleAdapter adapter;
    int idEmpleado = 1;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_venta_agregar, container, false);

        // IDs corregidos
        spnCliente = v.findViewById(R.id.spnCliente);
        spnProducto = v.findViewById(R.id.spnProducto);
        edtCantidadVenta = v.findViewById(R.id.edtCantidadVenta);
        txtPrecioVenta = v.findViewById(R.id.txtPrecioVenta);
        btnAgregarDetalleVenta = v.findViewById(R.id.btnAgregarDetalleVenta);
        btnFinalizarVenta = v.findViewById(R.id.btnFinalizarVenta);
        lstVentaDetalle = v.findViewById(R.id.lstVentaDetalle);

        adapter = new VentaDetalleAdapter(getActivity(), listaDetalles);
        lstVentaDetalle.setAdapter(adapter);

        listarClientes();
        listarProductos();

        btnAgregarDetalleVenta.setOnClickListener(view -> agregarDetalle());
        btnFinalizarVenta.setOnClickListener(view -> guardarVenta());

        spnProducto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Producto producto = listaProductos.get(position);
                txtPrecioVenta.setText("Precio de Venta: S/ " + producto.getPventa());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        return v;
    }

    private void listarClientes() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "cliente_mostrar.php", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    listaClientes.clear();
                    ArrayList<String> nombres = new ArrayList<>();
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        Cliente cliente = new Cliente(
                                obj.optInt("id_cliente", 0),
                                obj.optString("nom_cliente", ""),
                                obj.optString("apat_cliente", ""),
                                obj.optString("amat_cliente", ""),
                                obj.optString("ndoc_cliente", ""),
                                obj.optString("cel_cliente", ""),
                                obj.optString("em_cliente", ""),
                                obj.optString("dir_cliente", ""),
                                obj.optString("fn_cliente", ""),
                                1 // o usa obj.optInt("est_cliente", 1) si quieres traerlo desde la BD
                        );

                        listaClientes.add(cliente);
                        nombres.add(cliente.getNombreCompleto());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nombres);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spnCliente.setAdapter(adapter);
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error al procesar clientes", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al cargar clientes", Toast.LENGTH_SHORT).show();
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
        try {
            int index = spnProducto.getSelectedItemPosition();
            Producto producto = listaProductos.get(index);

            double cantidad = Double.parseDouble(edtCantidadVenta.getText().toString());
            double precio = producto.getPventa();
            double subtotal = cantidad * precio;

            VentaDetalle detalle = new VentaDetalle(producto.getId(), producto.getNombre(), cantidad, precio);

            listaDetalles.add(detalle);
            adapter.notifyDataSetChanged();

            edtCantidadVenta.setText("");
        } catch (Exception e) {
            Toast.makeText(getContext(), "Complete correctamente los campos", Toast.LENGTH_SHORT).show();
        }
    }

    private void guardarVenta() {
        int indexCli = spnCliente.getSelectedItemPosition();
        int idCliente = listaClientes.get(indexCli).getId();

        int idComprobante = 1;
        String numeroVenta = "V" + System.currentTimeMillis();
        String fechaActual = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        JSONArray jsonDetalles = new JSONArray();
        for (VentaDetalle detalle : listaDetalles) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("id_producto", detalle.getIdProducto());
                obj.put("cantidad", detalle.getCantidad());
                obj.put("precio", detalle.getPrecioVenta());
                jsonDetalles.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        RequestParams params = new RequestParams();
        params.put("idCliente", idCliente);
        params.put("idEmpleado", idEmpleado);
        params.put("idComprobante", idComprobante);
        params.put("numVenta", numeroVenta);
        params.put("fhVenta", fechaActual);
        params.put("detalles", jsonDetalles.toString());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(servidor + "venta_guardar.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getContext(), new String(responseBody), Toast.LENGTH_LONG).show();
                listaDetalles.clear();
                adapter.notifyDataSetChanged();
                Bundle result = new Bundle();
                result.putBoolean("venta_guardada", true);
                getParentFragmentManager().setFragmentResult("venta_result", result);

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.popBackStack(); // Para volver automáticamente sin recrear
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Error al guardar venta", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
