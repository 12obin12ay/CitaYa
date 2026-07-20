package com.codelabs.citaya;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class NotificacionesActivity extends AppCompatActivity {

    private TextView txtResumen, btnMarcarTodas;
    private LinearLayout contenedorNotificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        limpiarDatosDePruebaUnaVez();

        txtResumen = findViewById(R.id.txtResumen);
        btnMarcarTodas = findViewById(R.id.btnMarcarTodas);
        contenedorNotificaciones = findViewById(R.id.contenedorNotificaciones);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        btnMarcarTodas.setOnClickListener(v -> marcarTodasComoLeidas());

        cargarNotificaciones();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarNotificaciones();
    }

    private int dp(int valor) {
        return (int) (valor * getResources().getDisplayMetrics().density);
    }

    private String correoActual() {
        return MainActivity.obtenerCorreoActual(this);
    }

    private SharedPreferences prefsNotificacionesUsuario() {
        return getSharedPreferences(
                LoginActivity.prefsNotificaciones(correoActual()),
                MODE_PRIVATE
        );
    }

    private SharedPreferences prefsLeidasUsuario() {
        return getSharedPreferences(
                LoginActivity.prefsNotificacionesLeidas(correoActual()),
                MODE_PRIVATE
        );
    }

    private void marcarTodasComoLeidas() {
        Set<String> lista = prefsNotificacionesUsuario()
                .getStringSet("lista", new HashSet<>());

        SharedPreferences.Editor editor = prefsLeidasUsuario().edit();

        for (String item : lista) {
            String[] partes = item.split("\\|");
            if (partes.length < 5) continue;

            String id = partes[0];
            editor.putBoolean(id, true);
        }

        editor.apply();
        cargarNotificaciones();
    }

    private long obtenerTiempo(String item) {
        try {
            String id = item.split("\\|")[0];

            if (id.contains("_")) {
                id = id.split("_")[0];
            }

            return Long.parseLong(id);
        } catch (Exception e) {
            return 0;
        }
    }

    private void cargarNotificaciones() {
        contenedorNotificaciones.removeAllViews();

        SharedPreferences prefs = prefsNotificacionesUsuario();
        Set<String> lista = prefs.getStringSet("lista", new HashSet<>());

        ArrayList<String> ordenadas = new ArrayList<>(lista);

        ordenadas.sort((a, b) -> {
            long tiempoA = obtenerTiempo(a);
            long tiempoB = obtenerTiempo(b);
            return Long.compare(tiempoB, tiempoA);
        });

        int nuevas = 0;

        for (String item : ordenadas) {
            String[] partes = item.split("\\|");

            if (partes.length < 5) continue;

            String id = partes[0];
            String titulo = partes[1];
            String mensaje = partes[2];
            String tipo = partes[3];
            String tiempo = partes[4];

            boolean leida = estaLeida(id);

            if (!leida) nuevas++;

            View card = crearCard(id, titulo, mensaje, tipo, tiempo, leida);
            contenedorNotificaciones.addView(card);
        }

        if (nuevas > 1) {
            txtResumen.setText(nuevas + " notificaciones nuevas");
        } else if (nuevas == 1) {
            txtResumen.setText("1 notificación nueva");
        } else {
            txtResumen.setText("No tienes notificaciones nuevas");
        }

        btnMarcarTodas.setVisibility(nuevas > 0 ? View.VISIBLE : View.GONE);
    }

    private View crearCard(String id, String titulo, String mensaje, String tipo, String tiempo, boolean leida) {
        CardView card = new CardView(this);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        cardParams.setMargins(0, 0, 0, dp(12));

        card.setLayoutParams(cardParams);
        card.setRadius(dp(8));
        card.setCardElevation(dp(3));
        card.setUseCompatPadding(true);
        card.setCardBackgroundColor(0xFFFFFFFF);

        FrameLayout frame = new FrameLayout(this);

        LinearLayout horizontal = new LinearLayout(this);
        horizontal.setOrientation(LinearLayout.HORIZONTAL);
        horizontal.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
        ));

        View barra = new View(this);
        barra.setLayoutParams(new LinearLayout.LayoutParams(
                dp(5),
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        barra.setBackgroundColor(0xFF2D7DD2);
        horizontal.addView(barra);

        LinearLayout contenido = new LinearLayout(this);
        contenido.setOrientation(LinearLayout.HORIZONTAL);
        contenido.setGravity(android.view.Gravity.CENTER_VERTICAL);
        contenido.setPadding(dp(10), dp(12), dp(12), dp(12));
        contenido.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        ImageView icono = new ImageView(this);
        LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(dp(38), dp(38));
        icono.setLayoutParams(iconParams);
        icono.setPadding(dp(7), dp(7), dp(7), dp(7));

        if (tipo.equals("recordatorio")) {
            icono.setImageResource(R.drawable.ic_calendar);
            icono.setBackgroundResource(R.drawable.bg_icon_blue_light);
            icono.setColorFilter(0xFF2D7DD2);
        } else if (tipo.equals("clinica")) {
            icono.setImageResource(R.drawable.ic_consulta);
            icono.setBackgroundResource(R.drawable.bg_icon_green_light);
            icono.setColorFilter(0xFF43A047);
        } else if (tipo.equals("cita")) {
            icono.setImageResource(R.drawable.ic_check);
            icono.setBackgroundResource(R.drawable.bg_icon_green_light);
            icono.setColorFilter(0xFF4CAF50);
        } else if (tipo.equals("revision")) {
            icono.setImageResource(R.drawable.ic_calendar);
            icono.setBackgroundResource(R.drawable.bg_icon_blue_light);
            icono.setColorFilter(0xFF2D7DD2);
        } else if (tipo.equals("cancelada")) {
            icono.setImageResource(R.drawable.ic_cancel);
            icono.setBackgroundResource(R.drawable.bg_icon_red_light);
            icono.setColorFilter(0xFFD32F2F);

        } else if (tipo.equals("reprogramada")) {
            icono.setImageResource(R.drawable.ic_calendar);
            icono.setBackgroundResource(R.drawable.bg_icon_purple);
            icono.setColorFilter(0xFF7E57C2);

        } else {
            icono.setImageResource(R.drawable.ic_help);
            icono.setBackgroundResource(R.drawable.bg_icon_orange_light);
            icono.setColorFilter(0xFFFF9800);
        }

        contenido.addView(icono);

        LinearLayout textos = new LinearLayout(this);
        textos.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams textosParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );

        textosParams.setMargins(dp(12), 0, 0, 0);
        textos.setLayoutParams(textosParams);

        TextView txtTitulo = new TextView(this);
        txtTitulo.setText(titulo);
        txtTitulo.setTextSize(14);
        txtTitulo.setTextColor(0xFF333333);
        txtTitulo.setTypeface(null, android.graphics.Typeface.BOLD);

        TextView txtMensaje = new TextView(this);
        txtMensaje.setText(mensaje);
        txtMensaje.setTextSize(11);
        txtMensaje.setTextColor(0xFF333333);

        TextView txtTiempo = new TextView(this);
        txtTiempo.setText(tiempo);
        txtTiempo.setTextSize(10);
        txtTiempo.setTextColor(0xFF999999);

        textos.addView(txtTitulo);
        textos.addView(txtMensaje);
        textos.addView(txtTiempo);

        contenido.addView(textos);
        horizontal.addView(contenido);
        frame.addView(horizontal);

        View puntoRojo = new View(this);
        FrameLayout.LayoutParams puntoParams = new FrameLayout.LayoutParams(dp(11), dp(11));
        puntoParams.gravity = android.view.Gravity.TOP | android.view.Gravity.END;
        puntoParams.setMargins(0, dp(8), dp(8), 0);

        puntoRojo.setLayoutParams(puntoParams);
        puntoRojo.setBackgroundResource(R.drawable.bg_dot_red);
        puntoRojo.setVisibility(leida ? View.GONE : View.VISIBLE);

        frame.addView(puntoRojo);
        card.addView(frame);

        card.setOnClickListener(v -> {
            if (!estaLeida(id)) {
                guardarLeida(id);
                puntoRojo.setVisibility(View.GONE);
                cargarNotificaciones();
            }
        });

        return card;
    }

    private boolean estaLeida(String id) {
        SharedPreferences prefs = prefsLeidasUsuario();
        return prefs.getBoolean(id, false);
    }

    private void guardarLeida(String id) {
        prefsLeidasUsuario().edit().putBoolean(id, true).apply();
    }

    public static void guardarNotificacion(Context context, String titulo, String mensaje, String tipo) {
        String correo = MainActivity.obtenerCorreoActual(context);

        SharedPreferences prefs = context.getSharedPreferences(
                LoginActivity.prefsNotificaciones(correo),
                Context.MODE_PRIVATE
        );

        Set<String> lista = prefs.getStringSet("lista", new HashSet<>());
        Set<String> nuevaLista = new HashSet<>(lista);

        String id = System.currentTimeMillis() + "_" + java.util.UUID.randomUUID().toString();

        String notificacion = id
                + "|" + titulo
                + "|" + mensaje
                + "|" + tipo
                + "|Ahora";

        nuevaLista.add(notificacion);

        prefs.edit()
                .putStringSet("lista", nuevaLista)
                .apply();

        context.getSharedPreferences(
                        LoginActivity.prefsNotificacionesLeidas(correo),
                        Context.MODE_PRIVATE
                )
                .edit()
                .putBoolean(id, false)
                .apply();
    }

    private void limpiarDatosDePruebaUnaVez() {
        SharedPreferences prefs = getSharedPreferences("config_app", MODE_PRIVATE);
        boolean limpio = prefs.getBoolean("limpio_datos_antiguos", false);

        if (!limpio) {
            getSharedPreferences("citas", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("notificaciones", MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences("notificaciones_leidas", MODE_PRIVATE).edit().clear().apply();

            prefs.edit().putBoolean("limpio_datos_antiguos", true).apply();
        }
    }
}