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

import com.codelabs.citaya.database.ReservaDAO;
import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class CitasActivity extends AppCompatActivity {

    LinearLayout contenedorProximas, contenedorAnteriores;

    private UsuarioDAO usuarioDAO;
    private ReservaDAO reservaDAO;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas);

        usuarioDAO = new UsuarioDAO(this);
        reservaDAO = new ReservaDAO(this);

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

        String correo = MainActivity.obtenerCorreoActual(this);

        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);

        if (usuario == null) return;

        int usuarioId = usuario.getId();

        android.database.Cursor cursor =
                reservaDAO.obtenerReservasPorUsuario(usuarioId);

        contenedorProximas.removeAllViews();
        contenedorAnteriores.removeAllViews();

        if (cursor == null) return;

        while (cursor.moveToNext()) {

            int reservaId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("reserva_id")
            );

            int horarioId = cursor.getInt(
                    cursor.getColumnIndexOrThrow("horario_id")
            );

            String doctor = cursor.getString(
                    cursor.getColumnIndexOrThrow("doctor")
            );

            String especialidad = cursor.getString(
                    cursor.getColumnIndexOrThrow("especialidad")
            );

            String fecha = cursor.getString(
                    cursor.getColumnIndexOrThrow("fecha")
            );

            String hora = cursor.getString(
                    cursor.getColumnIndexOrThrow("hora")
            );

            String ubicacion = cursor.getString(
                    cursor.getColumnIndexOrThrow("ubicacion")
            );

            String estado = cursor.getString(
                    cursor.getColumnIndexOrThrow("estado")
            );

            agregarCard(
                    reservaId,
                    horarioId,
                    doctor,
                    especialidad,
                    fecha,
                    hora,
                    ubicacion,
                    estado
            );
        }

        cursor.close();
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

    private void agregarCard(
            int reservaId,
            int horarioId,
            String doctor,
            String especialidad,
            String fecha,
            String hora,
            String ubicacion,
            String estado
    ) {

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
                cambiarEstado(reservaId, "CONFIRMADA");
                cargarCitas();
            });

            btnReprogramarCita.setOnClickListener(v -> abrirReprogramacion(
                    reservaId, doctor, especialidad, fecha, hora, ubicacion, estado
            ));

            btnCancelarCita.setOnClickListener(v -> {
                cambiarEstado(reservaId, "CANCELADA");
                reservaDAO.liberarHorario(horarioId);

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
                    reservaId, doctor, especialidad, fecha, hora, ubicacion, estado
            ));

            btnCancelarCita.setOnClickListener(v -> {
                cambiarEstado(reservaId, "CANCELADA");
                reservaDAO.liberarHorario(horarioId);

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

    private void abrirReprogramacion(
            int reservaId,
            String doctor,
            String especialidad,
            String fecha,
            String hora,
            String ubicacion,
            String estado
    ) {

        Intent intent = new Intent(this, ReservaActivity.class);
        intent.putExtra("modo", "reprogramar");
        intent.putExtra("reservaId", reservaId);
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

    private void cambiarEstado(int reservaId, String nuevoEstado) {

        boolean actualizado =
                reservaDAO.actualizarEstadoReserva(reservaId, nuevoEstado);

        if (actualizado && "CONFIRMADA".equals(nuevoEstado)) {

            NotificacionesActivity.guardarNotificacion(
                    this,
                    "Cita confirmada",
                    "Tu cita fue confirmada correctamente.",
                    "cita"
            );
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