package com.example.tiendaapp2;


import static com.example.tiendaapp2.R.layout.custom_cambiar_password;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiendaapp2.databinding.ActivityMainBinding;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.httpclient.android.BuildConfig;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //acceder al nav_header_main
        View headerView = binding.navView.getHeaderView(0);

        //accerder a los textview del nav_header_main
        TextView tvMenuUsuario = headerView.findViewById(R.id.tvMenuUsuario);
        TextView tvMenuEmail = headerView.findViewById(R.id.tvMenuEmail);
        ImageView imgMenuFoto = headerView.findViewById(R.id.imgMenuFoto);

        //obtener datos de SharedPreferences
        String username = getSharedPreferences("datos", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("datos", MODE_PRIVATE).getString("password", "");
        String id_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("id_empleado", "");
        String nom_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("nom_empleado", "");
        String em_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("em_empleado", "");
        String foto_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("foto_empleado", "");
        String nom_cargo = getSharedPreferences("datos", MODE_PRIVATE).getString("nom_cargo", "");
        int opc_venta = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_venta", 0);
        int opc_compra = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_compra", 0);
        int opc_producto = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_producto", 0);
        int opc_cliente = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_cliente", 0);
        int opc_proveedor = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_proveedor", 0);
        int opc_empleado = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_empleado", 0);
        int opc_reportes = getSharedPreferences("datos", MODE_PRIVATE).getInt("opc_reportes", 0);


        //mostrar datos en los textview
        tvMenuUsuario.setText(nom_empleado);
        tvMenuEmail.setText(nom_cargo);

        //mostrar foto en el imageview
        //imgMenuFoto.getLayoutParams().height = 200;
        //imgMenuFoto.getLayoutParams().width = 200;
        //Glide.with(this).load(Login.servidor+foto_empleado).into(imgMenuFoto);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();

                //abrir una pagina web
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/CheapStorPe"));
                startActivity(intent);

            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_venta,R.id.nav_compra,R.id.nav_producto, R.id.nav_cliente, R.id.nav_proveedor,R.id.nav_empleado, R.id.nav_reportes)
                .setOpenableLayout(drawer)
                .build();
        VerificarAcceso(navigationView, opc_venta, opc_compra, opc_producto, opc_cliente, opc_proveedor, opc_empleado, opc_reportes);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        SharedPreferences preferences = getSharedPreferences("datos", MODE_PRIVATE);
        String nomCargo = preferences.getString("nom_cargo", "");
        mostrarDialogoCargo("", nomCargo); // puedes pasar "" como tipoCargo si ya no lo necesitas


    }

    private void VerificarAcceso(NavigationView navigationView, int opc_venta, int opc_compra, int opc_producto,
                                 int opc_cliente, int opc_proveedor, int opc_empleado, int opc_reportes) {
        if(opc_venta==0) //ocultar nav_venta
            navigationView.getMenu().findItem(R.id.nav_venta).setVisible(false);
        if(opc_compra==0) //ocultar nav_compra
            navigationView.getMenu().findItem(R.id.nav_compra).setVisible(false);
        if(opc_producto==0) //ocultar nav_producto
            navigationView.getMenu().findItem(R.id.nav_producto).setVisible(false);
        if(opc_cliente==0) //ocultar nav_cliente
            navigationView.getMenu().findItem(R.id.nav_cliente).setVisible(false);
        if(opc_proveedor==0) //ocultar nav_proveedor
            navigationView.getMenu().findItem(R.id.nav_proveedor).setVisible(false);
        if(opc_empleado==0) //ocultar nav_empleado
            navigationView.getMenu().findItem(R.id.nav_empleado).setVisible(false);
        if(opc_reportes==0) //ocultar nav_reportes
            navigationView.getMenu().findItem(R.id.nav_reportes).setVisible(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        //si presion action_logout se ejecuta la funcion logout
        menu.findItem(R.id.action_logout).setOnMenuItemClickListener(item -> {
            logout();
            return true;
        });

        menu.findItem(R.id.action_password).setOnMenuItemClickListener(item -> {
            modalCambiarContraseña();
            return true;
        });

        menu.findItem(R.id.action_datos).setOnMenuItemClickListener(item -> {
            modalCambiarDatos();
            return true;
        });

        menu.findItem(R.id.action_about).setOnMenuItemClickListener(item -> {
            modalAbout(); //mostrar información sobr el app
            return true;
        });

        return true;
    }


    private void modalAbout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.custom_about, null);

        TextView tvVersion = dialogView.findViewById(R.id.tvAboutVersion);
        String version = BuildConfig.VERSION_NAME;
        tvVersion.setText("Versión " + version);

        builder.setView(dialogView);
        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void modalCambiarDatos() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.custom_cambiar_datos, null);
        builder.setView(view);
        builder.setTitle("Actualizar Datos Personales");

        // Referencias a los EditText
        EditText etNom = view.findViewById(R.id.etNomEmpleado);
        EditText etApat = view.findViewById(R.id.etApatEmpleado);
        EditText etAmat = view.findViewById(R.id.etAmatEmpleado);
        EditText etNdoc = view.findViewById(R.id.etNdocEmpleado);
        EditText etCel = view.findViewById(R.id.etCelEmpleado);
        EditText etEm = view.findViewById(R.id.etEmEmpleado);
        EditText etDir = view.findViewById(R.id.etDirEmpleado);
        EditText etFn = view.findViewById(R.id.etFnEmpleado);

        // Cargar datos actuales desde SharedPreferences
        etNom.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("nom_empleado", ""));
        etApat.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("apat_empleado", ""));
        etAmat.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("amat_empleado", ""));
        etNdoc.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("ndoc_empleado", ""));
        etCel.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("cel_empleado", ""));
        etEm.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("em_empleado", ""));
        etDir.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("dir_empleado", ""));
        etFn.setText(getSharedPreferences("datos", MODE_PRIVATE).getString("fn_empleado", ""));

        builder.setPositiveButton("Guardar", (dialogInterface, i) -> {
            String nom = etNom.getText().toString().trim();
            String apat = etApat.getText().toString().trim();
            String amat = etAmat.getText().toString().trim();
            String ndoc = etNdoc.getText().toString().trim();
            String cel = etCel.getText().toString().trim();
            String em = etEm.getText().toString().trim();
            String dir = etDir.getText().toString().trim();
            String fn = etFn.getText().toString().trim();

            if (nom.isEmpty() || apat.isEmpty() || amat.isEmpty() || ndoc.isEmpty() || cel.isEmpty() || em.isEmpty() || dir.isEmpty() || fn.isEmpty()) {
                Toast.makeText(MainActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                String id_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("id_empleado", "");
                ActualizarDatosPersonales(id_empleado, nom, apat, amat, ndoc, cel, em, dir, fn);
            }
        });

        builder.setNegativeButton("Cancelar", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void modalCambiarContraseña() {
        //Crear un AlertDialog personalizado para cambiar contraseña
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(custom_cambiar_password, null);
        builder.setView(view);

        final EditText etPassActual = view.findViewById(R.id.etPassActual);
        final EditText etPassNueva = view.findViewById(R.id.etPassNueva);
        final EditText etPassConfirmar = view.findViewById(R.id.etPassConfirmar);

        //hacer referencia al titulo
        builder.setTitle("Cambiar Contraseña");

        builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String passActual = etPassActual.getText().toString();
                String passNueva = etPassNueva.getText().toString();
                String passConfirmar = etPassConfirmar.getText().toString();

                //obtener contraseña de SharedPreferences
                String password = getSharedPreferences("datos", MODE_PRIVATE).getString("password", "");
                String id_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("id_empleado", "");

                //validar que contraseña actual sea igual a la contraseña del SharedPrefenrece
                if(passActual.equals(password)){
                    //validar que contraseña nueva y confirmar contraseña sean iguales
                    if(passNueva.equals(passConfirmar)){
                        //validar que contraseña nueva no sea igual a la contraseña actual
                        if(!passNueva.equals(passActual)){
                            //validar que contraseña nueva no este vacia
                            if(!passNueva.isEmpty()){
                                ActualizarContraseña(passNueva,id_empleado);
                            }else{
                                Toast.makeText(MainActivity.this, "Contraseña nueva no puede estar vacia", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(MainActivity.this, "Contraseña nueva no puede ser igual a la actual", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(MainActivity.this, "Contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show();
                }


            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //cerrar el dialog
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void ActualizarContraseña(String passNueva, String id_empleado) {
        String url = Login.servidor + "usuario_actualizar_contra.php";

        RequestParams params = new RequestParams();
        params.put("id_empleado", id_empleado);
        params.put("password", passNueva);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(respuesta);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        // Actualizar SharedPreferences con la nueva contraseña
                        getSharedPreferences("datos", MODE_PRIVATE).edit()
                                .putString("password", passNueva)
                                .apply();

                        Toast.makeText(MainActivity.this, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show();
                        new android.os.Handler().postDelayed(() -> logout(), 1000);
                    } else {
                        Toast.makeText(MainActivity.this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar la respuesta", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ActualizarDatosPersonales(String id_empleado, String nom, String apat, String amat, String ndoc, String cel, String em, String dir, String fn) {
        String url = Login.servidor + "usuario_actualizar_datos.php";

        RequestParams params = new RequestParams();
        params.put("id_empleado", id_empleado);
        params.put("nom_empleado", nom);
        params.put("apat_empleado", apat);
        params.put("amat_empleado", amat);
        params.put("ndoc_empleado", ndoc);
        params.put("cel_empleado", cel);
        params.put("em_empleado", em);
        params.put("dir_empleado", dir);
        params.put("fn_empleado", fn);

        AsyncHttpClient client = new AsyncHttpClient();
        client.post(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                String respuesta = new String(responseBody);
                try {
                    JSONObject jsonObject = new JSONObject(respuesta);
                    boolean success = jsonObject.getBoolean("success");
                    if (success) {
                        Toast.makeText(MainActivity.this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();

                        // Actualizar SharedPreferences con los nuevos datos
                        getSharedPreferences("datos", MODE_PRIVATE).edit()
                                .putString("nom_empleado", nom)
                                .putString("apat_empleado", apat)
                                .putString("amat_empleado", amat)
                                .putString("ndoc_empleado", ndoc)
                                .putString("cel_empleado", cel)
                                .putString("em_empleado", em)
                                .putString("dir_empleado", dir)
                                .putString("fn_empleado", fn)
                                .apply();
                    } else {
                        Toast.makeText(MainActivity.this, "Error al actualizar datos", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error al procesar la respuesta del servidor", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + statusCode, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void logout() {
        //Borrar datos de SharedPreferences
        getSharedPreferences("datos", MODE_PRIVATE).edit()
                .remove("username")
                .remove("password")
                .remove("id_empleado")
                .remove("nom_empleado")
                .remove("em_empleado")
                .apply();


        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();

    }

    public void mostrarDialogoCargo(String tipoCargo, String nomCargo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_alert_empleado, null);

        TextView tvCargo = dialogView.findViewById(R.id.tvCargo);
        TextView tvOpciones = dialogView.findViewById(R.id.tvOpciones);

        tvCargo.setText("Has iniciado sesión como: " + nomCargo);

        String opciones;

        switch (nomCargo) {
            case "Administrador":
                opciones = "- Gestión de ventas\n- Gestión de compras\n- Productos\n- Clientes\n- Proveedores\n- Empleados\n- Reportes";
                break;
            case "Vendedor":
                opciones = "- Gestión de ventas\n- Productos\n- Clientes";
                break;
            case "Almacenero":
                opciones = "- Gestión de compras\n- Productos\n- Proveedores";
                break;
            default:
                opciones = "Sin acceso definido";
                break;
        }

        tvOpciones.setText("Opciones disponibles:\n" + opciones);

        builder.setView(dialogView);
        builder.setPositiveButton("Aceptar", null);
        builder.create().show();
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}