package com.codelabs.citaya;
import com.google.android.material.snackbar.Snackbar;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class EspecialidadesActivity extends AppCompatActivity {

    LinearLayout doc1, doc2, doc3, doc4;
    ImageView check1, check2, check3, check4;
    LinearLayout layoutFecha;
    EditText etFecha;

    LinearLayout layoutHorarios;

    LinearLayout hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9;

    MaterialButton btnConfirmar;

    private String horaSeleccionada = "";

    private String doctorSeleccionado = "";
    private String especialidadSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_especialidades);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        doc1 = findViewById(R.id.doc1);
        doc2 = findViewById(R.id.doc2);
        doc3 = findViewById(R.id.doc3);
        doc4 = findViewById(R.id.doc4);

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);

        layoutFecha = findViewById(R.id.layoutFecha);
        etFecha = findViewById(R.id.etFecha);

        layoutHorarios = findViewById(R.id.layoutHorarios);

        hora1 = findViewById(R.id.hora1);
        hora2 = findViewById(R.id.hora2);
        hora3 = findViewById(R.id.hora3);
        hora4 = findViewById(R.id.hora4);
        hora5 = findViewById(R.id.hora5);
        hora6 = findViewById(R.id.hora6);
        hora7 = findViewById(R.id.hora7);
        hora8 = findViewById(R.id.hora8);
        hora9 = findViewById(R.id.hora9);

        btnConfirmar = findViewById(R.id.btnConfirmar);

        // DOCTORES
        doc1.setOnClickListener(v -> { seleccionar(1); doctorSeleccionado = "Dr. Carlos Rodríguez"; especialidadSeleccionada = "Cardiología"; });
        doc2.setOnClickListener(v -> { seleccionar(2); doctorSeleccionado = "Dra. María González"; especialidadSeleccionada = "Medicina General"; });
        doc3.setOnClickListener(v -> { seleccionar(3); doctorSeleccionado = "Dr. Luis Mendoza"; especialidadSeleccionada = "Pediatría"; });
        doc4.setOnClickListener(v -> { seleccionar(4); doctorSeleccionado = "Dra. Ana Fernández"; especialidadSeleccionada = "Dermatología"; });

        etFecha.setOnClickListener(v -> mostrarCalendario());

        LinearLayout[] horas = {
                hora1, hora2, hora3,
                hora4, hora5, hora6,
                hora7, hora8, hora9
        };

        for (LinearLayout h : horas) {
            h.setOnClickListener(v -> seleccionarHora((LinearLayout) v));
        }

        // CONFIRMAR
        btnConfirmar.setOnClickListener(v -> {

            if (doctorSeleccionado.isEmpty()) {
                Toast.makeText(this, "Selecciona un doctor", Toast.LENGTH_SHORT).show();
                return;
            }

            if (etFecha.getText().toString().isEmpty()) {
                Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
                return;
            }

            if (horaSeleccionada.isEmpty()) {
                Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show();
                return;
            }

            String fecha = etFecha.getText().toString();
            String ubicacion = generarConsultorio(especialidadSeleccionada);

            String cita = doctorSeleccionado + "|" +
                    especialidadSeleccionada + "|" +
                    fecha + "|" +
                    horaSeleccionada + "|" +
                    ubicacion + "|PENDIENTE";

            SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);

            Set<String> citas = prefs.getStringSet("lista", new HashSet<>());
            Set<String> nuevaLista = new HashSet<>(citas);

            // 🔥 NUEVO: HORARIOS OCUPADOS
            Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());
            Set<String> copiaOcupados = new HashSet<>(ocupados);

            String clave = fecha + "_" + horaSeleccionada;

            // 🔴 VALIDAR DUPLICADO REAL
            if (copiaOcupados.contains(clave)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Este horario ya está ocupado",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }

            // 🔴 MÁXIMO 3
            if (nuevaLista.size() >= 3) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Máximo 3 citas permitidas",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }

            nuevaLista.add(cita);
            copiaOcupados.add(clave);

            prefs.edit()
                    .putStringSet("lista", nuevaLista)
                    .putStringSet("ocupados", copiaOcupados)
                    .apply();

            Snackbar.make(findViewById(android.R.id.content),
                    "Cita Registrada",
                    Snackbar.LENGTH_SHORT).show();

            startActivity(new Intent(this, CitasActivity.class));
        });
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(this,
                (view, y, m, d) -> {

                    String fechaSeleccionada = d + "/" + (m + 1) + "/" + y;
                    etFecha.setText(fechaSeleccionada);
                    layoutHorarios.setVisibility(View.VISIBLE);

                    SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);
                    Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());

                    LinearLayout[] horas = {
                            hora1, hora2, hora3,
                            hora4, hora5, hora6,
                            hora7, hora8, hora9
                    };

                    for (LinearLayout h : horas) {
                        String hora = obtenerHora(h);
                        String clave = fechaSeleccionada + "_" + hora;

                        if (ocupados.contains(clave)) {
                            h.setEnabled(false);
                            h.setAlpha(0.3f);
                        } else {
                            h.setEnabled(true);
                            h.setAlpha(1f);
                        }
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));

        dp.show();
    }

    private String obtenerHora(LinearLayout layout) {
        TextView txt = (TextView) layout.getChildAt(1);
        return txt.getText().toString();
    }

    private void seleccionarHora(LinearLayout seleccionada) {

        if (!seleccionada.isEnabled()) return;

        LinearLayout[] horas = {
                hora1, hora2, hora3,
                hora4, hora5, hora6,
                hora7, hora8, hora9
        };

        for (LinearLayout h : horas) {
            h.setSelected(false);
        }

        seleccionada.setSelected(true);
        horaSeleccionada = obtenerHora(seleccionada);

        btnConfirmar.setVisibility(View.VISIBLE);
    }

    private void seleccionar(int d) {

        doc1.setBackgroundResource(R.drawable.bg_card_normal);
        doc2.setBackgroundResource(R.drawable.bg_card_normal);
        doc3.setBackgroundResource(R.drawable.bg_card_normal);
        doc4.setBackgroundResource(R.drawable.bg_card_normal);

        check1.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);
        check3.setVisibility(View.GONE);
        check4.setVisibility(View.GONE);

        if (d == 1) { doc1.setBackgroundResource(R.drawable.bg_card_selected); check1.setVisibility(View.VISIBLE); }
        else if (d == 2) { doc2.setBackgroundResource(R.drawable.bg_card_selected); check2.setVisibility(View.VISIBLE); }
        else if (d == 3) { doc3.setBackgroundResource(R.drawable.bg_card_selected); check3.setVisibility(View.VISIBLE); }
        else if (d == 4) { doc4.setBackgroundResource(R.drawable.bg_card_selected); check4.setVisibility(View.VISIBLE); }

        layoutFecha.setVisibility(View.VISIBLE);

        etFecha.setText("");
        layoutHorarios.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE);

        resetHoras();
    }

    private void resetHoras() {
        LinearLayout[] horas = {
                hora1, hora2, hora3,
                hora4, hora5, hora6,
                hora7, hora8, hora9
        };

        for (LinearLayout h : horas) {
            h.setSelected(false);
            h.setEnabled(true);
            h.setAlpha(1f);
        }

        horaSeleccionada = "";
    }

    private String generarConsultorio(String especialidad) {

        int piso = 1;

        switch (especialidad) {
            case "Cardiología": piso = 1; break;
            case "Medicina General": piso = 2; break;
            case "Pediatría": piso = 3; break;
            case "Dermatología": piso = 4; break;
        }

        int numero = (int) (Math.random() * 10) + 1;

        return "Piso " + piso + " - Consultorio " + piso + "0" + numero;
    }
}