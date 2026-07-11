package com.codelabs.citaya;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;

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

        UsuarioDAO usuarioDAO = new UsuarioDAO(this);

        Usuario usuario = usuarioDAO.buscarPorCorreo(correoActual);

        String nombre = "Nombre del Paciente";
        String correo = correoActual;
        String telefono = "Sin teléfono";
        String dni = "Sin DNI";
        String sexo = "Masculino";

        if (usuario != null) {
            nombre = usuario.getNombre();
            correo = usuario.getCorreo();
            telefono = usuario.getTelefono();
            dni = usuario.getDni();
            sexo = usuario.getSexo();
        }

        txtNombreHeader.setText(nombre);
        tvNombreValor.setText(nombre);
        tvCorreoValor.setText(correo);
        tvDniValor.setText(dni);

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {

            // 1. Cierra sesión en Firebase
            FirebaseAuth.getInstance().signOut();

            // 2. Cierra sesión en Google (para que la próxima vez pida elegir cuenta)
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

            googleSignInClient.signOut().addOnCompleteListener(this, task -> {

                // 3. Borra tu sesión local
                getSharedPreferences(LoginActivity.PREF_SESION, MODE_PRIVATE)
                        .edit()
                        .clear()
                        .apply();

                // 4. Regresa al login
                Intent intent = new Intent(PerfilActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });

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