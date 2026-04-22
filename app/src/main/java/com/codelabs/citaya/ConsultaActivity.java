package com.codelabs.citaya;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ConsultaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);

        // 🔙 Botón volver
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}