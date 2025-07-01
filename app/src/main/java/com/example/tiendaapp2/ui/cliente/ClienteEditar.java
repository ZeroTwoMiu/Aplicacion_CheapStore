package com.example.tiendaapp2.ui.cliente;

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

public class ClienteEditar extends Fragment implements View.OnClickListener {

    private int idCliente;

    private EditText etNom, etApat, etAmat, etDoc, etCel, etCorreo, etDir, etFnac, etEstado;
    private Button btnGuardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cliente_editar, container, false);

        etNom = root.findViewById(R.id.etNomE);
        etApat = root.findViewById(R.id.etApatE);
        etAmat = root.findViewById(R.id.etAmatE);
        etDoc = root.findViewById(R.id.etNDocE);
        etCel = root.findViewById(R.id.etCelE);
        etCorreo = root.findViewById(R.id.etCorreoE);
        etDir = root.findViewById(R.id.etDirE);
        etFnac = root.findViewById(R.id.etFnacE);
        etEstado = root.findViewById(R.id.etEstadoE);
        btnGuardar = root.findViewById(R.id.btnGuardarE);

        btnGuardar.setOnClickListener(this);

        idCliente = getArguments().getInt("idCliente", 0);

        consultarCliente(idCliente);

        return root;
    }

    private void consultarCliente(int id) {
        String url = servidor + "cliente_consultar.php";

        RequestParams params = new RequestParams();
        params.put("idCliente", id);

        new AsyncHttpClient().get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONArray array = new JSONArray(new String(responseBody));
                    JSONObject obj = array.getJSONObject(0);

                    etNom.setText(obj.getString("nom_cliente"));
                    etApat.setText(obj.getString("apat_cliente"));
                    etAmat.setText(obj.getString("amat_cliente"));
                    etDoc.setText(obj.getString("ndoc_cliente"));
                    etCel.setText(obj.getString("cel_cliente"));
                    etCorreo.setText(obj.getString("em_cliente"));
                    etDir.setText(obj.getString("dir_cliente"));
                    etFnac.setText(obj.getString("fn_cliente"));
                    etEstado.setText(obj.getString("est_cliente"));
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
        String apat = etApat.getText().toString().trim();
        String amat = etAmat.getText().toString().trim();
        String doc = etDoc.getText().toString().trim();
        String cel = etCel.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String dir = etDir.getText().toString().trim();
        String fnac = etFnac.getText().toString().trim();
        String estText = etEstado.getText().toString().trim();

        if (nom.isEmpty() || apat.isEmpty() || amat.isEmpty() || doc.isEmpty() || cel.isEmpty() || correo.isEmpty() || dir.isEmpty() || fnac.isEmpty() || estText.isEmpty()) {
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

        actualizarCliente(idCliente, nom, apat, amat, doc, cel, correo, dir, fnac, estado);
    }

    private void actualizarCliente(int idCliente, String nom, String apat, String amat, String doc, String cel, String correo, String dir, String fnac, int estado) {
        String url = servidor + "cliente_actualizar.php";

        RequestParams params = new RequestParams();
        params.put("idCliente", idCliente);
        params.put("nombre", nom);
        params.put("apat", apat);
        params.put("amat", amat);
        params.put("ndoc", doc);
        params.put("cel", cel);
        params.put("correo", correo);
        params.put("direccion", dir);
        params.put("fnac", fnac);
        params.put("estado", estado);

        new AsyncHttpClient().get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "Cliente actualizado correctamente", Toast.LENGTH_SHORT).show();
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_content_main);
                navController.navigate(R.id.action_nav_cliente_editar_to_nav_cliente);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
