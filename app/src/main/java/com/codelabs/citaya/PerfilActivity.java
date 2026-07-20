package com.codelabs.citaya;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;

import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

public class PerfilActivity extends AppCompatActivity {

    private TextView txtNombreHeader;
    private TextView tvNombreValor;
    private TextView tvCorreoValor;
    private TextView tvTelefonoValor;
    private TextView tvDniValor;

    private TextView tvEstadoPerfil;
    private TextView tvPorcentajePerfil;
    private ProgressBar progresoPerfil;

    private ImageView fotoPerfil;

    private UsuarioDAO usuarioDAO;
    private Usuario usuarioActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        usuarioDAO = new UsuarioDAO(this);

        vincularVistas();
        cargarDatos();

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        findViewById(R.id.btnEditarPerfil).setOnClickListener(v -> showEditDialog());

        findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> cerrarSesion());
    }

    private void vincularVistas() {

        txtNombreHeader = findViewById(R.id.txtNombreHeader);

        tvNombreValor = findViewById(R.id.tvNombreCompletoValor);
        tvCorreoValor = findViewById(R.id.tvCorreoValor);
        tvTelefonoValor = findViewById(R.id.tvTelefonoValor);
        tvDniValor = findViewById(R.id.tvDniValor);

        tvEstadoPerfil = findViewById(R.id.tvEstadoPerfil);
        tvPorcentajePerfil = findViewById(R.id.tvPorcentajePerfil);
        progresoPerfil = findViewById(R.id.progresoPerfil);

        fotoPerfil = findViewById(R.id.fotoPerfil);
    }

    private void cargarDatos() {

        String correoActual = MainActivity.obtenerCorreoActual(this);
        usuarioActual = usuarioDAO.buscarPorCorreo(correoActual);

        if (usuarioActual == null)
            return;

        txtNombreHeader.setText(usuarioActual.getNombre());
        tvNombreValor.setText(usuarioActual.getNombre());
        tvCorreoValor.setText(usuarioActual.getCorreo());
        tvDniValor.setText(usuarioActual.getDni());

        String telefono = usuarioActual.getTelefono();

        if (telefono != null && telefono.length() == 9) {
            tvTelefonoValor.setText(
                    telefono.substring(0,3) + " " +
                            telefono.substring(3,6) + " " +
                            telefono.substring(6)
            );
        } else {
            tvTelefonoValor.setText(telefono);
        }

        if ("Femenino".equals(usuarioActual.getSexo())) {
            fotoPerfil.setImageResource(R.drawable.perfil_femenino);
        } else {
            fotoPerfil.setImageResource(R.drawable.perfil_masculino);
        }

        actualizarEstadoPerfil();
    }

    /**
     * Actualiza el porcentaje del perfil
     */
    private void actualizarEstadoPerfil() {

        boolean telefonoCompleto =
                usuarioActual.getTelefono() != null &&
                        usuarioActual.getTelefono().trim().length() == 9;

        boolean dniCompleto =
                usuarioActual.getDni() != null &&
                        usuarioActual.getDni().trim().length() == 8;

        if (telefonoCompleto && dniCompleto) {

            progresoPerfil.setProgress(100);

            tvPorcentajePerfil.setText("100%");

            tvEstadoPerfil.setText("✓ Perfil verificado");

        } else {

            progresoPerfil.setProgress(50);

            tvPorcentajePerfil.setText("50%");

            tvEstadoPerfil.setText("Perfil parcialmente verificado");
        }
    }

    private void showEditDialog() {

        BottomSheetDialog dialog = new BottomSheetDialog(this);

        View view = getLayoutInflater().inflate(
                R.layout.dialog_editar_perfil,
                null
        );

        EditText etNombre = view.findViewById(R.id.etEditNombre);
        EditText etTelefono = view.findViewById(R.id.etEditTelefono);
        EditText etDni = view.findViewById(R.id.etEditDni);

        MaterialButton btnGuardar = view.findViewById(R.id.btnGuardarCambios);

        etNombre.setText(usuarioActual.getNombre());
        etTelefono.setText(usuarioActual.getTelefono());
        etDni.setText(usuarioActual.getDni());

        btnGuardar.setOnClickListener(v -> {

            String nuevoNombre = etNombre.getText().toString().trim();
            String nuevoTelefono = etTelefono.getText().toString().trim();
            String nuevoDni = etDni.getText().toString().trim();

            if (nuevoNombre.isEmpty()
                    || nuevoTelefono.length() != 9
                    || nuevoDni.length() != 8) {

                Toast.makeText(
                        this,
                        "Por favor, completa los campos correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            usuarioActual.setNombre(nuevoNombre);
            usuarioActual.setTelefono(nuevoTelefono);
            usuarioActual.setDni(nuevoDni);

            if (usuarioDAO.actualizarUsuario(usuarioActual)) {

                Toast.makeText(
                        this,
                        "Perfil actualizado correctamente",
                        Toast.LENGTH_SHORT
                ).show();

                cargarDatos();

                dialog.dismiss();

            } else {

                Toast.makeText(
                        this,
                        "Error al actualizar",
                        Toast.LENGTH_SHORT
                ).show();
            }

        });

        dialog.setContentView(view);
        dialog.show();
    }

    private void cerrarSesion() {

        getSharedPreferences(LoginActivity.PREF_SESION, MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(
                PerfilActivity.this,
                LoginActivity.class
        );

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}