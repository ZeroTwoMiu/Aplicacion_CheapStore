package com.example.tiendaapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tiendaapp2.databinding.ActivityMainBinding;

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

        //obtener datos de SharedPreferences
        String username = getSharedPreferences("datos", MODE_PRIVATE).getString("username", "");
        String password = getSharedPreferences("datos", MODE_PRIVATE).getString("password", "");
        String id_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("id_empleado", "");
        String nom_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("nom_empleado", "");
        String em_empleado = getSharedPreferences("datos", MODE_PRIVATE).getString("em_empleado", "");


        //mostrar datos en los textview
        tvMenuUsuario.setText(nom_empleado);
        tvMenuEmail.setText(em_empleado);

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .setAnchorView(R.id.fab).show();
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_producto, R.id.nav_cliente, R.id.nav_proveedor)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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

        return true;
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

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}