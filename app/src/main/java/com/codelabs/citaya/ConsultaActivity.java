package com.codelabs.citaya;

import com.codelabs.citaya.network.*;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConsultaActivity extends AppCompatActivity {

    private EditText input;
    private ProgressBar progressBar;
    private MaterialButton btnAnalizar;

    private static final String API_KEY = "AIzaSyDwZXa_HPlpqnJbLXRzS7dOsyfCTvWr4xc";
    private Set<String> sintomasSeleccionados = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        // Vincular vistas
        input = findViewById(R.id.inputSintomas);
        progressBar = findViewById(R.id.progressConsulta);
        btnAnalizar = findViewById(R.id.btnSintomas);

        // Botón volver
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Acción principal
        btnAnalizar.setOnClickListener(v -> analizar());

        // Configurar botones de síntomas rápidos
        configurarBotonSintoma(R.id.btnDolorCabeza);
        configurarBotonSintoma(R.id.btnFiebre);
        configurarBotonSintoma(R.id.btnTos);
        configurarBotonSintoma(R.id.btnVomito);
        configurarBotonSintoma(R.id.btnDiarrea);
        configurarBotonSintoma(R.id.btnFatiga);

        // Escuchar cambios en el texto
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                sincronizarSintomasDesdeTexto(s.toString());
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void configurarBotonSintoma(int id) {
        MaterialButton btn = findViewById(id);
        if (btn == null) return;

        btn.setOnClickListener(v -> {
            String sintoma = btn.getText().toString().toLowerCase();

            if (sintomasSeleccionados.contains(sintoma)) {
                sintomasSeleccionados.remove(sintoma);
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
                btn.setTextColor(Color.parseColor("#2D7DD2"));
            } else {
                if (sintomasSeleccionados.size() >= 3) {
                    Snackbar.make(input, "Máximo 3 síntomas", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                sintomasSeleccionados.add(sintoma);
                btn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#2D7DD2")));
                btn.setTextColor(Color.WHITE);
            }

            input.setText(String.join(", ", sintomasSeleccionados));
        });
    }

    private void sincronizarSintomasDesdeTexto(String texto) {
        String limpio = texto.toLowerCase().trim();
        sintomasSeleccionados.clear();
        if (limpio.isEmpty()) return;

        String[] partes = limpio.split("\\s*,\\s*");
        for (String p : partes) {
            if (!p.isEmpty()) sintomasSeleccionados.add(p);
        }
    }

    private void analizar() {
        if (sintomasSeleccionados.isEmpty() && input.getText().toString().trim().isEmpty()) {
            Snackbar.make(input, "Por favor, ingresa tus síntomas", Snackbar.LENGTH_SHORT).show();
            return;
        }
        consultarGemini(String.join(", ", sintomasSeleccionados));
    }

    private void consultarGemini(String sintomasUsuario) {
        GeminiApi api = RetrofitClient.getClient().create(GeminiApi.class);

        String prompt = "Actúa como asistente médico preliminar de triaje. " +
                "Analiza los siguientes síntomas y responde EXACTAMENTE en este formato:\n\n" +
                "ORIENTACION: una explicación corta y profesional\n" +
                "NIVEL: LEVE o MODERADO o GRAVE\n" +
                "ESPECIALIDAD: una sola especialidad sugerida\n" +
                "RECOMENDACIONES: exactamente 4 recomendaciones cortas separadas por | \n\n" +
                "Paciente: " + sintomasUsuario;

        GeminiRequest request = new GeminiRequest(prompt);
        mostrarCarga();

        api.generar(API_KEY, request).enqueue(new retrofit2.Callback<GeminiResponse>() {
            @Override
            public void onResponse(retrofit2.Call<GeminiResponse> call, retrofit2.Response<GeminiResponse> response) {
                ocultarCarga();
                if (response.isSuccessful() && response.body() != null) {
                    String textoIA = response.body().candidates.get(0).content.parts.get(0).text;
                    procesarRespuestaIA(textoIA, sintomasUsuario);
                } else {
                    Snackbar.make(input, "La IA está ocupada. Intenta de nuevo.", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<GeminiResponse> call, Throwable t) {
                ocultarCarga();
                Snackbar.make(input, "Error: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void procesarRespuestaIA(String textoIA, String sintomas) {
        String orientacion = "", nivel = "LEVE", especialidad = "Medicina General", recomendaciones = "";

        String[] lineas = textoIA.split("\n");
        for (String linea : lineas) {
            if (linea.startsWith("ORIENTACION:")) orientacion = linea.replace("ORIENTACION:", "").trim();
            else if (linea.startsWith("NIVEL:")) nivel = linea.replace("NIVEL:", "").trim();
            else if (linea.startsWith("ESPECIALIDAD:")) especialidad = linea.replace("ESPECIALIDAD:", "").trim();
            else if (linea.startsWith("RECOMENDACIONES:")) recomendaciones = linea.replace("RECOMENDACIONES:", "").trim();
        }

        // Guardar notificación de resultado
        NotificacionesActivity.guardarNotificacion(this, "Triaje completado", 
                "Tu nivel de urgencia es " + nivel + ". Ve a resultados para ver detalles.", "analisis");

        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra("sintomas", sintomas);
        intent.putExtra("resultado", orientacion);
        intent.putExtra("nivel", nivel);
        intent.putExtra("especialidad", especialidad);
        intent.putExtra("recomendaciones", recomendaciones);
        startActivity(intent);
    }

    private void mostrarCarga() {
        progressBar.setVisibility(View.VISIBLE);
        btnAnalizar.setEnabled(false);
        btnAnalizar.setText("Analizando...");
    }

    private void ocultarCarga() {
        progressBar.setVisibility(View.GONE);
        btnAnalizar.setEnabled(true);
        btnAnalizar.setText("Analizar consulta");
    }
}
