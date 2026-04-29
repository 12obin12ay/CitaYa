package com.codelabs.citaya;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    public static Set<String> leidas = new HashSet<>();
    public static final int TOTAL_NOTIS = 5;
    private static boolean yaInicializado = false;
    private TextView txtBadge;
    private BottomNavigationView bottomNav;

    // PRÓXIMA CITA
    private CardView cardProximaCita;
    private TextView txtDoctor, txtEspecialidad, txtHora, txtDia, txtMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );

        setContentView(R.layout.activity_main);

        // INICIALIZAR VISTAS
        txtBadge        = findViewById(R.id.txtBadge);
        bottomNav       = findViewById(R.id.bottomNav);

        // PRÓXIMA CITA — inicializar antes de onResume
        cardProximaCita  = findViewById(R.id.cardProximaCita);
        txtDoctor        = findViewById(R.id.txtDoctor);
        txtEspecialidad  = findViewById(R.id.txtEspecialidad);
        txtHora          = findViewById(R.id.txtHora);
        txtDia           = findViewById(R.id.txtDia);
        txtMes           = findViewById(R.id.txtMes);


        if (!yaInicializado) {
            getSharedPreferences("citas", MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();
            yaInicializado = true;
        }

        // SIEMPRE INICIA EN ESTABLE
        getSharedPreferences("salud", MODE_PRIVATE)
                .edit()
                .putString("nivel", "LEVE")
                .apply();

        actualizarBadges();

        aplicarEfectoClick(findViewById(R.id.cardConsulta));
        aplicarEfectoClick(findViewById(R.id.cardReservar));
        aplicarEfectoClick(findViewById(R.id.cardEspecialidad));
        aplicarEfectoClick(findViewById(R.id.cardHistorial));

        findViewById(R.id.btnNotificaciones).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, NotificacionesActivity.class)));

        findViewById(R.id.cardHistorial).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CitasActivity.class)));

        findViewById(R.id.cardReservar).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ReservaActivity.class)));

        findViewById(R.id.cardConsulta).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, ConsultaActivity.class)));

        findViewById(R.id.cardEspecialidad).setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, EspecialidadesActivity.class)));

        // CARD PRÓXIMA CITA → lleva a CitasActivity
        cardProximaCita.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CitasActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_inicio) return true;
            else if (id == R.id.nav_citas) {
                startActivity(new Intent(this, CitasActivity.class));
                return true;
            } else if (id == R.id.nav_alertas) {
                startActivity(new Intent(this, NotificacionesActivity.class));
                return true;
            } else if (id == R.id.nav_perfil) {
                startActivity(new Intent(this, PerfilActivity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarBadges();
        actualizarEstadoSalud();
        cargarProximaCita();
    }

    public static int getNoLeidas() {
        return TOTAL_NOTIS - leidas.size();
    }

    private void actualizarBadges() {
        int cantidad = getNoLeidas();

        if (txtBadge != null) {
            if (cantidad > 0) {
                txtBadge.setVisibility(View.VISIBLE);
                txtBadge.setText(String.valueOf(cantidad));
            } else {
                txtBadge.setVisibility(View.GONE);
            }
        }

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

    private void actualizarEstadoSalud() {
        String nivel = getSharedPreferences("salud", MODE_PRIVATE)
                .getString("nivel", "LEVE");

        TextView txtEstado  = findViewById(R.id.txtEstadoSalud);
        ImageView iconCorazon = findViewById(R.id.iconCorazon);
        ImageView iconVital   = findViewById(R.id.iconVital);
        CardView cardEstado   = findViewById(R.id.cardEstado);

        if (txtEstado == null || iconCorazon == null || iconVital == null || cardEstado == null) return;

        if ("GRAVE".equals(nivel)) {
            txtEstado.setText("Requiere atención");
            txtEstado.setTextColor(Color.RED);
            iconCorazon.setBackgroundResource(R.drawable.bg_icon_red);
            iconVital.setColorFilter(Color.RED);
            cardEstado.setCardBackgroundColor(Color.parseColor("#FFEBEE"));

        } else if ("MODERADO".equals(nivel)) {
            txtEstado.setText("Precaución");
            txtEstado.setTextColor(Color.parseColor("#FFA000"));
            iconCorazon.setBackgroundResource(R.drawable.bg_icon_orange);
            iconVital.setColorFilter(Color.parseColor("#FFA000"));
            cardEstado.setCardBackgroundColor(Color.parseColor("#FFF8E1"));

        } else {
            txtEstado.setText("Estable");
            txtEstado.setTextColor(Color.parseColor("#4CAF50"));
            iconCorazon.setBackgroundResource(R.drawable.bg_icon_green);
            iconVital.setColorFilter(Color.parseColor("#4CAF50"));
            cardEstado.setCardBackgroundColor(Color.parseColor("#E8F5E9"));
        }
    }
    private String formatearHoraConAMPM(String hora) {
        try {
            // Soporta formato "09:00" o "09:00 AM/PM"
            String[] partes = hora.trim().split(":");
            int horas = Integer.parseInt(partes[0].trim());
            String minutos = partes[1].trim().length() >= 2
                    ? partes[1].trim().substring(0, 2)
                    : partes[1].trim();

            String sufijo = horas < 12 ? "AM" : "PM";
            int hora12 = horas % 12;
            if (hora12 == 0) hora12 = 12;

            return String.format("%02d:%s %s", hora12, minutos, sufijo);
        } catch (Exception e) {
            return hora; // Si falla, devuelve la hora original
        }
    }
    private void cargarProximaCita() {
        if (cardProximaCita == null || txtDoctor == null ||
                txtEspecialidad == null || txtHora == null ||
                txtDia == null || txtMes == null) return;

        Set<String> citas = getSharedPreferences("citas", MODE_PRIVATE)
                .getStringSet("lista", new HashSet<>());

        if (citas != null && !citas.isEmpty()) {

            String citaEncontrada = null;
            for (String cita : citas) {
                if (cita.endsWith("PENDIENTE")) {
                    citaEncontrada = cita;
                    break;
                }
            }

            if (citaEncontrada != null) {
                // Formato: doctor|especialidad|fecha|hora|ubicacion|PENDIENTE
                String[] datos = citaEncontrada.split("\\|");

                if (datos.length >= 5) {
                    txtDoctor.setText(datos[0]);        // "Dr. Carlos Rodríguez"
                    txtEspecialidad.setText(datos[1]);  // "Cardiología"
                    String horaFormateada = formatearHoraConAMPM(datos[3]);
                    txtHora.setText(horaFormateada);

                    // Extraer día y mes de la fecha (formato dd/MM/yyyy)
                    String[] fechaParts = datos[2].split("/");
                    if (fechaParts.length >= 2) {
                        txtDia.setText(fechaParts[0]);  // "25"

                        // Convertir número de mes a abreviatura
                        String[] meses = {"ENE","FEB","MAR","ABR","MAY","JUN",
                                "JUL","AGO","SEP","OCT","NOV","DIC"};
                        try {
                            int mesIndex = Integer.parseInt(fechaParts[1]) - 1;
                            txtMes.setText(meses[mesIndex]);  // "ABR"
                        } catch (NumberFormatException e) {
                            txtMes.setText(fechaParts[1]);
                        }
                    }

                    cardProximaCita.setVisibility(View.VISIBLE);
                    return;
                }
            }
        }

        // Sin citas pendientes → ocultar la card
        cardProximaCita.setVisibility(View.GONE);
    }

    private void aplicarEfectoClick(View view) {
        view.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(100).start();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(100).start();
                    break;
            }
            return false;
        });
    }
}