package com.codelabs.citaya;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class RegistroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        //  VOLVER
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish(); // regresa al login
        });

        //   CREAR CUENTA
        findViewById(R.id.btnCrearCuenta).setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // evita volver al registro
        });
    }
}