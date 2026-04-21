package com.codelabs.citaya;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BLANCA
        getWindow().setStatusBarColor(Color.WHITE);

        // 🔥 ICONOS NEGROS (hora, batería)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_main);

        // Inicializar el BottomNavigation
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        // Listener para la navegación
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(MainActivity.this, CitasActivity.class));
                return true;
            } else if (id == R.id.nav_alertas) {
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }
}