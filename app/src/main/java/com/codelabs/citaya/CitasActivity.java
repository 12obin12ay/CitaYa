package com.codelabs.citaya;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashSet;
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

        // Estado inicial
        layoutProximas.setVisibility(View.VISIBLE);
        layoutAnteriores.setVisibility(View.GONE);

        tabProximas.setBackgroundResource(R.drawable.bg_tab_selected);
        tabProximas.setTextColor(getResources().getColor(android.R.color.white));

        tabAnteriores.setBackground(null);
        tabAnteriores.setTextColor(getResources().getColor(android.R.color.darker_gray));

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

    private void cargarCitas() {

        SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);
        Set<String> citas = prefs.getStringSet("lista", new HashSet<>());

        contenedorProximas.removeAllViews();
        contenedorAnteriores.removeAllViews();

        if (citas == null || citas.isEmpty()) return;

        int contador = 0;

        for (String c : citas) {

            if (contador >= 3) break;

            String[] datos = c.split("\\|");
            if (datos.length < 6) continue;

            View card = getLayoutInflater().inflate(R.layout.activity_card_citas, contenedorProximas, false);

            ((TextView) card.findViewById(R.id.txtNombre)).setText(datos[0]);
            ((TextView) card.findViewById(R.id.txtEspecialidad)).setText(datos[1]);
            ((TextView) card.findViewById(R.id.txtFecha)).setText(datos[2]);
            ((TextView) card.findViewById(R.id.txtHora)).setText(datos[3]);
            ((TextView) card.findViewById(R.id.txtUbicacion)).setText(datos[4]);

            TextView txtEstado = card.findViewById(R.id.txtEstado);

            if ("PENDIENTE".equals(datos[5])) {
                txtEstado.setText(" Pendiente");
                txtEstado.setTextColor(getResources().getColor(android.R.color.holo_orange_dark));
                contenedorProximas.addView(card);
            } else {
                txtEstado.setText(" Confirmada");
                txtEstado.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                contenedorAnteriores.addView(card);
            }

            contador++;
        }
    }

    private void activarTab(TextView activo, TextView inactivo){

        activo.setBackgroundResource(R.drawable.bg_tab_selected);
        activo.setTextColor(getResources().getColor(android.R.color.white));

        inactivo.setBackground(null);
        inactivo.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }
}