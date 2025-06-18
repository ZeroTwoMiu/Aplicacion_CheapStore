package com.example.tiendaapp2.ui.producto;

import static com.example.tiendaapp2.Login.servidor;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.Item;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.google.zxing.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class ProductoAgregar extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    // -------------------- 1. Variables globales --------------------
    int idCategoria = -1;
    int idMarca = -1;
    private EditText cod, nom, des, pv, pc;
    private Spinner cat, mar;
    private Button reg;
    private FloatingActionButton floEscaner;

    // -------------------- 2. ActivityResultLaunchers --------------------
    private final ActivityResultLauncher<ScanOptions> escanerLauncher =
            registerForActivityResult(new ScanContract(), result -> {
                if (result.getContents() != null) {
                    procesarRegistro(result.getContents());
                }
            });

    private final ActivityResultLauncher<String> galeriaLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri !=null){
                    decodificarQRdesdeImagen(uri);
                }
            });

    // -------------------- 3. onCreateView --------------------
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

        floEscaner = rootView.findViewById(R.id.flo_escaner);
        floEscaner.setOnClickListener(v -> mostrarOpcionesEscaneo());
        ObtenerCategoria();
        ObtenerMarca();

        return rootView;
    }

    // -------------------- 4. onClick --------------------
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
            } else {
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

    // -------------------- 5. onItemSelected + onNothingSelected --------------------
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == cat) //Seleccionando item en spinner Categoria
        {
            Item itemSeleccionado = (Item) parent.getItemAtPosition(position);
            idCategoria = itemSeleccionado.getId();
            String nomCategoria = itemSeleccionado.getNombre();
            //Toast.makeText(getActivity(), "ID seleccionado: " + idCategoria+" "+nomCategoria, Toast.LENGTH_SHORT).show();
        } else if (parent == mar) //Seleccionando item en spinner Marca
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

    // -------------------- 6. Escaneo de QR y opciones --------------------
    private void mostrarOpcionesEscaneo() {
        String[] opciones = {"Escanear con camara", "Seleccionar Imagen"};
        new AlertDialog.Builder(getContext())
                .setTitle("Selecciona una opción")
                .setItems(opciones, (dialog, which) -> {
                    if (which == 0) {
                        iniciarEscaneoCamara();
                    } else {
                        seleccionarImagenDeGaleria();
                    }
                }).show();
    }

    private void seleccionarImagenDeGaleria() {
        galeriaLauncher.launch("image/*");
    }

    private void iniciarEscaneoCamara() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanea el código del producto");
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureActivityPortrait.class);
        escanerLauncher.launch(options);
    }

    private void decodificarQRdesdeImagen(Uri uri) {
        try{
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();
            int[] pixels = new int[width * height];
            bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

            RGBLuminanceSource source = new RGBLuminanceSource(width,height,pixels);
            BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));

            Reader reader = new MultiFormatReader();
            Result result = reader.decode(bBitmap);
            procesarRegistro(result.getText());

        } catch (Exception e){
            Toast.makeText(getContext(), "No se pudo leer el código", Toast.LENGTH_SHORT).show();
        }
    }

    // -------------------- 7. Procesamiento y registro --------------------

    private void procesarRegistro(String datosQR) {
        try {
            String[] partes = datosQR.split("\\|");

            if (partes.length < 7) {
                Toast.makeText(getContext(), "QR incompleto. Se esperaban 7 campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            cod.setText(partes[0]);
            nom.setText(partes[1]);
            des.setText(partes[2]);
            pc.setText(partes[3]);
            pv.setText(partes[4]);

            String nombreCategoria = partes[5].trim();
            String nombreMarca = partes[6].trim();


            seleccionarItemPorNombre(cat, nombreCategoria);
            seleccionarItemPorNombre(mar, nombreMarca);

            new Handler().postDelayed(() -> {
                String codigo = cod.getText().toString().trim();
                String nombre = nom.getText().toString().trim();
                String descripcion = des.getText().toString().trim();
                double preComp = Double.parseDouble(pc.getText().toString().trim());
                double preVent = Double.parseDouble(pv.getText().toString().trim());

                RegistrarProducto(codigo, nombre, descripcion, preComp, preVent, idCategoria, idMarca);
            }, 500);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Error al procesar QR", Toast.LENGTH_SHORT).show();
        }
    }

    private void RegistrarProducto(String codigo, String nombre, String descripcion, double preComp, double preVent, int idCategoria, int idMarca) {

        //Declarar la URL
        String url = servidor + "producto_registrar.php";

        //Enviar parámetros
        RequestParams requestParams = new RequestParams();
        requestParams.put("codigo", codigo);
        requestParams.put("nombre", nombre);
        requestParams.put("descripcion", descripcion);
        requestParams.put("preComp", preComp);
        requestParams.put("preVent", preVent);
        requestParams.put("idCategoria", idCategoria);
        requestParams.put("idMarca", idMarca);

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
                Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
            }
        });

    }

    // -------------------- 8. Utilitarios --------------------
    private void seleccionarItemPorNombre(Spinner spinner, String nombreBuscado) {
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            Item item = (Item) adapter.getItem(i);
            if (item.getNombre().equalsIgnoreCase(nombreBuscado)) {
                spinner.setSelection(i);
                break;
            }
        }
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

    private void LimpiarCampos() {
        cod.setText("");
        nom.setText("");
        des.setText("");
        pc.setText("");
        pv.setText("");
        cat.setSelection(0);
        mar.setSelection(0);
        cod.requestFocus();
    }



}