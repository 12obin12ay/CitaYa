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

import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private UsuarioDAO usuarioDAO;
    EditText etCorreo, etPassword;
    Button btnLogin;
    MaterialButton btnCrearCuenta;
    MaterialButton btnFacebook, btnInstagram, btnTikTok;

    public static final String PREF_USUARIOS = "usuarios_data";
    public static final String PREF_SESION = "sesion_usuario";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioDAO = new UsuarioDAO(this);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);

        btnFacebook = findViewById(R.id.btnFacebook);
        btnInstagram = findViewById(R.id.btnInstagram);
        btnTikTok = findViewById(R.id.btnTikTok);

        btnCrearCuenta.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                btnCrearCuenta.setBackgroundColor(Color.parseColor("#E3F2FD"));
            } else if (event.getAction() == MotionEvent.ACTION_UP ||
                    event.getAction() == MotionEvent.ACTION_CANCEL) {
                btnCrearCuenta.setBackgroundColor(Color.WHITE);
            }
            return false;
        });

        btnCrearCuenta.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class))
        );

        btnLogin.setOnClickListener(v -> login());

        btnFacebook.setOnClickListener(v ->
                Snackbar.make(findViewById(android.R.id.content),
                        "Login con Facebook (en desarrollo)",
                        Snackbar.LENGTH_SHORT).show()
        );

        btnInstagram.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content),
                    "Login con Instagram",
                    Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/accounts/login/"));
            startActivity(intent);
        });

        btnTikTok.setOnClickListener(v -> {
            Snackbar.make(findViewById(android.R.id.content),
                    "Login con TikTok",
                    Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.tiktok.com/login"));
            startActivity(intent);
        });
    }

    private void login() {
        String correo = etCorreo.getText().toString().trim().toLowerCase();
        String pass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Completa los campos",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        Usuario usuario = usuarioDAO.login(correo, pass);

        if (usuario != null) {

            String nombreGuardado = usuario.getNombre();

            String key = claveUsuario(correo);

            getSharedPreferences(PREF_SESION, MODE_PRIVATE)
                    .edit()
                    .putBoolean("sesion_activa", true)
                    .putString("correo_actual", correo)
                    .putString("usuario_key", key)
                    .apply();

            Snackbar.make(findViewById(android.R.id.content),
                    "Bienvenido",
                    Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("NOMBRE_USUARIO", nombreGuardado);
            startActivity(intent);
            finish();

        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    "Correo o contraseña incorrectos",
                    Snackbar.LENGTH_SHORT).show();
        }
    }

    public static String claveUsuario(String correo) {
        return correo.toLowerCase()
                .replace("@", "_")
                .replace(".", "_")
                .replace("-", "_")
                .replace("+", "_");
    }

    public static String prefsCitas(String correo) {
        return "citas_" + claveUsuario(correo);
    }

    public static String prefsNotificaciones(String correo) {
        return "notificaciones_" + claveUsuario(correo);
    }

    public static String prefsNotificacionesLeidas(String correo) {
        return "notificaciones_leidas_" + claveUsuario(correo);
    }
}