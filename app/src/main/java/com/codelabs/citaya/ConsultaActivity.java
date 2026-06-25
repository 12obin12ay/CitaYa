package com.codelabs.citaya;

import com.codelabs.citaya.network.*;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedHashSet;
import java.util.Set;

public class ConsultaActivity extends AppCompatActivity {

    private EditText input;

    private static final String API_KEY = "";

    private Set<String> sintomasSeleccionados = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        input = findViewById(R.id.inputSintomas);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnSintomas).setOnClickListener(v -> analizar());

        configurarBoton(R.id.btnDolorCabeza);
        configurarBoton(R.id.btnFiebre);
        configurarBoton(R.id.btnTos);
        configurarBoton(R.id.btnVomito);
        configurarBoton(R.id.btnDiarrea);
        configurarBoton(R.id.btnFatiga);

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String texto = s.toString().toLowerCase().trim();
                sintomasSeleccionados.clear();

                if (texto.isEmpty()) return;

                String[] partes = texto.split("\\s*,\\s*");

                for (String p : partes) {
                    if (!p.isEmpty()) {
                        sintomasSeleccionados.add(p);
                    }
                }
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    private void configurarBoton(int id) {
        MaterialButton btn = findViewById(id);

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

    private void analizar() {
        if (sintomasSeleccionados.isEmpty()) {
            Snackbar.make(input, "Ingresa síntomas", Snackbar.LENGTH_SHORT).show();
            return;
        }

        consultarGemini(String.join(", ", sintomasSeleccionados));
    }

    private void consultarGemini(String sintomasUsuario) {
        GeminiApi api = RetrofitClient.getClient().create(GeminiApi.class);

        String prompt =
                "Actúa como asistente médico preliminar.\n" +
                        "SOLO acepta síntomas médicos.\n" +
                        "Si el usuario no escribe síntomas médicos responde EXACTAMENTE:\n" +
                        "INVALIDO\n" +
                        "No escribiste síntomas médicos válidos.\n\n" +
                        "Si sí son síntomas responde EXACTAMENTE ESTE FORMATO:\n\n" +
                        "ORIENTACION: una explicación corta\n" +
                        "NIVEL: LEVE o MODERADO o GRAVE\n" +
                        "ESPECIALIDAD: una sola especialidad médica\n" +
                        "RECOMENDACIONES: EXACTAMENTE 4 recomendaciones separadas por | \n" +
                        "Ejemplo:\n" +
                        "RECOMENDACIONES: Descanso|Hidratación|Consulta médica|Evitar esfuerzo\n\n" +
                        "Paciente:\n" +
                        sintomasUsuario;

        GeminiRequest request = new GeminiRequest(prompt);

        api.generar(API_KEY, request)
                .enqueue(new retrofit2.Callback<GeminiResponse>() {
                    @Override
                    public void onResponse(
                            retrofit2.Call<GeminiResponse> call,
                            retrofit2.Response<GeminiResponse> response
                    ) {
                        if (response.isSuccessful() && response.body() != null) {
                            String textoIA = response.body()
                                    .candidates
                                    .get(0)
                                    .content
                                    .parts
                                    .get(0)
                                    .text;

                            Log.d("GEMINI_OK", textoIA);

                            if (textoIA.startsWith("INVALIDO")) {
                                Snackbar.make(
                                        input,
                                        "Solo escribe síntomas médicos.",
                                        Snackbar.LENGTH_LONG
                                ).show();
                                return;
                            }

                            String orientacion = "";
                            String nivel = "LEVE";
                            String especialidad = "Medicina General";
                            String recomendaciones = "";

                            String[] lineas = textoIA.split("\n");

                            for (String linea : lineas) {
                                if (linea.startsWith("ORIENTACION:")) {
                                    orientacion = linea.replace("ORIENTACION:", "").trim();
                                } else if (linea.startsWith("NIVEL:")) {
                                    nivel = linea.replace("NIVEL:", "").trim();
                                } else if (linea.startsWith("ESPECIALIDAD:")) {
                                    especialidad = linea.replace("ESPECIALIDAD:", "").trim();
                                } else if (linea.startsWith("RECOMENDACIONES:")) {
                                    recomendaciones = linea.replace("RECOMENDACIONES:", "").trim();
                                }
                            }

                            NotificacionesActivity.guardarNotificacion(
                                    ConsultaActivity.this,
                                    "Resultado de análisis",
                                    "Tu orientación médica indica nivel " + nivel +
                                            ". Especialidad sugerida: " + especialidad,
                                    "analisis"
                            );

                            Intent intent = new Intent(
                                    ConsultaActivity.this,
                                    ResultadoActivity.class
                            );

                            intent.putExtra("sintomas", String.join(", ", sintomasSeleccionados));
                            intent.putExtra("resultado", orientacion);
                            intent.putExtra("nivel", nivel);
                            intent.putExtra("especialidad", especialidad);
                            intent.putExtra("recomendaciones", recomendaciones);

                            startActivity(intent);

                        } else {
                            Log.d("GEMINI_ERROR", "Code: " + response.code());
                            Snackbar.make(
                                    input,
                                    "No se pudo obtener respuesta de la IA.",
                                    Snackbar.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            retrofit2.Call<GeminiResponse> call,
                            Throwable t
                    ) {
                        Snackbar.make(
                                input,
                                "Error IA: " + t.getMessage(),
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                });
    }
}