package com.codelabs.citaya;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etCorreo, etPassword;
    Button btnLogin;
    MaterialButton btnCrearCuenta;

    // 🔵 NUEVO: Redes sociales
    MaterialButton btnFacebook, btnInstagram, btnTikTok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // LOGIN CAMPOS
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        // 🔵 REDES SOCIALES
        btnFacebook = findViewById(R.id.btnFacebook);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnTikTok = findViewById(R.id.btnTikTok);

        // EFECTO HOVER (crear cuenta)
        btnCrearCuenta.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                btnCrearCuenta.setBackgroundColor(Color.parseColor("#E3F1FD"));
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                btnCrearCuenta.setBackgroundColor(Color.WHITE);
            }
            return false;
        });

        // IR A REGISTRO
        btnCrearCuenta.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class))
        );

        // LOGIN NORMAL
        btnLogin.setOnClickListener(v -> login());

        // 🔵 FACEBOOK (SDK luego)
        btnFacebook.setOnClickListener(v ->
                Snackbar.make(findViewById(android.R.id.content),
                        "Login con Facebook (en desarrollo)",
                        Snackbar.LENGTH_SHORT).show()
        );

        // 📸 INSTAGRAM (WEB OAuth)
        btnInstagram.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content),
                    "Login con Instagram",
                    Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/accounts/login/"));
            startActivity(intent);
        });

        // 🎵 TIKTOK (WEB OAuth)
        btnTikTok.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content),
                    "Login con TikTok",
                    Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.tiktok.com/login"));
            startActivity(intent);
        });
    }

    // 🔐 LOGIN ORIGINAL (NO MODIFICADO)
    private void login() {
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        // VALIDACIÓN
        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Completa los campos",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        // LOGIN
        if (correo.equals("robinraymundos@gmail.com") && pass.equals("123456")) {

            Snackbar.make(findViewById(android.R.id.content),
                    "Bienvenido",
                    Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "Credenciales incorrectas",
                    Snackbar.LENGTH_SHORT).show();
        }
    }
}