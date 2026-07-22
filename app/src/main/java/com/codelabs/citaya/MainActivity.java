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
import android.database.Cursor;
import com.codelabs.citaya.database.ReservaDAO;
import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;

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
        UsuarioDAO usuarioDAO = new UsuarioDAO(this);
        Usuario usuario = usuarioDAO.buscarPorCorreo(correoActual);

        if (usuario != null) {
            txtName.setText(usuario.getNombre());
        } else {
            txtName.setText("Nombre del Paciente");
        }

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

    private void cargarProximaCita() {
        if (cardProximaCita == null || txtDoctor == null ||
                txtEspecialidad == null || txtHora == null ||
                txtDia == null || txtMes == null) return;

        String correo = obtenerCorreoActual(this);
        UsuarioDAO usuarioDAO = new UsuarioDAO(this);
        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

        if (usuario == null) {
            cardProximaCita.setVisibility(View.GONE);
            return;
        }

        ReservaDAO reservaDAO = new ReservaDAO(this);
        Cursor cursor = reservaDAO.obtenerReservasPorUsuario(usuario.getId());

        String doctorProximo = null;
        String especialidadProxima = null;
        String fechaProxima = null;
        String horaProxima = null;

        long tiempoMasProximo = Long.MAX_VALUE;
        long ahora = System.currentTimeMillis();

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));
                if (!estado.equals("PENDIENTE") && !estado.equals("CONFIRMADA")) continue;

                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));

                // 🔥 CORREGIDO: Usamos obtenerTiempoCita (que maneja AM/PM) 
                // en lugar de obtenerTiempoCita24 para que reconozca los datos de la v7.
                long tiempoCita = obtenerTiempoCita(fecha, hora);

                if (tiempoCita >= ahora && tiempoCita < tiempoMasProximo) {
                    tiempoMasProximo = tiempoCita;
                    doctorProximo = cursor.getString(cursor.getColumnIndexOrThrow("doctor"));
                    especialidadProxima = cursor.getString(cursor.getColumnIndexOrThrow("especialidad"));
                    fechaProxima = fecha;
                    horaProxima = hora;
                }
            }
            cursor.close();
        }

        if (doctorProximo != null) {
            txtDoctor.setText(doctorProximo);
            txtEspecialidad.setText(especialidadProxima);
            txtHora.setText(horaProxima);

            String[] fechaParts = fechaProxima.split("/");
            if (fechaParts.length >= 2) {
                txtDia.setText(fechaParts[0]);
                String[] meses = {"ENE", "FEB", "MAR", "ABR", "MAY", "JUN", "JUL", "AGO", "SEP", "OCT", "NOV", "DIC"};
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
            // Formato d/M/yyyy hh:mm a (Soporta AM/PM)
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
