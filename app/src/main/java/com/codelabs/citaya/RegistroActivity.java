package com.codelabs.citaya;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;


public class RegistroActivity extends AppCompatActivity {

    private UsuarioDAO usuarioDAO;
    private EditText etNombre, etDni, etTelefono, etCorreo, etPassword, etConfirmPassword;
    private RadioGroup rgSexo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        usuarioDAO = new UsuarioDAO(this);

        etNombre = findViewById(R.id.etNombre);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        rgSexo = findViewById(R.id.rgSexo);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        MaterialButton btnCrear = findViewById(R.id.btnCrearCuenta);

        btnCrear.setOnClickListener(v -> {
            if (validarCampos()) {

                String nombre = etNombre.getText().toString().trim();
                String dni = etDni.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim().toLowerCase();
                String password = etPassword.getText().toString().trim();

                String sexo;

                int selectedId = rgSexo.getCheckedRadioButtonId();

                if (selectedId == R.id.rbMasculino) {
                    sexo = "Masculino";
                } else if (selectedId == R.id.rbFemenino) {
                    sexo = "Femenino";
                } else {
                    Toast.makeText(this, "Selecciona tu sexo", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (usuarioDAO.existeCorreo(correo)) {
                    etCorreo.setError("Este correo ya está registrado");
                    etCorreo.requestFocus();
                    return;
                }

                Usuario usuario = new Usuario(
                        nombre,
                        dni,
                        telefono,
                        correo,
                        password,
                        sexo
                );

                boolean registrado = usuarioDAO.registrarUsuario(usuario);

                if (!registrado) {
                    Toast.makeText(this,
                            "No se pudo registrar usuario",
                            Toast.LENGTH_SHORT).show();

                    return;
                }

                String key = LoginActivity.claveUsuario(correo);

                getSharedPreferences(LoginActivity.PREF_SESION, MODE_PRIVATE)
                        .edit()
                        .putBoolean("sesion_activa", true)
                        .putString("correo_actual", correo)
                        .putString("usuario_key", key)
                        .apply();

                limpiarDatosNuevaCuenta(correo);

                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                intent.putExtra("NOMBRE_USUARIO", nombre);
                startActivity(intent);
                finish();
            }
        });
    }

    private void limpiarDatosNuevaCuenta(String correo) {
        getSharedPreferences(LoginActivity.prefsCitas(correo), MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences(LoginActivity.prefsNotificaciones(correo), MODE_PRIVATE).edit().clear().apply();
        getSharedPreferences(LoginActivity.prefsNotificacionesLeidas(correo), MODE_PRIVATE).edit().clear().apply();
    }

    private boolean validarCampos() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Ingresa tu nombre completo");
            etNombre.requestFocus();
            return false;
        }

        if (contieneNumero(nombre)) {
            etNombre.setError("El nombre no debe contener números");
            etNombre.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(dni)) {
            etDni.setError("Ingresa tu DNI");
            etDni.requestFocus();
            return false;
        }

        if (!dni.matches("\\d{8}")) {
            etDni.setError("El DNI debe tener 8 dígitos numéricos");
            etDni.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(telefono)) {
            etTelefono.setError("Ingresa tu teléfono");
            etTelefono.requestFocus();
            return false;
        }

        if (!telefono.matches("9\\d{8}")) {
            etTelefono.setError("El teléfono debe tener 9 dígitos y empezar con 9");
            etTelefono.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(correo)) {
            etCorreo.setError("Ingresa tu correo electrónico");
            etCorreo.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            etCorreo.setError("Correo electrónico no válido");
            etCorreo.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Ingresa una contraseña");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("La contraseña debe tener al menos 6 caracteres");
            etPassword.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Confirma tu contraseña");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (rgSexo.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Selecciona tu sexo", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean contieneNumero(String texto) {
        for (char c : texto.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }
}