package com.codelabs.citaya; // Cambia esto por tu package real

import android.os.Bundle;
import android.content.Intent;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar el BottomNavigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Listener para la navegación
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                // Lógica para Inicio
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(MainActivity.this, CitasActivity.class));
                return true;
                // Lógica para Citas
            } else if (id == R.id.nav_alertas) {
                // Lógica para Alertas
                return true;
            } else if (id == R.id.nav_perfil) {
                // Lógica para Perfil
                return true;
            }
            return false;
        });
    }
}