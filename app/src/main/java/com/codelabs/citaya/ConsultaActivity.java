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

import java.util.*;

public class ConsultaActivity extends AppCompatActivity {

    private EditText input;

    private static final String API_KEY = "AIzaSyCRFeIqMfPOSHZkEBp9OZ684sXW-PL3Sow";
    private Set<String> sintomasSeleccionados = new LinkedHashSet<>();

    // MODELO (AGREGADO NIVEL)
    private static class Enfermedad {
        String nombre, especialidad, nivel;
        List<String> sintomas, recomendaciones;

        Enfermedad(String n, String e, String nivel, List<String> s, List<String> r) {
            nombre = n;
            especialidad = e;
            this.nivel = nivel;
            sintomas = s;
            recomendaciones = r;
        }
    }

    // 🔥 BASE (MISMA, SOLO SE AGREGÓ NIVEL)
    private List<Enfermedad> base = Arrays.asList(

            new Enfermedad("Gripe","Medicina General","MODERADO",
                    Arrays.asList("fiebre","tos","congestion nasal"),
                    Arrays.asList("• Descanso","• Hidratación","• Controlar fiebre","• Evitar frío")),

            new Enfermedad("Resfriado común","Medicina General","LEVE",
                    Arrays.asList("estornudos","congestion nasal","dolor de garganta"),
                    Arrays.asList("• Descanso","• Líquidos","• Analgésicos","• Evitar cambios bruscos")),

            new Enfermedad("COVID-19","Medicina General","MODERADO",
                    Arrays.asList("fiebre","tos","fatiga"),
                    Arrays.asList("• Aislamiento","• Hidratación","• Control médico","• Descanso")),

            new Enfermedad("Gastroenteritis","Gastroenterología","MODERADO",
                    Arrays.asList("diarrea","vomito","dolor abdominal"),
                    Arrays.asList("• Suero oral","• Dieta blanda","• Evitar grasas","• Hidratación")),

            new Enfermedad("Gastritis","Gastroenterología","LEVE",
                    Arrays.asList("dolor abdominal","acidez","nauseas"),
                    Arrays.asList("• Evitar irritantes","• Dieta suave","• Consulta médica","• No alcohol")),

            new Enfermedad("Migraña","Neurología","LEVE",
                    Arrays.asList("dolor de cabeza","nauseas"),
                    Arrays.asList("• Reposo","• Evitar luz","• Hidratación","• Analgésicos")),

            new Enfermedad("Sinusitis","Otorrinolaringología","MODERADO",
                    Arrays.asList("dolor de cabeza","congestion nasal"),
                    Arrays.asList("• Vapor","• Hidratación","• Analgésicos","• Consulta médica")),

            new Enfermedad("Faringitis","Medicina General","MODERADO",
                    Arrays.asList("dolor de garganta","fiebre"),
                    Arrays.asList("• Líquidos","• Reposo","• Analgésicos","• Consulta")),

            new Enfermedad("Bronquitis","Neumología","MODERADO",
                    Arrays.asList("tos","fatiga"),
                    Arrays.asList("• Hidratación","• Evitar humo","• Reposo","• Consulta médica")),

            new Enfermedad("Neumonía","Neumología","GRAVE",
                    Arrays.asList("fiebre","tos","dificultad para respirar"),
                    Arrays.asList("• Atención médica","• Reposo","• No automedicarse","• Control")),

            new Enfermedad("Dengue","Medicina General","MODERADO",
                    Arrays.asList("fiebre","dolor muscular"),
                    Arrays.asList("• Hidratación","• Reposo","• Consulta médica","• No automedicarse")),

            new Enfermedad("Anemia","Hematología","LEVE",
                    Arrays.asList("fatiga","mareo"),
                    Arrays.asList("• Dieta rica en hierro","• Suplementos","• Consulta médica","• Descanso")),

            new Enfermedad("Alergia","Alergología","LEVE",
                    Arrays.asList("estornudos","picazon"),
                    Arrays.asList("• Evitar alérgenos","• Antihistamínicos","• Limpieza","• Consulta")),

            new Enfermedad("Asma","Neumología","MODERADO",
                    Arrays.asList("falta de aire","tos"),
                    Arrays.asList("• Evitar desencadenantes","• Inhalador","• Consulta","• Control")),

            new Enfermedad("Infarto","Cardiología","GRAVE",
                    Arrays.asList("dolor en el pecho","falta de aire"),
                    Arrays.asList("• Emergencia inmediata","• No esfuerzo","• Reposo","• Atención urgente"))
    );

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
                    if (!p.isEmpty()) sintomasSeleccionados.add(p);
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

        consultarGemini(
                String.join(", ", sintomasSeleccionados)
        );

        return;

        /*Set<String> sintomasSet = new HashSet<>(sintomasSeleccionados);

        List<Resultado> resultados = diagnosticarTop(sintomasSet);

        StringBuilder resultadoTexto = new StringBuilder();
        Set<String> recomendacionesFinal = new LinkedHashSet<>();
        String especialidad = "Medicina General";

        // 🔥 NUEVO
        String nivel = "LEVE";

        for (int i = 0; i < resultados.size(); i++) {

            Resultado r = resultados.get(i);

            resultadoTexto.append("• ")
                    .append(r.enfermedad.nombre)
                    .append("\n");

            recomendacionesFinal.addAll(r.enfermedad.recomendaciones);

            if (i == 0) {
                especialidad = r.enfermedad.especialidad;
                nivel = r.enfermedad.nivel; // 🔥 CLAVE
            }
        }

        if (resultadoTexto.length() == 0) {
            resultadoTexto.append("No se encontraron coincidencias claras");

            recomendacionesFinal.add("• Descansar");
            recomendacionesFinal.add("• Hidratación");
            recomendacionesFinal.add("• Monitorear síntomas");
            recomendacionesFinal.add("• Consultar médico");
        }

        Intent intent = new Intent(this, ResultadoActivity.class);
        intent.putExtra("sintomas", String.join(", ", sintomasSeleccionados));
        intent.putExtra("resultado", resultadoTexto.toString());
        intent.putExtra("especialidad", especialidad);
        intent.putExtra("recomendaciones", String.join("|", recomendacionesFinal));

        // 🔥 ENVÍO DEL NIVEL
        intent.putExtra("nivel", nivel);

        startActivity(intent); */
    }

    private static class Resultado {
        Enfermedad enfermedad;
        int score;

        Resultado(Enfermedad e, int s) {
            enfermedad = e;
            score = s;
        }
    }

    private List<Resultado> diagnosticarTop(Set<String> sintomasUsuario) {

        List<Resultado> lista = new ArrayList<>();

        for (Enfermedad e : base) {

            int coincidencias = 0;

            for (String s : e.sintomas) {
                if (sintomasUsuario.contains(s)) {
                    coincidencias++;
                }
            }

            if (coincidencias > 0) {
                lista.add(new Resultado(e, coincidencias));
            }
        }

        lista.sort((a, b) -> b.score - a.score);

        return lista.size() > 3 ? lista.subList(0, 3) : lista;
    }

    private void consultarGemini(String sintomasUsuario){

        GeminiApi api = RetrofitClient.getClient().create(GeminiApi.class);

        String prompt =

                "Actúa como asistente médico preliminar.\n"+

                        "SOLO acepta síntomas médicos.\n"+

                        "Si el usuario no escribe síntomas médicos responde EXACTAMENTE:\n"+
                        "INVALIDO\n"+
                        "No escribiste síntomas médicos válidos.\n\n"+

                        "Si sí son síntomas responde EXACTAMENTE ESTE FORMATO:\n\n"+

                        "ORIENTACION: una explicación corta\n"+
                        "NIVEL: LEVE o MODERADO o GRAVE\n"+
                        "ESPECIALIDAD: una sola especialidad médica\n"+

                        "RECOMENDACIONES: EXACTAMENTE 4 recomendaciones separadas por | \n"+

                        "Ejemplo:\n"+
                        "RECOMENDACIONES: Descanso|Hidratación|Consulta médica|Evitar esfuerzo\n\n"+

                        "Paciente:\n"+

                        sintomasUsuario;

        GeminiRequest request = new GeminiRequest(prompt);

        api.generar(API_KEY, request)

                .enqueue(

                        new retrofit2.Callback<GeminiResponse>() {

                            @Override
                            public void onResponse(

                                    retrofit2.Call<GeminiResponse> call,

                                    retrofit2.Response<GeminiResponse> response
                            ) {

                                if(response.isSuccessful() && response.body()!=null){

                                    String textoIA =
                                            response.body()
                                                    .candidates
                                                    .get(0)
                                                    .content
                                                    .parts
                                                    .get(0)
                                                    .text;

                                    Log.d("GEMINI_OK", textoIA);

                                    if(textoIA.startsWith("INVALIDO")){

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

                                    for(String linea : lineas){

                                        if(linea.startsWith("ORIENTACION:")){

                                            orientacion =
                                                    linea.replace("ORIENTACION:","").trim();

                                        }

                                        else if(linea.startsWith("NIVEL:")){

                                            nivel =
                                                    linea.replace("NIVEL:","").trim();

                                        }

                                        else if(linea.startsWith("ESPECIALIDAD:")){

                                            especialidad =
                                                    linea.replace("ESPECIALIDAD:","").trim();

                                        }

                                        else if(linea.startsWith("RECOMENDACIONES:")){

                                            recomendaciones =
                                                    linea.replace("RECOMENDACIONES:","").trim();

                                        }
                                    }

                                    Intent intent =
                                            new Intent(
                                                    ConsultaActivity.this,
                                                    ResultadoActivity.class
                                            );

                                    intent.putExtra(
                                            "sintomas",
                                            String.join(", ", sintomasSeleccionados)
                                    );

                                    intent.putExtra(
                                            "resultado",
                                            orientacion
                                    );

                                    intent.putExtra(
                                            "nivel",
                                            nivel
                                    );

                                    intent.putExtra(
                                            "especialidad",
                                            especialidad
                                    );

                                    intent.putExtra(
                                            "recomendaciones",
                                            recomendaciones
                                    );

                                    startActivity(intent);

                                }else{

                                    Log.d("GEMINI_ERROR",
                                            "Code: " + response.code());

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
                        }

                );
    }
}