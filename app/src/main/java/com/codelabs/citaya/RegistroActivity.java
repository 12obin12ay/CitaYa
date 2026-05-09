package com.codelabs.citaya;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etDni, etTelefono, etCorreo, etPassword, etConfirmPassword;
    private RadioGroup rgSexo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // Referencias a TODOS los campos
        etNombre = findViewById(R.id.etNombre);
        etDni = findViewById(R.id.etDni);
        etTelefono = findViewById(R.id.etTelefono);
        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);  // ← NUEVO
        rgSexo = findViewById(R.id.rgSexo);                        // ← NUEVO

        // Botón Volver
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        // Botón Crear Cuenta
        MaterialButton btnCrear = findViewById(R.id.btnCrearCuenta);
        btnCrear.setOnClickListener(v -> {
            if (validarCampos()) {
                // Obtener valores
                String nombre = etNombre.getText().toString().trim();
                String dni = etDni.getText().toString().trim();
                String telefono = etTelefono.getText().toString().trim();
                String correo = etCorreo.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                // Obtener sexo seleccionado
                String sexo = "";
                int selectedId = rgSexo.getCheckedRadioButtonId();
                if (selectedId == R.id.rbMasculino) {
                    sexo = "Masculino";
                } else if (selectedId == R.id.rbFemenino) {
                    sexo = "Femenino";
                } else {
                    Toast.makeText(this, "Selecciona tu sexo", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Guardar TODOS los datos en SharedPreferences
                SharedPreferences prefs = getSharedPreferences("user_data", MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putString("nombre_completo", nombre);
                editor.putString("dni", dni);
                editor.putString("telefono", telefono);
                editor.putString("correo", correo);
                editor.putString("sexo", sexo);   // ← GUARDAR SEXO
                editor.apply();

                // Navegar a MainActivity enviando el nombre
                Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
                intent.putExtra("NOMBRE_USUARIO", nombre);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean validarCampos() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String correo = etCorreo.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();  // ← NUEVO

        // 1. Validar Nombre completo (sin números)
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

        // 2. Validar DNI: exactamente 8 dígitos
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

        // 3. Validar Teléfono: 9 dígitos y que empiece con 9
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

        // 4. Validar Correo electrónico
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

        // 5. Validar Contraseña (mínimo 6 caracteres)
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

        // 6. Validar Confirmación de contraseña
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

        // 7. Validar que se haya seleccionado sexo
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