package com.codelabs.citaya;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    // 🔴 Estado global (mientras la app está abierta)
    public static Set<String> leidas = new HashSet<>();
    public static final int TOTAL_NOTIS = 5;

    private TextView txtBadge;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_main);

        txtBadge = findViewById(R.id.txtBadge);
        bottomNav = findViewById(R.id.bottomNav);

        actualizarBadges();

        //
        findViewById(R.id.btnNotificaciones).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NotificacionesActivity.class));
        });

        findViewById(R.id.cardHistorial).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, CitasActivity.class));
        });

        findViewById(R.id.cardReservar).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ReservaActivity.class));
        });

        findViewById(R.id.cardConsulta).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ConsultaActivity.class));
        });

        findViewById(R.id.cardEspecialidad).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, EspecialidadesActivity.class));
        });

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

    @Override
    protected void onResume() {
        super.onResume();
        actualizarBadges();
    }

    // 🔥 Cálculo de no leídas
    public static int getNoLeidas() {
        return TOTAL_NOTIS - leidas.size();
    }

    // 🔥 SINCRONIZA AMBOS BADGES
    private void actualizarBadges() {

        int cantidad = getNoLeidas();

        // 🔔 BADGE SUPERIOR (campana)
        if (txtBadge != null) {
            if (cantidad > 0) {
                txtBadge.setVisibility(View.VISIBLE);
                txtBadge.setText(String.valueOf(cantidad));
            } else {
                txtBadge.setVisibility(View.GONE);
            }
        }

        // 🔻 BADGE INFERIOR (BottomNavigation)
        if (bottomNav != null) {
            BadgeDrawable badge = bottomNav.getOrCreateBadge(R.id.nav_alertas);

            if (cantidad > 0) {
                badge.setVisible(true);
                badge.setNumber(cantidad);
            } else {
                badge.setVisible(false);
            }
        }
    }
}