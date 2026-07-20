package com.codelabs.citaya;

import androidx.activity.OnBackPressedCallback;
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

        findViewById(R.id.btnBack).setOnClickListener(v -> irAMain());

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                irAMain();
            }
        });

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

    private void irAMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void cargarCitas() {
        String correo = MainActivity.obtenerCorreoActual(this);
        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);
        if (usuario == null) return;

        int usuarioId = usuario.getId();
        android.database.Cursor cursor = reservaDAO.obtenerReservasPorUsuario(usuarioId);

        contenedorProximas.removeAllViews();
        contenedorAnteriores.removeAllViews();

        if (cursor == null) return;

        while (cursor.moveToNext()) {
            int reservaId = cursor.getInt(cursor.getColumnIndexOrThrow("reserva_id"));
            int horarioId = cursor.getInt(cursor.getColumnIndexOrThrow("horario_id"));
            String doctor = cursor.getString(cursor.getColumnIndexOrThrow("doctor"));
            String especialidad = cursor.getString(cursor.getColumnIndexOrThrow("especialidad"));
            String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
            String hora = cursor.getString(cursor.getColumnIndexOrThrow("hora"));
            String ubicacion = cursor.getString(cursor.getColumnIndexOrThrow("ubicacion"));
            String estado = cursor.getString(cursor.getColumnIndexOrThrow("estado"));

            agregarCard(reservaId, horarioId, doctor, especialidad, fecha, hora, ubicacion, estado);
        }
        cursor.close();
    }

    private void agregarCard(int reservaId, int horarioId, String doctor, String especialidad, String fecha, String hora, String ubicacion, String estado) {
        View card = getLayoutInflater().inflate(R.layout.activity_card_citas, contenedorProximas, false);

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
        LinearLayout btnVerQr = card.findViewById(R.id.btnVerQr);

        // Configurar clic para Ver QR
        btnVerQr.setOnClickListener(v -> {
            String correoActual = MainActivity.obtenerCorreoActual(this);
            // Uso del método verificado en UsuarioDAO
            String nombrePaciente = usuarioDAO.obtenerNombrePorCorreo(correoActual);
            
            Intent qrIntent = new Intent(this, QrActivity.class);
            qrIntent.putExtra("reservaId", reservaId);
            qrIntent.putExtra("paciente", nombrePaciente);
            qrIntent.putExtra("doctor", doctor);
            qrIntent.putExtra("especialidad", especialidad);
            qrIntent.putExtra("fecha", fecha);
            qrIntent.putExtra("hora", hora);
            qrIntent.putExtra("estado", estado);
            startActivity(qrIntent);
        });

        if ("PENDIENTE".equals(estado) || "CONFIRMADA".equals(estado)) {
            String color = "PENDIENTE".equals(estado) ? "#EF6C00" : "#2E7D32";
            int icon = "PENDIENTE".equals(estado) ? R.drawable.ic_help : R.drawable.ic_check;
            int bg = "PENDIENTE".equals(estado) ? R.drawable.bg_pendiente : R.drawable.bg_confirmada;
            String text = "PENDIENTE".equals(estado) ? "Pendiente" : "Confirmada";

            configurarBadge(badgeEstado, iconEstado, txtEstado, text, color, icon, bg);
            badgeEstado.setOnClickListener(v -> alternarOpciones(layoutOpciones));

            if ("CONFIRMADA".equals(estado)) btnConfirmarCita.setVisibility(View.GONE);

            btnConfirmarCita.setOnClickListener(v -> {
                reservaDAO.actualizarEstadoReserva(reservaId, "CONFIRMADA");
                cargarCitas();
            });

            btnReprogramarCita.setOnClickListener(v -> abrirReprogramacion(reservaId, doctor, especialidad, fecha, hora, ubicacion, estado));

            btnCancelarCita.setOnClickListener(v -> {
                reservaDAO.actualizarEstadoReserva(reservaId, "CANCELADA");
                reservaDAO.liberarHorario(horarioId);
                cargarCitas();
            });

            contenedorProximas.addView(card);
        } else {
            String color = "COMPLETADA".equals(estado) ? "#1976D2" : "#D32F2F";
            int icon = "COMPLETADA".equals(estado) ? R.drawable.ic_check : R.drawable.ic_cancel;
            int bg = "COMPLETADA".equals(estado) ? R.drawable.bg_completada : R.drawable.bg_cancelada;
            configurarBadge(badgeEstado, iconEstado, txtEstado, estado, color, icon, bg);
            layoutOpciones.setVisibility(View.GONE);
            contenedorAnteriores.addView(card);
            
            if ("CANCELADA".equals(estado)) btnVerQr.setVisibility(View.GONE);
        }
    }

    private void alternarOpciones(LinearLayout layoutOpciones) {
        layoutOpciones.setVisibility(layoutOpciones.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
    }

    private void abrirReprogramacion(int reservaId, String doctor, String especialidad, String fecha, String hora, String ubicacion, String estado) {
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

    private void configurarBadge(LinearLayout badge, ImageView icono, TextView texto, String estado, String color, int iconRes, int bgRes) {
        badge.setBackgroundResource(bgRes);
        icono.setImageResource(iconRes);
        icono.setColorFilter(Color.parseColor(color));
        texto.setText(estado);
        texto.setTextColor(Color.parseColor(color));
    }

    private void activarTab(TextView activo, TextView inactivo) {
        activo.setBackgroundResource(R.drawable.bg_tab_selected);
        activo.setTextColor(Color.WHITE);
        inactivo.setBackground(null);
        inactivo.setTextColor(Color.DKGRAY);
    }
}