package com.example.tiendaapp2.ui.producto;

import static com.example.tiendaapp2.ui.producto.ProductoFragment.EditarProducto;
import static com.example.tiendaapp2.ui.producto.ProductoFragment.EliminarProducto;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;


import com.example.tiendaapp2.R;
import com.example.tiendaapp2.ui.network.OpenRouterAPI;
import com.example.tiendaapp2.ui.network.PeticionIA;
import com.example.tiendaapp2.ui.network.RespuestaIA;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProductoAdapter extends BaseAdapter {

    private Context context;
    private List<Producto> productos;

    public ProductoAdapter(Context context, List<Producto> productos) {
        this.context = context;
        this.productos = productos;
    }

    @Override
    public int getCount() {
        return productos.size();
    }

    @Override
    public Object getItem(int position) {
        return productos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Inflar el layout del item
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_producto, parent, false);
        }

        // Obtener los datos del producto
        Producto producto = productos.get(position);

        // Referenciar las vistas
        TextView tvId = convertView.findViewById(R.id.tvId);
        TextView tvCod = convertView.findViewById(R.id.tvCodigo);
        TextView tvNom = convertView.findViewById(R.id.tvNombre);
        TextView tvCat = convertView.findViewById(R.id.tvCategoria);
        TextView tvMar = convertView.findViewById(R.id.tvMarca);
        TextView tvDes = convertView.findViewById(R.id.tvDescripcion);
        TextView tvPcomp = convertView.findViewById(R.id.tvPreccomp);
        TextView tvPvent = convertView.findViewById(R.id.tvPrecvent);
        TextView tvStock = convertView.findViewById(R.id.tvStock);
        Button btnEditar = convertView.findViewById(R.id.btnEditar);
        Button btnEliminar = convertView.findViewById(R.id.btnEliminar);
        Button btnIA = convertView.findViewById(R.id.btnIA);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Editar "+producto.getId(),Toast.LENGTH_LONG).show();
                EditarProducto(producto.getId(), context);
            }
        });

        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(context.getApplicationContext(),"Eliminar "+producto.getId(),Toast.LENGTH_LONG).show();
               EliminarProducto(producto.getId(), context);
            }
        });

        btnIA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarRespuestaIA(producto, context);
            }
        });

        // Establecer los valores
        tvId.setText(String.valueOf(producto.getId()));
        tvCod.setText("Código: "+producto.getCodigo());
        tvNom.setText("Nombre: "+producto.getNombre());
        tvCat.setText("Categoría: "+producto.getCategoria());
        tvMar.setText("Marca:"+producto.getMarca());
        tvDes.setText("Descripción: "+producto.getDescripcion());
        tvPcomp.setText("Precio Compra: "+String.valueOf(producto.getPcompra()));
        tvPvent.setText("Precio Venta: "+String.valueOf(producto.getPventa()));
        tvStock.setText("Stock: "+producto.getStock());

        return convertView;
    }

    private void mostrarRespuestaIA(Producto producto, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_ia, null);

        WebView webView = dialogView.findViewById(R.id.webRespuestaIA);
        webView.getSettings().setJavaScriptEnabled(false);
        webView.setBackgroundColor(Color.TRANSPARENT);

        builder.setView(dialogView);
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        // Construir el prompt
        String prompt = "Describe el producto \"" + producto.getNombre() + "\". " +
                "Además, brinda consejos para venderlo de manera efectiva. ";

        // Crear el cuerpo de la solicitud
        List<PeticionIA.MensajeIA> mensajes = new ArrayList<>();
        mensajes.add(new PeticionIA.MensajeIA("user", prompt));
        PeticionIA peticion = new PeticionIA("deepseek/deepseek-r1:free", mensajes);

        // Convertir a JSON
        Gson gson = new Gson();
        String jsonBody = gson.toJson(peticion);
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonBody);

        // Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://openrouter.ai/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OpenRouterAPI api = retrofit.create(OpenRouterAPI.class);
        Call<RespuestaIA> call = api.enviarPrompt("Bearer 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", requestBody);

        // Llamada asíncrona
        call.enqueue(new Callback<RespuestaIA>() {
            @Override
            public void onResponse(Call<RespuestaIA> call, Response<RespuestaIA> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String respuestaIA = response.body().choices.get(0).message.content;
                    String html = "<html><body style='font-family:sans-serif; font-size:16px; color:#333333;'>" +
                            respuestaIA.replace("\n", "<br>")
                                    .replace("**", "<b>")
                                    .replace("*", "<i>") +
                            "</body></html>";

                    webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                } else {
                    String errorHtml = "<html><body style='font-family:sans-serif; color:#b00020;'><b>Error:</b> en la respuesta del servidor.</body></html>";
                    webView.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
                }
            }

            @Override
            public void onFailure(Call<RespuestaIA> call, Throwable t) {
                String errorHtml = "<html><body style='font-family:sans-serif; color:#b00020;'>" +
                        "<b>Error de conexión:</b> " + t.getMessage() +
                        "</body></html>";
                webView.loadDataWithBaseURL(null, errorHtml, "text/html", "UTF-8", null);
            }
        });
    }


}
