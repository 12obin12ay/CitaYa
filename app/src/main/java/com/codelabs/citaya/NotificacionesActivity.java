package com.codelabs.citaya;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class NotificacionesActivity extends AppCompatActivity {

    private TextView txtResumen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);

        txtResumen = findViewById(R.id.txtResumen);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        setupCard(R.id.cardRecordatorio, R.id.punto1, "cardRecordatorio");
        setupCard(R.id.cardMensaje, R.id.punto2, "cardMensaje");
        setupCard(R.id.cardResultado, R.id.punto3, "cardResultado");
        setupCard(R.id.cardCita, R.id.punto4, "cardCita");
        setupCard(R.id.cardCitaprox, R.id.punto5, "cardCitaprox");

        actualizarResumen();
    }

    private void setupCard(int cardId, int puntoId, String key) {

        CardView card = findViewById(cardId);
        View punto = findViewById(puntoId);

        // Estado inicial
        if (MainActivity.leidas.contains(key)) {
            punto.setVisibility(View.GONE);
        } else {
            punto.setVisibility(View.VISIBLE);
        }

        card.setOnClickListener(v -> {

            if (!MainActivity.leidas.contains(key)) {
                MainActivity.leidas.add(key);
                actualizarResumen(); // 🔥 actualizar texto en tiempo real
            }

            punto.setVisibility(View.GONE);
        });
    }

    //  TEXTO DINÁMICO INTELIGENTE
    private void actualizarResumen() {

        int cantidad = MainActivity.getNoLeidas();

        if (cantidad > 1) {
            txtResumen.setText(cantidad + " notificaciones nuevas");
        } else if (cantidad == 1) {
            txtResumen.setText("1 notificación nueva");
        } else {
            // 😎 aquí lo “pro”
            txtResumen.setText("No tienes notificaciones nuevas");
        }
    }
}