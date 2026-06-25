package com.codelabs.citaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CitasActivity extends AppCompatActivity {

    LinearLayout contenedorProximas, contenedorAnteriores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        TextView tabProximas = findViewById(R.id.tabProximas);
        TextView tabAnteriores = findViewById(R.id.tabAnteriores);

        LinearLayout layoutProximas = findViewById(R.id.layoutProximas);
        LinearLayout layoutAnteriores = findViewById(R.id.layoutAnteriores);

        contenedorProximas = findViewById(R.id.contenedorProximas);
        contenedorAnteriores = findViewById(R.id.contenedorAnteriores);

        layoutProximas.setVisibility(View.VISIBLE);
        layoutAnteriores.setVisibility(View.GONE);

        activarTab(tabProximas, tabAnteriores);

        tabProximas.setOnClickListener(v -> {
            layoutProximas.setVisibility(View.VISIBLE);
            layoutAnteriores.setVisibility(View.GONE);
            activarTab(tabProximas, tabAnteriores);
        });

        tabAnteriores.setOnClickListener(v -> {
            layoutProximas.setVisibility(View.GONE);
            layoutAnteriores.setVisibility(View.VISIBLE);
            activarTab(tabAnteriores, tabProximas);
        });

        cargarCitas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCitas();
    }

    private SharedPreferences prefsCitasUsuario() {
        String correo = MainActivity.obtenerCorreoActual(this);
        return getSharedPreferences(LoginActivity.prefsCitas(correo), MODE_PRIVATE);
    }
    private void cargarCitas() {
        SharedPreferences prefs = prefsCitasUsuario();
        Set<String> citas = prefs.getStringSet("lista", new HashSet<>());

        contenedorProximas.removeAllViews();
        contenedorAnteriores.removeAllViews();

        if (citas == null || citas.isEmpty()) return;

        Set<String> citasActualizadas = new HashSet<>();
        java.util.ArrayList<String> proximas = new java.util.ArrayList<>();
        java.util.ArrayList<String> anteriores = new java.util.ArrayList<>();

        for (String c : citas) {
            String[] datos = c.split("\\|");
            if (datos.length < 6) continue;

            String estado = datos[5];

            if (("PENDIENTE".equals(estado) || "CONFIRMADA".equals(estado))
                    && citaYaPaso(datos[2], datos[3])) {
                estado = "COMPLETADA";
                c = construirCita(datos[0], datos[1], datos[2], datos[3], datos[4], estado);
            }

            citasActualizadas.add(c);

            if ("PENDIENTE".equals(estado) || "CONFIRMADA".equals(estado)) {
                proximas.add(c);
            } else {
                anteriores.add(c);
            }
        }

        proximas.sort((a, b) -> {
            String[] da = a.split("\\|");
            String[] db = b.split("\\|");
            return Long.compare(obtenerTiempoCita(da[2], da[3]), obtenerTiempoCita(db[2], db[3]));
        });

        anteriores.sort((a, b) -> {
            String[] da = a.split("\\|");
            String[] db = b.split("\\|");
            return Long.compare(obtenerTiempoCita(db[2], db[3]), obtenerTiempoCita(da[2], da[3]));
        });

        for (String c : proximas) {
            String[] datos = c.split("\\|");
            if (datos.length < 6) continue;
            agregarCard(c, datos[0], datos[1], datos[2], datos[3], datos[4], datos[5]);
        }

        for (String c : anteriores) {
            String[] datos = c.split("\\|");
            if (datos.length < 6) continue;
            agregarCard(c, datos[0], datos[1], datos[2], datos[3], datos[4], datos[5]);
        }

        prefs.edit().putStringSet("lista", citasActualizadas).apply();
    }
    private long obtenerTiempoCita(String fecha, String hora) {
        try {
            java.text.SimpleDateFormat formato =
                    new java.text.SimpleDateFormat("d/M/yyyy hh:mm a", java.util.Locale.US);

            java.util.Date fechaCita = formato.parse(fecha + " " + hora);

            return fechaCita != null ? fechaCita.getTime() : 0;

        } catch (Exception e) {
            return 0;
        }
    }

    private void agregarCard(String citaOriginal, String doctor, String especialidad,
                             String fecha, String hora, String ubicacion, String estado) {

        View card = getLayoutInflater().inflate(
                R.layout.activity_card_citas,
                contenedorProximas,
                false
        );

        ((TextView) card.findViewById(R.id.txtNombre)).setText(doctor);
        ((TextView) card.findViewById(R.id.txtEspecialidad)).setText(especialidad);
        ((TextView) card.findViewById(R.id.txtFecha)).setText(fecha);
        ((TextView) card.findViewById(R.id.txtHora)).setText(hora);
        ((TextView) card.findViewById(R.id.txtUbicacion)).setText(ubicacion);

        LinearLayout badgeEstado = card.findViewById(R.id.badgeEstado);
        ImageView iconEstado = card.findViewById(R.id.iconEstado);
        TextView txtEstado = card.findViewById(R.id.txtEstado);
        LinearLayout layoutOpciones = card.findViewById(R.id.layoutOpciones);
        LinearLayout btnConfirmarCita = card.findViewById(R.id.btnConfirmarCita);
        LinearLayout btnReprogramarCita = card.findViewById(R.id.btnReprogramarCita);
        LinearLayout btnCancelarCita = card.findViewById(R.id.btnCancelarCita);

        if ("PENDIENTE".equals(estado)) {

            configurarBadge(badgeEstado, iconEstado, txtEstado,
                    "Pendiente", "#EF6C00", R.drawable.ic_help, R.drawable.bg_pendiente);

            badgeEstado.setOnClickListener(v -> alternarOpciones(layoutOpciones));

            btnConfirmarCita.setVisibility(View.VISIBLE);

            btnConfirmarCita.setOnClickListener(v -> {
                cambiarEstado(citaOriginal, doctor, especialidad, fecha, hora, ubicacion, "CONFIRMADA");
                cargarCitas();
            });

            btnReprogramarCita.setOnClickListener(v -> abrirReprogramacion(
                    citaOriginal, doctor, especialidad, fecha, hora, ubicacion, estado
            ));

            btnCancelarCita.setOnClickListener(v -> {
                cambiarEstado(citaOriginal, doctor, especialidad, fecha, hora, ubicacion, "CANCELADA");
                liberarHorario(fecha, hora);

                NotificacionesActivity.guardarNotificacion(
                        this,
                        "Cita cancelada",
                        "Tu cita con " + doctor + " del " + fecha + " a las " + hora + " fue cancelada correctamente.",
                        "cancelada"
                );

                cargarCitas();
            });

            contenedorProximas.addView(card);

        } else if ("CONFIRMADA".equals(estado)) {

            configurarBadge(badgeEstado, iconEstado, txtEstado,
                    "Confirmada", "#2E7D32", R.drawable.ic_check, R.drawable.bg_confirmada);

            badgeEstado.setOnClickListener(v -> alternarOpciones(layoutOpciones));

            btnConfirmarCita.setVisibility(View.GONE);

            btnReprogramarCita.setOnClickListener(v -> abrirReprogramacion(
                    citaOriginal, doctor, especialidad, fecha, hora, ubicacion, estado
            ));

            btnCancelarCita.setOnClickListener(v -> {
                cambiarEstado(citaOriginal, doctor, especialidad, fecha, hora, ubicacion, "CANCELADA");
                liberarHorario(fecha, hora);

                NotificacionesActivity.guardarNotificacion(
                        this,
                        "Cita cancelada",
                        "Tu cita con " + doctor + " del " + fecha + " a las " + hora + " fue cancelada correctamente.",
                        "cancelada"
                );

                cargarCitas();
            });

            contenedorProximas.addView(card);

        } else if ("COMPLETADA".equals(estado)) {

            configurarBadge(badgeEstado, iconEstado, txtEstado,
                    "Completada", "#1976D2", R.drawable.ic_check, R.drawable.bg_completada);

            layoutOpciones.setVisibility(View.GONE);
            contenedorAnteriores.addView(card);

        } else {

            configurarBadge(badgeEstado, iconEstado, txtEstado,
                    "Cancelada", "#D32F2F", R.drawable.ic_cancel, R.drawable.bg_cancelada);

            layoutOpciones.setVisibility(View.GONE);
            contenedorAnteriores.addView(card);
        }
    }

    private void alternarOpciones(LinearLayout layoutOpciones) {
        if (layoutOpciones.getVisibility() == View.VISIBLE) {
            layoutOpciones.setVisibility(View.GONE);
        } else {
            layoutOpciones.setVisibility(View.VISIBLE);
        }
    }

    private void abrirReprogramacion(String citaOriginal, String doctor, String especialidad,
                                     String fecha, String hora, String ubicacion, String estado) {

        Intent intent = new Intent(this, ReservaActivity.class);
        intent.putExtra("modo", "reprogramar");
        intent.putExtra("citaOriginal", citaOriginal);
        intent.putExtra("doctor", doctor);
        intent.putExtra("especialidad", especialidad);
        intent.putExtra("fecha", fecha);
        intent.putExtra("hora", hora);
        intent.putExtra("ubicacion", ubicacion);
        intent.putExtra("estado", estado);
        startActivity(intent);
    }

    private void configurarBadge(LinearLayout badge, ImageView icono, TextView texto,
                                 String estado, String color, int iconRes, int bgRes) {
        badge.setBackgroundResource(bgRes);
        icono.setImageResource(iconRes);
        icono.setColorFilter(Color.parseColor(color));
        texto.setText(estado);
        texto.setTextColor(Color.parseColor(color));
    }

    private void cambiarEstado(String citaOriginal, String doctor, String especialidad,
                               String fecha, String hora, String ubicacion, String nuevoEstado) {

        SharedPreferences prefs = prefsCitasUsuario();
        Set<String> citas = prefs.getStringSet("lista", new HashSet<>());
        Set<String> nuevas = new HashSet<>(citas);

        nuevas.remove(citaOriginal);
        nuevas.add(construirCita(doctor, especialidad, fecha, hora, ubicacion, nuevoEstado));

        prefs.edit().putStringSet("lista", nuevas).apply();

        if ("CONFIRMADA".equals(nuevoEstado)) {
            NotificacionesActivity.guardarNotificacion(
                    this,
                    "Cita confirmada",
                    "Tu cita con " + doctor + " del " + fecha + " a las " + hora + " fue confirmada correctamente.",
                    "cita"
            );
        }
    }

    private void liberarHorario(String fecha, String horaAMPM) {
        SharedPreferences prefs = prefsCitasUsuario();

        Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());
        Set<String> copia = new HashSet<>(ocupados);

        String hora24 = convertirHora24(horaAMPM);
        String clave = fecha + "_" + hora24;

        copia.remove(clave);

        prefs.edit().putStringSet("ocupados", copia).apply();
    }

    private String convertirHora24(String horaAMPM) {
        try {
            SimpleDateFormat formato12 = new SimpleDateFormat("hh:mm a", Locale.US);
            SimpleDateFormat formato24 = new SimpleDateFormat("HH:mm", Locale.US);
            Date fecha = formato12.parse(horaAMPM);
            return formato24.format(fecha);
        } catch (Exception e) {
            return horaAMPM;
        }
    }

    private String construirCita(String doctor, String especialidad, String fecha,
                                 String hora, String ubicacion, String estado) {
        return doctor + "|" + especialidad + "|" + fecha + "|" + hora + "|" + ubicacion + "|" + estado;
    }

    private boolean citaYaPaso(String fecha, String hora) {
        try {
            String fechaHora = fecha + " " + hora;

            SimpleDateFormat formato = new SimpleDateFormat("d/M/yyyy hh:mm a", Locale.US);
            Date fechaCita = formato.parse(fechaHora);
            Date ahora = new Date();

            return fechaCita != null && fechaCita.before(ahora);

        } catch (Exception e) {
            return false;
        }
    }

    private void activarTab(TextView activo, TextView inactivo) {
        activo.setBackgroundResource(R.drawable.bg_tab_selected);
        activo.setTextColor(getResources().getColor(android.R.color.white));

        inactivo.setBackground(null);
        inactivo.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }
}