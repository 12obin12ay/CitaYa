package com.codelabs.citaya;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TextView txtBadge;
    private BottomNavigationView bottomNav;

    private CardView cardProximaCita;
    private TextView txtDoctor, txtEspecialidad, txtHora, txtDia, txtMes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        setContentView(R.layout.activity_main);

        if (!haySesionActiva()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        TextView txtName = findViewById(R.id.txtName);

        String correoActual = obtenerCorreoActual(this);
        String key = LoginActivity.claveUsuario(correoActual);

        SharedPreferences usuariosPrefs = getSharedPreferences(LoginActivity.PREF_USUARIOS, MODE_PRIVATE);
        String nombre = usuariosPrefs.getString(key + "_nombre", "Nombre del Paciente");

        txtName.setText(nombre);

        txtBadge = findViewById(R.id.txtBadge);
        bottomNav = findViewById(R.id.bottomNav);

        cardProximaCita = findViewById(R.id.cardProximaCita);
        txtDoctor = findViewById(R.id.txtDoctor);
        txtEspecialidad = findViewById(R.id.txtEspecialidad);
        txtHora = findViewById(R.id.txtHora);
        txtDia = findViewById(R.id.txtDia);
        txtMes = findViewById(R.id.txtMes);

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

        cardProximaCita.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, CitasActivity.class)));

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_inicio) {
                return true;
            } else if (id == R.id.nav_citas) {
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

        if (bottomNav != null) {
            bottomNav.setSelectedItemId(R.id.nav_inicio);
        }
    }

    private boolean haySesionActiva() {
        return getSharedPreferences(LoginActivity.PREF_SESION, MODE_PRIVATE)
                .getBoolean("sesion_activa", false);
    }

    public static String obtenerCorreoActual(Context context) {
        return context.getSharedPreferences(LoginActivity.PREF_SESION, Context.MODE_PRIVATE)
                .getString("correo_actual", "");
    }

    public static String prefsSalud(Context context) {
        String correo = obtenerCorreoActual(context);
        return "salud_" + LoginActivity.claveUsuario(correo);
    }

    public static int contarNoLeidas(Context context) {
        String correo = obtenerCorreoActual(context);

        SharedPreferences prefs = context.getSharedPreferences(
                LoginActivity.prefsNotificaciones(correo),
                Context.MODE_PRIVATE
        );

        Set<String> lista = prefs.getStringSet("lista", new HashSet<>());

        SharedPreferences leidas = context.getSharedPreferences(
                LoginActivity.prefsNotificacionesLeidas(correo),
                Context.MODE_PRIVATE
        );

        int total = 0;

        for (String item : lista) {
            String[] partes = item.split("\\|");
            if (partes.length < 5) continue;

            String id = partes[0];

            if (!leidas.getBoolean(id, false)) {
                total++;
            }
        }

        return total;
    }

    private void actualizarBadges() {
        int cantidad = contarNoLeidas(this);

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
                badge.setBackgroundColor(Color.RED);
                badge.setBadgeTextColor(Color.WHITE);
            } else {
                badge.setVisible(false);
                badge.clearNumber();
            }
        }
    }

    private void actualizarEstadoSalud() {
        String nivel = getSharedPreferences(prefsSalud(this), MODE_PRIVATE)
                .getString("nivel", "LEVE");

        TextView txtEstado = findViewById(R.id.txtEstadoSalud);
        ImageView iconCorazon = findViewById(R.id.iconCorazon);
        ImageView iconVital = findViewById(R.id.iconVital);
        CardView cardEstado = findViewById(R.id.cardEstado);

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
            String[] partes = hora.trim().split(":");
            int horas = Integer.parseInt(partes[0].trim());
            String minutos = partes[1].trim().substring(0, 2);

            String sufijo = horas < 12 ? "AM" : "PM";
            int hora12 = horas % 12;
            if (hora12 == 0) hora12 = 12;

            return String.format("%02d:%s %s", hora12, minutos, sufijo);
        } catch (Exception e) {
            return hora;
        }
    }

    private void cargarProximaCita() {
        if (cardProximaCita == null || txtDoctor == null ||
                txtEspecialidad == null || txtHora == null ||
                txtDia == null || txtMes == null) return;

        String correo = obtenerCorreoActual(this);

        Set<String> citas = getSharedPreferences(LoginActivity.prefsCitas(correo), MODE_PRIVATE)
                .getStringSet("lista", new HashSet<>());

        String citaMasProxima = null;
        long tiempoMasProximo = Long.MAX_VALUE;
        long ahora = System.currentTimeMillis();

        for (String cita : citas) {
            String[] datos = cita.split("\\|");
            if (datos.length < 6) continue;

            String estado = datos[5];

            if (!estado.equals("PENDIENTE") && !estado.equals("CONFIRMADA")) {
                continue;
            }

            long tiempoCita = obtenerTiempoCita(datos[2], datos[3]);

            if (tiempoCita >= ahora && tiempoCita < tiempoMasProximo) {
                tiempoMasProximo = tiempoCita;
                citaMasProxima = cita;
            }
        }

        if (citaMasProxima != null) {
            String[] datos = citaMasProxima.split("\\|");

            txtDoctor.setText(datos[0]);
            txtEspecialidad.setText(datos[1]);
            txtHora.setText(datos[3]);

            String[] fechaParts = datos[2].split("/");

            if (fechaParts.length >= 2) {
                txtDia.setText(fechaParts[0]);

                String[] meses = {
                        "ENE", "FEB", "MAR", "ABR", "MAY", "JUN",
                        "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"
                };

                try {
                    int mesIndex = Integer.parseInt(fechaParts[1]) - 1;
                    txtMes.setText(meses[mesIndex]);
                } catch (Exception e) {
                    txtMes.setText(fechaParts[1]);
                }
            }

            cardProximaCita.setVisibility(View.VISIBLE);
        } else {
            cardProximaCita.setVisibility(View.GONE);
        }
    }

    private long obtenerTiempoCita(String fecha, String hora) {
        try {
            java.text.SimpleDateFormat formato =
                    new java.text.SimpleDateFormat("d/M/yyyy hh:mm a", java.util.Locale.US);

            java.util.Date date = formato.parse(fecha + " " + hora);

            return date != null ? date.getTime() : Long.MAX_VALUE;

        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    private void aplicarEfectoClick(View view) {
        if (view == null) return;

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