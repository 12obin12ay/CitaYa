package com.codelabs.citaya;
import android.content.Intent;
import android.os.Bundle;
import androidx.core.view.WindowCompat;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_perfil);
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {

            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);

            //  evita volver atrás
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            startActivity(intent);
        });
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }}
