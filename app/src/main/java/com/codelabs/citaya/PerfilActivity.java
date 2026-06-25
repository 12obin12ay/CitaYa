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

        TextView txtNombreHeader = findViewById(R.id.txtNombreHeader);
        TextView tvNombreValor = findViewById(R.id.tvNombreCompletoValor);
        TextView tvCorreoValor = findViewById(R.id.tvCorreoValor);
        TextView tvTelefonoValor = findViewById(R.id.tvTelefonoValor);
        TextView tvDniValor = findViewById(R.id.tvDniValor);
        ImageView fotoPerfil = findViewById(R.id.fotoPerfil);

        String correoActual = MainActivity.obtenerCorreoActual(this);
        String key = LoginActivity.claveUsuario(correoActual);

        SharedPreferences usuariosPrefs =
                getSharedPreferences(LoginActivity.PREF_USUARIOS, MODE_PRIVATE);

        String nombre = usuariosPrefs.getString(key + "_nombre", "Nombre del Paciente");
        String correo = usuariosPrefs.getString(key + "_correo", correoActual);
        String telefono = usuariosPrefs.getString(key + "_telefono", "Sin teléfono");
        String dni = usuariosPrefs.getString(key + "_dni", "Sin DNI");
        String sexo = usuariosPrefs.getString(key + "_sexo", "Masculino");

        txtNombreHeader.setText(nombre);
        tvNombreValor.setText(nombre);
        tvCorreoValor.setText(correo);
        tvDniValor.setText(dni);

        if (telefono.length() == 9 && telefono.matches("\\d{9}")) {
            String telFormateado = telefono.substring(0, 3) + " " +
                    telefono.substring(3, 6) + " " +
                    telefono.substring(6);
            tvTelefonoValor.setText(telFormateado);
        } else {
            tvTelefonoValor.setText(telefono);
        }

        if ("Femenino".equals(sexo)) {
            fotoPerfil.setImageResource(R.drawable.perfil_femenino);
        } else {
            fotoPerfil.setImageResource(R.drawable.perfil_masculino);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            getSharedPreferences(LoginActivity.PREF_SESION, MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply();

            Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }
}