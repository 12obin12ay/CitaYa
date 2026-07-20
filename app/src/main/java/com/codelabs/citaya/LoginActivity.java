package com.codelabs.citaya;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private UsuarioDAO usuarioDAO;
    EditText etCorreo, etPassword;
    Button btnLogin;
    MaterialButton btnCrearCuenta;
    MaterialButton btnGoogle;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    public static final String PREF_USUARIOS = "usuarios_data";
    public static final String PREF_SESION = "sesion_usuario";
    public static final String PREF_HORARIOS = "horarios_globales";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usuarioDAO = new UsuarioDAO(this);
        mAuth = FirebaseAuth.getInstance();

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        btnGoogle = findViewById(R.id.btnGoogle);

        // ---- Configuración de Google Sign-In ----
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task =
                            GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    handleSignInResult(task);
                }
        );

        btnGoogle.setOnClickListener(v -> signInWithGoogle());

        // ---- Botones existentes ----
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
    }

    // =========================================================
    //                  GOOGLE SIGN-IN
    // =========================================================

    private void signInWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken(), account.getEmail(), account.getDisplayName());
        } catch (ApiException e) {
            Toast.makeText(this, "Error al iniciar sesión: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(String idToken, String correoGoogle, String nombreGoogle) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {

                        String correo = correoGoogle != null ? correoGoogle.trim().toLowerCase() : "";
                        String nombre = nombreGoogle != null ? nombreGoogle : "Usuario";

                        Usuario usuarioExistente = usuarioDAO.buscarPorCorreo(correo);

                        if (usuarioExistente == null) {
                            Usuario nuevo = new Usuario(
                                    nombre,   // nombre
                                    "",       // dni (pendiente de completar)
                                    "",       // telefono (pendiente de completar)
                                    correo,   // correo
                                    "",       // password (no aplica, login con Google)
                                    ""        // sexo (pendiente de completar)
                            );
                            usuarioDAO.registrarUsuario(nuevo);
                        }

                        String key = claveUsuario(correo);

                        getSharedPreferences(PREF_SESION, MODE_PRIVATE)
                                .edit()
                                .putBoolean("sesion_activa", true)
                                .putString("correo_actual", correo)
                                .putString("usuario_key", key)
                                .apply();

                        Snackbar.make(findViewById(android.R.id.content),
                                "Bienvenido " + nombre, Snackbar.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("NOMBRE_USUARIO", nombre);
                        startActivity(intent);
                        finish();

                    } else {
                        Toast.makeText(this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // =========================================================
    //                  LOGIN NORMAL (correo/password)
    // =========================================================

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