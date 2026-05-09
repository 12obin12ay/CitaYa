package com.codelabs.citaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Vincular vistas
        TextView txtNombreHeader = findViewById(R.id.txtNombreHeader);
        TextView tvNombreValor = findViewById(R.id.tvNombreCompletoValor);
        TextView tvCorreoValor = findViewById(R.id.tvCorreoValor);
        TextView tvTelefonoValor = findViewById(R.id.tvTelefonoValor);
        TextView tvDniValor = findViewById(R.id.tvDniValor);
        ImageView fotoPerfil = findViewById(R.id.fotoPerfil); // ← ahora funciona

        // Leer SharedPreferences
        SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
        String nombre = prefs.getString("nombre_completo", "Lucas Andrade");
        String correo = prefs.getString("correo", "lucas@example.com");
        String telefono = prefs.getString("telefono", "987654321");
        String dni = prefs.getString("dni", "12345678");
        String sexo = prefs.getString("sexo", "Masculino");

        // Mostrar textos
        txtNombreHeader.setText(nombre);
        tvNombreValor.setText(nombre);
        tvCorreoValor.setText(correo);
        tvDniValor.setText(dni);

        // Formatear teléfono
        if (telefono.length() == 9 && telefono.matches("\\d{9}")) {
            String telFormateado = telefono.substring(0, 3) + " " +
                    telefono.substring(3, 6) + " " +
                    telefono.substring(6);
            tvTelefonoValor.setText(telFormateado);
        } else {
            tvTelefonoValor.setText(telefono);
        }

        // Cambiar foto según sexo
        if ("Femenino".equals(sexo)) {
            fotoPerfil.setImageResource(R.drawable.perfil_femenino);
        } else {
            fotoPerfil.setImageResource(R.drawable.perfil_masculino);
        }

        // Botones
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}