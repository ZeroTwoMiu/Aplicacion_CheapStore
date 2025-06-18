package com.example.tiendaapp2.ui.empleado;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.Item;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EmpleadoAgregar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    // ---------- Variables ----------
    int idCargo = -1;
    private EditText nombres, apat, amat, ndoc, cel, email, direccion, fechaNac, usuario, contrasena, estado;
    private Spinner spCargo;
    private Button btnRegistrar;

    // ---------- onCreateView ----------
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_empleado_agregar, container, false);

        // Referencias
        nombres = root.findViewById(R.id.etNom);
        apat = root.findViewById(R.id.etApat);
        amat = root.findViewById(R.id.etAmat);
        ndoc = root.findViewById(R.id.etNDoc);
        cel = root.findViewById(R.id.etCel);
        email = root.findViewById(R.id.etCorreo);
        direccion = root.findViewById(R.id.etDir);
        fechaNac = root.findViewById(R.id.etFnac);
        usuario = root.findViewById(R.id.etUsu);
        contrasena = root.findViewById(R.id.etPass);
        estado = root.findViewById(R.id.etEstado);

        spCargo = root.findViewById(R.id.spinnerCargo);
        spCargo.setOnItemSelectedListener(this);

        btnRegistrar = root.findViewById(R.id.btnGuardar);
        btnRegistrar.setOnClickListener(this);

        obtenerCargos();

        return root;
    }

    // ---------- onClick ----------
    @Override
    public void onClick(View v) {
        String nom = nombres.getText().toString().trim();
        String pat = apat.getText().toString().trim();
        String mat = amat.getText().toString().trim();
        String doc = ndoc.getText().toString().trim();
        String telefono = cel.getText().toString().trim();
        String correo = email.getText().toString().trim();
        String dir = direccion.getText().toString().trim();
        String fecha = fechaNac.getText().toString().trim();
        String usu = usuario.getText().toString().trim();
        String pass = contrasena.getText().toString().trim();
        String estText = estado.getText().toString().trim();

        if (nom.isEmpty() || pat.isEmpty() || mat.isEmpty() || doc.isEmpty() || telefono.isEmpty()
                || correo.isEmpty() || dir.isEmpty() || fecha.isEmpty() || usu.isEmpty() || pass.isEmpty() || estText.isEmpty()) {
            Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int est;
        try {
            est = Integer.parseInt(estText);
            if (est != 0 && est != 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Estado debe ser 0 (Inactivo) o 1 (Activo)", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idCargo == -1) {
            Toast.makeText(getActivity(), "Seleccione un cargo válido", Toast.LENGTH_SHORT).show();
            return;
        }

        registrarEmpleado(nom, pat, mat, doc, telefono, correo, dir, fecha, usu, pass, est, idCargo);
    }

    // ---------- Registro de Empleado ----------
    private void registrarEmpleado(String nom, String pat, String mat, String doc, String cel, String correo,
                                   String dir, String fecha, String usu, String pass, int est, int idCargo) {

        String url = servidor + "empleado_registrar.php";

        RequestParams params = new RequestParams();
        params.put("nombres", nom);
        params.put("apat", pat);
        params.put("amat", mat);
        params.put("ndoc", doc);
        params.put("cel", cel);
        params.put("correo", correo);
        params.put("direccion", dir);
        params.put("fecha", fecha);
        params.put("usuario", usu);
        params.put("clave", pass);
        params.put("estado", est);
        params.put("id_cargo", idCargo);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody).trim();
                Toast.makeText(getActivity(), "Respuesta: " + respuesta, Toast.LENGTH_LONG).show();
                limpiarCampos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // ---------- Cargar Cargos desde WebService ----------
    private void obtenerCargos() {
        String url = servidor + "cargo_mostrar.php";

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    ArrayList<Item> lista = new ArrayList<>();
                    lista.add(new Item(-1, "Seleccione Cargo"));

                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    for (int i = 0; i < jsonArray.length(); i++) {
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
                    spCargo.setAdapter(adapter);

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al cargar cargos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error de conexión al cargar cargos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ---------- onItemSelected ----------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Item item = (Item) parent.getItemAtPosition(position);
        idCargo = item.getId();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // Nada
    }

    // ---------- Utilitario ----------
    private void limpiarCampos() {
        nombres.setText("");
        apat.setText("");
        amat.setText("");
        ndoc.setText("");
        cel.setText("");
        email.setText("");
        direccion.setText("");
        fechaNac.setText("");
        usuario.setText("");
        contrasena.setText("");
        estado.setText("");
        spCargo.setSelection(0);
        nombres.requestFocus();
    }
}
