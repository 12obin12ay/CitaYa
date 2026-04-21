package com.codelabs.citaya;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class CitasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas);

        // Botón volver
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Referencias de tabs
        TextView tabProximas = findViewById(R.id.tabProximas);
        TextView tabAnteriores = findViewById(R.id.tabAnteriores);

        // Referencias de contenido
        LinearLayout layoutProximas = findViewById(R.id.layoutProximas);
        LinearLayout layoutAnteriores = findViewById(R.id.layoutAnteriores);

        // Estado inicial
        layoutProximas.setVisibility(View.VISIBLE);
        layoutAnteriores.setVisibility(View.GONE);

        tabProximas.setBackgroundResource(R.drawable.bg_tab_selected);
        tabProximas.setTextColor(getResources().getColor(android.R.color.white));

        tabAnteriores.setBackground(null);
        tabAnteriores.setTextColor(getResources().getColor(android.R.color.darker_gray));

        // Click PROXIMAS
        tabProximas.setOnClickListener(v -> {
            layoutProximas.setVisibility(View.VISIBLE);
            layoutAnteriores.setVisibility(View.GONE);

            activarTab(tabProximas, tabAnteriores);
        });

        // Click ANTERIORES
        tabAnteriores.setOnClickListener(v -> {
            layoutProximas.setVisibility(View.GONE);
            layoutAnteriores.setVisibility(View.VISIBLE);

            activarTab(tabAnteriores, tabProximas);
        });
    }

    // Método para cambiar estilos
    private void activarTab(TextView activo, TextView inactivo){
        activo.setBackgroundResource(R.drawable.bg_tab_selected);
        activo.setTextColor(getResources().getColor(android.R.color.white));

        inactivo.setBackground(null);
        inactivo.setTextColor(getResources().getColor(android.R.color.darker_gray));
    }
}