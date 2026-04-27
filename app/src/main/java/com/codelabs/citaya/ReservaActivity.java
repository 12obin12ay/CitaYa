package com.codelabs.citaya;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class ReservaActivity extends AppCompatActivity {

    LinearLayout doc1, doc2, doc3, doc4;
    ImageView check1, check2, check3, check4;
    LinearLayout layoutFecha;
    EditText etFecha;

    LinearLayout layoutHorarios;
    TextView hora1, hora2, hora3, hora4, hora5, hora6,hora7,hora8,hora9;
    MaterialButton btnConfirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_reserva);

        // BOTÓN VOLVER
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // DOCTORES
        doc1 = findViewById(R.id.doc1);
        doc2 = findViewById(R.id.doc2);
        doc3 = findViewById(R.id.doc3);
        doc4 = findViewById(R.id.doc4);

        // CHECKS
        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);

        // FECHA
        layoutFecha = findViewById(R.id.layoutFecha);
        etFecha = findViewById(R.id.etFecha);

        // HORARIOS
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

        // CLICK DOCTORES
        doc1.setOnClickListener(v -> seleccionar(1));
        doc2.setOnClickListener(v -> seleccionar(2));
        doc3.setOnClickListener(v -> seleccionar(3));
        doc4.setOnClickListener(v -> seleccionar(4));

        // CALENDARIO
        etFecha.setOnClickListener(v -> mostrarCalendario());

        // CLICK HORAS
        View[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};

        for (View h : horas) {
            h.setOnClickListener(v -> seleccionarHora((TextView) v));
        }
    }

    private void seleccionar(int d) {

        // RESET
        doc1.setBackgroundResource(R.drawable.bg_card_normal);
        doc2.setBackgroundResource(R.drawable.bg_card_normal);
        doc3.setBackgroundResource(R.drawable.bg_card_normal);
        doc4.setBackgroundResource(R.drawable.bg_card_normal);

        check1.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);
        check3.setVisibility(View.GONE);
        check4.setVisibility(View.GONE);

        // SELECCIÓN
        if (d == 1) {
            doc1.setBackgroundResource(R.drawable.bg_card_selected);
            check1.setVisibility(View.VISIBLE);
        } else if (d == 2) {
            doc2.setBackgroundResource(R.drawable.bg_card_selected);
            check2.setVisibility(View.VISIBLE);
        } else if (d == 3) {
            doc3.setBackgroundResource(R.drawable.bg_card_selected);
            check3.setVisibility(View.VISIBLE);
        } else if (d == 4) {
            doc4.setBackgroundResource(R.drawable.bg_card_selected);
            check4.setVisibility(View.VISIBLE);
        }

        // MOSTRAR FECHA
        layoutFecha.setVisibility(View.VISIBLE);

        // LIMPIAR
        etFecha.setText("");
        layoutHorarios.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE);
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();

        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(this,
                (view, y, m, d) -> {
                    etFecha.setText(d + "/" + (m + 1) + "/" + y);

                    // 👇 MOSTRAR HORARIOS
                    layoutHorarios.setVisibility(View.VISIBLE);
                },
                year, month, day);

        dp.show();
    }

    private void seleccionarHora(TextView seleccionada) {

        TextView[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7,hora8,hora9};

        // RESET
        for (TextView h : horas) {
            h.setBackgroundResource(R.drawable.bg_hora_normal);
            h.setTextColor(getResources().getColor(android.R.color.black));
        }

        // SELECCIONADA
        seleccionada.setBackgroundResource(R.drawable.bg_hora_selected);
        seleccionada.setTextColor(getResources().getColor(android.R.color.white));

        // MOSTRAR BOTÓN
        btnConfirmar.setVisibility(View.VISIBLE);
    }
}