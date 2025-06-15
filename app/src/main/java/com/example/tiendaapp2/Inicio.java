package com.example.tiendaapp2;

import static com.example.tiendaapp2.Login.servidor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class Inicio extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_inicio);

        // Ocultar la barra de estado y navegación (modo inmersivo)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom);
            return insets;
        });


        //Despues de 3 segundos abrir Login
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Accerder();
                finish();
            }
        }, 3000); // 3000 milisegundos = 3 segundos


    }

    public void Accerder()
    {
        //verificar si hay datos guardados en SharedPreferences
        String username = getSharedPreferences("datos", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("datos", MODE_PRIVATE).getString("password", "");
        if (!username.isEmpty() && !password.isEmpty()) {
            IniciarSesion(username, password);
        }
        else {
            Intent intent = new Intent(Inicio.this, Login.class);
            startActivity(intent);
        }
    }

    private void IniciarSesion(String username, String password) {


        String url = servidor+"usuario_autentificar.php";

        RequestParams requestParams = new RequestParams();
        requestParams.put("username", username);
        requestParams.put("password", password);

        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                String respuesta = new String(responseBody);
                JSONArray jsonArray = null;
                try {
                    jsonArray = new JSONArray(respuesta);

                    if(jsonArray.length()==0)
                    {
                        Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        String id_empleado="";
                        String nom_empleado="";
                        String em_empleado="";
                        String foto_empleado="";

                        // Recorrer el array JSON y agregar cada contacto a la lista
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject contactoJson = jsonArray.getJSONObject(i);
                            id_empleado = contactoJson.getString("id_empleado");
                            nom_empleado = contactoJson.getString("nom_empleado");
                            em_empleado = contactoJson.getString("em_empleado");
                            foto_empleado = contactoJson.getString("foto_empleado");
                        }

                        Toast.makeText(getApplicationContext(), "Bienvenido " + nom_empleado, Toast.LENGTH_LONG).show();

                        //guardar datos en SharedPreferences
                        GuardarSharedPreferences(username, password,id_empleado,nom_empleado,em_empleado,foto_empleado);

                        Intent intent = new Intent(Inicio.this, MainActivity.class);
                        intent.putExtra("id", respuesta);
                        startActivity(intent);
                        finish();

                    }


                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }




            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                String mensaje = "Error: " + statusCode + " - " + error.getMessage();
                Toast.makeText(getApplicationContext(),mensaje,Toast.LENGTH_LONG).show();

            }
        });


    }

    private void GuardarSharedPreferences(String username, String password, String id_empleado, String nom_empleado, String em_empleado, String foto_empleado) {
        //guardar usuario y contraseña en modo privado
        getSharedPreferences("datos", MODE_PRIVATE).edit()
                .putString("username", username)
                .putString("password", password)
                .putString("id_empleado", id_empleado)
                .putString("nom_empleado", nom_empleado)
                .putString("em_empleado", em_empleado)
                .putString("foto_empleado", foto_empleado)
                .apply();

    }
}