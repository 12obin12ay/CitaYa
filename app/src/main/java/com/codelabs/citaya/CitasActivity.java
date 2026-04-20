package com.codelabs.citaya;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class CitasActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_citas);

        // Botón volver personalizado
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}