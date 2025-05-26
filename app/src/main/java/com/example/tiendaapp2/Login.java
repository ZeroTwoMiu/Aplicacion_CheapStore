package com.example.tiendaapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class Login extends AppCompatActivity implements View.OnClickListener {

    public static final String servidor = "http://10.0.2.2/cheapstore/";

    EditText usuarioEditText;
    EditText contraEditText;
    Button loginButton;
    TextView signupText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usuarioEditText = findViewById(R.id.usuario);
        contraEditText = findViewById(R.id.contra);
        loginButton = findViewById(R.id.loginButton);
        signupText = findViewById(R.id.signupText);

        loginButton.setOnClickListener(this);


        signupText.setOnClickListener(v -> {
            Toast.makeText(this, "Redirigir a registro (a implementar)", Toast.LENGTH_SHORT).show();
            // Puedes lanzar una nueva actividad aquí
            // startActivity(new Intent(Login.this, RegistroActivity.class));
        });

        // Autologin si hay datos guardados
        String username = getSharedPreferences("datos", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("datos", MODE_PRIVATE).getString("password", "");
        if (!username.isEmpty() && !password.isEmpty()) {
            IniciarSesion(username, password);
        }

        // Aplicar insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.loginButton) {
            String username = usuarioEditText.getText().toString();
            String password = contraEditText.getText().toString();
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                IniciarSesion(username, password);
            }
        }
    }

    private void IniciarSesion(String username, String password) {
        String url = servidor + "usuario_autentificar.php";

        RequestParams requestParams = new RequestParams();
        requestParams.put("username", username);
        requestParams.put("password", password);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, requestParams, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONArray jsonArray = new JSONArray(respuesta);
                    if (jsonArray.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show();
                    } else {
                        JSONObject usuario = jsonArray.getJSONObject(0);
                        String id_empleado = usuario.getString("id_empleado");
                        String nom_empleado = usuario.getString("nom_empleado");
                        String em_empleado = usuario.getString("em_empleado");

                        Toast.makeText(getApplicationContext(), "Bienvenido " + nom_empleado, Toast.LENGTH_LONG).show();
                        GuardarSharedPreferences(username, password, id_empleado, nom_empleado, em_empleado);

                        Intent intent = new Intent(Login.this, MainActivity.class);
                        intent.putExtra("id", respuesta);
                        startActivity(intent);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al procesar la respuesta del servidor", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void GuardarSharedPreferences(String username, String password, String id_empleado, String nom_empleado, String em_empleado) {
        getSharedPreferences("datos", MODE_PRIVATE).edit()
                .putString("username", username)
                .putString("password", password)
                .putString("id_empleado", id_empleado)
                .putString("nom_empleado", nom_empleado)
                .putString("em_empleado", em_empleado)
                .apply();
    }
}