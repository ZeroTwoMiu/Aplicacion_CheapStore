package com.example.tiendaapp2.ui.cliente;

import static com.example.tiendaapp2.Login.servidor;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.fragment.app.Fragment;

import com.example.tiendaapp2.R;
import com.loopj.android.http.*;

import cz.msebera.android.httpclient.Header;

public class ClienteAgregar extends Fragment implements View.OnClickListener {

    private EditText etNombre, etApat, etAmat, etNDoc, etCel, etCorreo, etDir, etFnac, etEstado;
    private Button btnGuardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cliente_agregar, container, false);

        etNombre = root.findViewById(R.id.etNom);
        etApat = root.findViewById(R.id.etApat);
        etAmat = root.findViewById(R.id.etAmat);
        etNDoc = root.findViewById(R.id.etNDoc);
        etCel = root.findViewById(R.id.etCel);
        etCorreo = root.findViewById(R.id.etCorreo);
        etDir = root.findViewById(R.id.etDir);
        etFnac = root.findViewById(R.id.etFnac);
        etEstado = root.findViewById(R.id.etEstado);

        btnGuardar = root.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(this);

        return root;
    }

    @Override
    public void onClick(View v) {
        String nombre = etNombre.getText().toString().trim();
        String apat = etApat.getText().toString().trim();
        String amat = etAmat.getText().toString().trim();
        String ndoc = etNDoc.getText().toString().trim();
        String cel = etCel.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String direccion = etDir.getText().toString().trim();
        String fnac = etFnac.getText().toString().trim();
        String estadoTexto = etEstado.getText().toString().trim();

        if (nombre.isEmpty() || apat.isEmpty() || amat.isEmpty() || ndoc.isEmpty() || cel.isEmpty()
                || correo.isEmpty() || direccion.isEmpty() || fnac.isEmpty() || estadoTexto.isEmpty()) {
            Toast.makeText(getActivity(), "Complete todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int estado;
        try {
            estado = Integer.parseInt(estadoTexto);
            if (estado != 0 && estado != 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            Toast.makeText(getActivity(), "Estado debe ser 0 o 1", Toast.LENGTH_SHORT).show();
            return;
        }

        registrarCliente(nombre, apat, amat, ndoc, cel, correo, direccion, fnac, estado);
    }

    private void registrarCliente(String nombre, String apat, String amat, String ndoc, String cel, String correo, String dir, String fnac, int estado) {
        String url = servidor + "cliente_registrar.php";

        RequestParams params = new RequestParams();
        params.put("nombre", nombre);
        params.put("apat", apat);
        params.put("amat", amat);
        params.put("ndoc", ndoc);
        params.put("cel", cel);
        params.put("correo", correo);
        params.put("direccion", dir);
        params.put("fnac", fnac);
        params.put("estado", estado);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Toast.makeText(getActivity(), "Cliente registrado correctamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "Error al registrar: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etApat.setText("");
        etAmat.setText("");
        etNDoc.setText("");
        etCel.setText("");
        etCorreo.setText("");
        etDir.setText("");
        etFnac.setText("");
        etEstado.setText("");
        etNombre.requestFocus();
    }
}
