package com.codelabs.citaya;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_main);

        // Boton Navegacion
        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);

        BottomNavigationView nav = findViewById(R.id.bottomNav);

        BadgeDrawable badge = nav.getOrCreateBadge(R.id.nav_alertas);
        badge.setVisible(true);
        badge.setNumber(2);

        findViewById(R.id.cardAnalisis).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ConsultaActivity.class));
        });
        // Menu nav
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_citas) {
                startActivity(new Intent(MainActivity.this, CitasActivity.class));
                return true;
            } else if (id == R.id.nav_alertas) {
                    startActivity(new Intent(MainActivity.this, NotificacionesActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(MainActivity.this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }
}