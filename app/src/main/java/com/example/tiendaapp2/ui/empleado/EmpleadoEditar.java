package com.example.tiendaapp2.ui.empleado;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.Callback;
import com.example.tiendaapp2.ui.Item;
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class EmpleadoEditar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    int idCargo = -1;
    int idEmpleado;
    private ArrayList<Item> lista_cargo = new ArrayList<>();

    private EditText etNom, etApat, etAmat, etDoc, etCel, etCorreo, etDir, etFecha, etUsuario, etPass;
    private Spinner spCargo;
    private Button btnGuardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_empleado_editar, container, false);

        etNom = root.findViewById(R.id.etNomE);
        etApat = root.findViewById(R.id.etApatE);
        etAmat = root.findViewById(R.id.etAmatE);
        etDoc = root.findViewById(R.id.etNDocE);
        etCel = root.findViewById(R.id.etCelE);
        etCorreo = root.findViewById(R.id.etCorreoE);
        etDir = root.findViewById(R.id.etDirE);
        etFecha = root.findViewById(R.id.etFnacE);
        etUsuario = root.findViewById(R.id.etUsuE);
        etPass = root.findViewById(R.id.etPassE);
        spCargo = root.findViewById(R.id.spinnerCargoE);
        btnGuardar = root.findViewById(R.id.btnGuardarE);

        spCargo.setOnItemSelectedListener(this);
        btnGuardar.setOnClickListener(this);

        idEmpleado = getArguments().getInt("idEmpleado", 0);

        obtenerCargo(new Callback() {
            @Override
            public void onSuccess() {
                consultarEmpleado(idEmpleado);
            }
        });

        return root;
    }

    private void consultarEmpleado(int id) {
        String url = servidor + "empleado_consultar.php";

        RequestParams params = new RequestParams();
        params.put("idEmpleado", id);

        new AsyncHttpClient().get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    JSONObject obj = array.getJSONObject(0);

                    etNom.setText(obj.getString("nom_empleado"));
                    etApat.setText(obj.getString("apat_empleado"));
                    etAmat.setText(obj.getString("amat_empleado"));
                    etDoc.setText(obj.getString("ndoc_empleado"));
                    etCel.setText(obj.getString("cel_empleado"));
                    etCorreo.setText(obj.getString("em_empleado"));
                    etDir.setText(obj.getString("dir_empleado"));
                    etFecha.setText(obj.getString("fn_empleado"));
                    etUsuario.setText(obj.getString("usu_empleado"));
                    etPass.setText(obj.getString("pass_empleado"));

                    int idCargoBD = obj.getInt("id_cargo");

                    for (int i = 0; i < lista_cargo.size(); i++) {
                        if (lista_cargo.get(i).getId() == idCargoBD) {
                            spCargo.setSelection(i);
                            break;
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al parsear datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void obtenerCargo(Callback callback) {
        String url = servidor + "empleado_cargo.php";

        new AsyncHttpClient().get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                lista_cargo.clear();
                lista_cargo.add(new Item(-1, "Seleccione Cargo"));

                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject obj = array.getJSONObject(i);
                        lista_cargo.add(new Item(obj.getInt("id"), obj.getString("nombre")));
                    }

                    ArrayAdapter<Item> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, lista_cargo);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCargo.setAdapter(adapter);

                    callback.onSuccess();

                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error cargando cargos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btnGuardar) {
            String nom = etNom.getText().toString();
            String apat = etApat.getText().toString();
            String amat = etAmat.getText().toString();
            String doc = etDoc.getText().toString();
            String cel = etCel.getText().toString();
            String email = etCorreo.getText().toString();
            String dir = etDir.getText().toString();
            String fecha = etFecha.getText().toString();
            String usu = etUsuario.getText().toString();
            String pass = etPass.getText().toString();

            if (nom.isEmpty() || apat.isEmpty() || doc.isEmpty() || usu.isEmpty() || pass.isEmpty() || idCargo == -1) {
                Toast.makeText(getActivity(), "Complete todos los campos requeridos", Toast.LENGTH_SHORT).show();
            } else {
                actualizarEmpleado(idEmpleado, idCargo, nom, apat, amat, doc, cel, email, dir, fecha, usu, pass);
            }
        }
    }

    private void actualizarEmpleado(int idEmpleado, int idCargo, String nom, String apat, String amat, String doc, String cel, String email, String dir, String fecha, String usu, String pass) {
        String url = servidor + "empleado_actualizar.php";

        RequestParams params = new RequestParams();
        params.put("idEmpleado", idEmpleado);
        params.put("idCargo", idCargo);
        params.put("nom", nom);
        params.put("apat", apat);
        params.put("amat", amat);
        params.put("doc", doc);
        params.put("cel", cel);
        params.put("email", email);
        params.put("dir", dir);
        params.put("fecha", fecha);
        params.put("usu", usu);
        params.put("pass", pass);
        params.put("estado", 1);

        new AsyncHttpClient().get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "Empleado actualizado correctamente", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_empleado_editar_to_nav_empleado);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == spCargo) {
            idCargo = ((Item) parent.getItemAtPosition(position)).getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
