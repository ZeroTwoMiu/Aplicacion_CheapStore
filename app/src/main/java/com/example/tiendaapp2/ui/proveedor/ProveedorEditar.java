package com.example.tiendaapp2.ui.proveedor;

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
import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProveedorEditar extends Fragment implements View.OnClickListener {

    private int idProveedor;

    private EditText etNom, etDoc, etCel, etCorreo, etDir, etEstado;
    private Button btnGuardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_proveedor_editar, container, false);

        etNom = root.findViewById(R.id.etNomE);
        etDoc = root.findViewById(R.id.etNDocE);
        etCel = root.findViewById(R.id.etCelE);
        etCorreo = root.findViewById(R.id.etCorreoE);
        etDir = root.findViewById(R.id.etDirE);
        etEstado = root.findViewById(R.id.etEstadoE);
        btnGuardar = root.findViewById(R.id.btnGuardarE);

        btnGuardar.setOnClickListener(this);

        idProveedor = getArguments().getInt("idProveedor", 0);

        consultarProveedor(idProveedor);

        return root;
    }

    private void consultarProveedor(int id) {
        String url = servidor + "proveedor_consultar.php";

        RequestParams params = new RequestParams();
        params.put("idProveedor", id);

        new AsyncHttpClient().get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    JSONObject obj = array.getJSONObject(0);

                    etNom.setText(obj.getString("nom_proveedor"));
                    etDoc.setText(obj.getString("ndoc_proveedor"));
                    etCel.setText(obj.getString("cel_proveedor"));
                    etCorreo.setText(obj.getString("em_proveedor"));
                    etDir.setText(obj.getString("dir_proveedor"));
                    etEstado.setText(obj.getString("est_proveedor"));
                } catch (JSONException e) {
                    Toast.makeText(getActivity(), "Error al parsear datos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        String nom = etNom.getText().toString().trim();
        String doc = etDoc.getText().toString().trim();
        String cel = etCel.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String dir = etDir.getText().toString().trim();
        String estText = etEstado.getText().toString().trim();

        if (nom.isEmpty() || doc.isEmpty() || cel.isEmpty() || correo.isEmpty() || dir.isEmpty() || estText.isEmpty()) {
            Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int estado;
        try {
            estado = Integer.parseInt(estText);
            if (estado != 0 && estado != 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Estado debe ser 0 o 1", Toast.LENGTH_SHORT).show();
            return;
        }

        actualizarProveedor(idProveedor, nom, doc, cel, correo, dir, estado);
    }

    private void actualizarProveedor(int idProveedor, String nom, String doc, String cel, String correo, String dir, int estado) {
        String url = servidor + "proveedor_actualizar.php";

        RequestParams params = new RequestParams();
        params.put("idProveedor", idProveedor);
        params.put("nombre", nom);
        params.put("ndoc", doc);
        params.put("cel", cel);
        params.put("correo", correo);
        params.put("direccion", dir);
        params.put("estado", estado);

        new AsyncHttpClient().get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "Proveedor actualizado correctamente", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_proveedor_editar_to_nav_proveedor);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
