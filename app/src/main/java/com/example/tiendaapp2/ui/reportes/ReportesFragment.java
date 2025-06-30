package com.example.tiendaapp2.ui.reportes;

import static com.example.tiendaapp2.Login.servidor;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.ProductoCallback;
import com.example.tiendaapp2.ui.producto.Producto;
import com.itextpdf.text.Document;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.util.ArrayList;

import cz.msebera.android.httpclient.entity.mime.Header;


public class ReportesFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private ListView lstReportes;
    private Button btnDescargarReporte;
    private Button btnGraficoVenta;
    private Button btnGraficoCompra;

    private ArrayList<String> opcionesReporte;

    private int reporteSeleccionado = -1; // Para saber qué opción se eligió

    public ReportesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        // Vincular componentes
        lstReportes = view.findViewById(R.id.lstReportes);
        btnDescargarReporte = view.findViewById(R.id.btnDescargarReporte);

        btnGraficoVenta = view.findViewById(R.id.btnGraficoVentas);
        btnGraficoCompra = view.findViewById(R.id.btnGraficoCompras);

        btnGraficoVenta.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_nav_reportes_to_nav_venta_grafico);
        });

        btnGraficoCompra.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(view);
            navController.navigate(R.id.action_nav_reportes_to_nav_compra_grafico);
        });

        // Lista de opciones
        opcionesReporte = new ArrayList<>();
        opcionesReporte.add("Reporte de Productos");

        // Adaptador
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, opcionesReporte);
        lstReportes.setAdapter(adapter);

        // Listener de selección
        lstReportes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                reporteSeleccionado = position;
                Toast.makeText(getContext(), "Seleccionado: " + opcionesReporte.get(position), Toast.LENGTH_SHORT).show();
            }
        });

        // Acción del botón
        btnDescargarReporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reporteSeleccionado == -1) {
                    Toast.makeText(getContext(), "Seleccione un tipo de reporte primero.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Aquí iría la lógica futura para generar o descargar
                String opcion = opcionesReporte.get(reporteSeleccionado);
                if (opcion.equals("Reporte de Productos")) {
                    generarReporteProductos(); // Crear esta función luego
                }
            }
        });

        return view;
    }

    private void generarReporteProductos() {
        obtenerProductos(new ProductoCallback() {
            @Override
            public void onProductosObtenidos(ArrayList<Producto> productos) {
                try {
                    String fileName = "Reporte_Productos_" + System.currentTimeMillis() + ".pdf";

                    // Crear contenido en MediaStore para Descargas
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                    values.put(MediaStore.Downloads.IS_PENDING, 1);

                    ContentResolver resolver = requireContext().getContentResolver();
                    Uri collection = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);
                    }
                    Uri fileUri = resolver.insert(collection, values);

                    if (fileUri == null) {
                        Toast.makeText(getContext(), "No se pudo crear el archivo PDF", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    OutputStream out = resolver.openOutputStream(fileUri);

                    // Crear documento PDF
                    Document document = new Document();
                    PdfWriter.getInstance(document, out);
                    document.open();

                    document.add(new Paragraph("Reporte de Productos", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18)));
                    document.add(new Paragraph(" "));

                    PdfPTable table = new PdfPTable(4);
                    table.addCell("Código");
                    table.addCell("Nombre");
                    table.addCell("Precio Venta");
                    table.addCell("Stock");

                    for (Producto p : productos) {
                        table.addCell(String.valueOf(p.getId()));
                        table.addCell(p.getNombre());
                        table.addCell(String.valueOf(p.getPventa()));
                        table.addCell(String.valueOf(p.getPcompra()));
                    }

                    document.add(table);
                    document.close();
                    out.close();

                    // Marcar como no pendiente (disponible para apps/usuarios)
                    values.clear();
                    values.put(MediaStore.Downloads.IS_PENDING, 0);
                    resolver.update(fileUri, values, null, null);

                    Toast.makeText(getContext(), "PDF guardado en Descargas", Toast.LENGTH_LONG).show();

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Error al guardar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(String mensaje) {
                Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
            }
        });
    }


    public void obtenerProductos(ProductoCallback callback) {
        String url = servidor + "producto_mostrar.php";
        RequestParams requestParams = new RequestParams();

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                try {
                    JSONArray jsonArray = new JSONArray(new String(responseBody));
                    ArrayList<Producto> productosList = new ArrayList<>();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
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
                        productosList.add(producto);
                    }
                    callback.onProductosObtenidos(productosList);
                } catch (JSONException e) {
                    callback.onError("Error al parsear datos");
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                callback.onError("Error: " + statusCode + " - " + error.getMessage());

            }
        });
    }

}