package com.codelabs.citaya;
import com.google.android.material.snackbar.Snackbar;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etCorreo, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        findViewById(R.id.btnCrearCuenta).setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
        });

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String correo = etCorreo.getText().toString().trim();
        String pass = etPassword.getText().toString().trim();

        //  VALIDACIÓN
        if (TextUtils.isEmpty(correo) || TextUtils.isEmpty(pass)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Completa los campos",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        //  LOGIN
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
    }}