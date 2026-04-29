package com.codelabs.citaya;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ResultadoActivity extends AppCompatActivity {

    private TextView txtSintomas, txtResultado, txtEspecialidad;
    private TextView rec1, rec2, rec3, rec4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resultado);

        txtSintomas = findViewById(R.id.txtSintomas);
        txtResultado = findViewById(R.id.txtResultado);
        txtEspecialidad = findViewById(R.id.txtEspecialidad);

        rec1 = findViewById(R.id.rec1);
        rec2 = findViewById(R.id.rec2);
        rec3 = findViewById(R.id.rec3);
        rec4 = findViewById(R.id.rec4);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnVerDoctores).setOnClickListener(v ->
                startActivity(new Intent(this, EspecialidadesActivity.class)));

        // DATOS
        String sintomas = getIntent().getStringExtra("sintomas");
        String resultado = getIntent().getStringExtra("resultado");
        String especialidad = getIntent().getStringExtra("especialidad");
        String recomendaciones = getIntent().getStringExtra("recomendaciones");
        String nivel = getIntent().getStringExtra("nivel");

        if (sintomas == null) sintomas = "No especificado";
        if (resultado == null) resultado = "Sin resultados";
        if (especialidad == null) especialidad = "Medicina General";
        if (nivel == null || nivel.isEmpty()) nivel = "LEVE";

        final String finalEspecialidad = especialidad;
        final String finalNivel = nivel;
        final String finalSintomas = sintomas;

        findViewById(R.id.btnReservar).setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservaActivity.class);
            intent.putExtra("especialidad", finalEspecialidad);
            intent.putExtra("nivel", finalNivel);
            intent.putExtra("sintomas", finalSintomas);
            startActivity(intent);
        });

        txtSintomas.setText(sintomas);

        if ("GRAVE".equals(nivel)) {
            txtResultado.setText("🔴 URGENTE\n" + resultado);
            txtResultado.setTextColor(Color.RED);

        } else if ("MODERADO".equals(nivel)) {
            txtResultado.setText("🟡 ATENCION\n" + resultado);
            txtResultado.setTextColor(Color.parseColor("#FFA000"));

        } else {
            txtResultado.setText("🟢 LEVE\n" + resultado);
            txtResultado.setTextColor(Color.parseColor("#2E7D32"));
        }

        txtEspecialidad.setText(
                "Te recomendamos acudir a " + especialidad +
                        " para una evaluación completa."
        );

        if (recomendaciones != null && !recomendaciones.isEmpty()) {

            String[] recs = recomendaciones.split("\\|");

            rec1.setText(recs.length > 0 ? recs[0] : "");
            rec2.setText(recs.length > 1 ? recs[1] : "");
            rec3.setText(recs.length > 2 ? recs[2] : "");
            rec4.setText(recs.length > 3 ? recs[3] : "");

        } else {

            rec1.setText("• Descansar");
            rec2.setText("• Hidratación");
            rec3.setText("• Monitorear síntomas");
            rec4.setText("• Consultar médico");
        }

        getSharedPreferences("salud", MODE_PRIVATE)
                .edit()
                .putString("nivel", nivel)
                .apply();
    }
}