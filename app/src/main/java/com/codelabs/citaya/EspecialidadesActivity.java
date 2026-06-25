package com.codelabs.citaya;

import com.google.android.material.snackbar.Snackbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

public class EspecialidadesActivity extends AppCompatActivity {

    LinearLayout layoutEspecialidades, layoutDoctores;
    LinearLayout cardCardiologia, cardMedicina, cardPediatria, cardDermatologia;
    LinearLayout cardNeumologia, cardGastro, cardNeurologia, cardTraumatologia;
    LinearLayout doc1, doc2, doc3, doc4;
    ImageView check1, check2, check3, check4;
    LinearLayout layoutFecha, layoutHorarios;
    EditText etFecha;
    LinearLayout hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9;
    MaterialButton btnConfirmar;

    private String horaSeleccionada = "";
    private String doctorSeleccionado = "";
    private String especialidadSeleccionada = "";

    private HashMap<String, Doctor[]> doctoresPorEspecialidad = new HashMap<>();
    private Doctor[] doctoresActuales = new Doctor[3];

    static class Doctor {
        String nombre, rating, experiencia;
        int imagen;

        Doctor(String nombre, String rating, String experiencia, int imagen) {
            this.nombre = nombre;
            this.rating = rating;
            this.experiencia = experiencia;
            this.imagen = imagen;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_especialidades);

        inicializarDoctores();
        vincularVistas();
        configurarEventos();

        String especialidadExtra = getIntent().getStringExtra("especialidad");
        if (especialidadExtra != null && doctoresPorEspecialidad.containsKey(especialidadExtra)) {
            abrirDoctores(especialidadExtra);
        }
    }

    private void vincularVistas() {
        layoutEspecialidades = findViewById(R.id.layoutEspecialidades);
        layoutDoctores = findViewById(R.id.layoutDoctores);

        cardCardiologia = findViewById(R.id.cardCardiologia);
        cardMedicina = findViewById(R.id.cardMedicina);
        cardPediatria = findViewById(R.id.cardPediatria);
        cardDermatologia = findViewById(R.id.cardDermatologia);

        cardNeumologia = findViewById(R.id.cardNeumologia);
        cardGastro = findViewById(R.id.cardGastro);
        cardNeurologia = findViewById(R.id.cardNeurologia);
        cardTraumatologia = findViewById(R.id.cardTraumatologia);

        doc1 = findViewById(R.id.doc1);
        doc2 = findViewById(R.id.doc2);
        doc3 = findViewById(R.id.doc3);
        doc4 = findViewById(R.id.doc4);

        check1 = findViewById(R.id.check1);
        check2 = findViewById(R.id.check2);
        check3 = findViewById(R.id.check3);
        check4 = findViewById(R.id.check4);

        layoutFecha = findViewById(R.id.layoutFecha);
        etFecha = findViewById(R.id.etFecha);
        layoutHorarios = findViewById(R.id.layoutHorarios);

        hora1 = findViewById(R.id.hora1);
        hora2 = findViewById(R.id.hora2);
        hora3 = findViewById(R.id.hora3);
        hora4 = findViewById(R.id.hora4);
        hora5 = findViewById(R.id.hora5);
        hora6 = findViewById(R.id.hora6);
        hora7 = findViewById(R.id.hora7);
        hora8 = findViewById(R.id.hora8);
        hora9 = findViewById(R.id.hora9);

        btnConfirmar = findViewById(R.id.btnConfirmar);

        if (doc4 != null) doc4.setVisibility(View.GONE);
    }

    private void configurarEventos() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (layoutFecha.getVisibility() == View.VISIBLE || layoutHorarios.getVisibility() == View.VISIBLE) {
                resetSeleccionDoctor();
            } else if (layoutDoctores.getVisibility() == View.VISIBLE) {
                layoutDoctores.setVisibility(View.GONE);
                layoutEspecialidades.setVisibility(View.VISIBLE);
            } else {
                finish();
            }
        });

        cardCardiologia.setOnClickListener(v -> abrirDoctores("Cardiología"));
        cardMedicina.setOnClickListener(v -> abrirDoctores("Medicina General"));
        cardPediatria.setOnClickListener(v -> abrirDoctores("Pediatría"));
        cardDermatologia.setOnClickListener(v -> abrirDoctores("Dermatología"));

        if (cardNeumologia != null) cardNeumologia.setOnClickListener(v -> abrirDoctores("Neumología"));
        if (cardGastro != null) cardGastro.setOnClickListener(v -> abrirDoctores("Gastroenterología"));
        if (cardNeurologia != null) cardNeurologia.setOnClickListener(v -> abrirDoctores("Neurología"));
        if (cardTraumatologia != null) cardTraumatologia.setOnClickListener(v -> abrirDoctores("Traumatología"));

        doc1.setOnClickListener(v -> seleccionarDoctor(1));
        doc2.setOnClickListener(v -> seleccionarDoctor(2));
        doc3.setOnClickListener(v -> seleccionarDoctor(3));

        etFecha.setOnClickListener(v -> mostrarCalendario());

        LinearLayout[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};

        for (LinearLayout h : horas) {
            h.setOnClickListener(v -> seleccionarHora((LinearLayout) v));
        }

        btnConfirmar.setOnClickListener(v -> confirmarCita());
    }

    private void inicializarDoctores() {
        doctoresPorEspecialidad.put("Cardiología", new Doctor[]{
                new Doctor("Dr. Miguel Salazar", "4.9", "18 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Valeria Rojas", "4.8", "14 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Andrés Cárdenas", "5.0", "20 años de experiencia", R.drawable.doctor)
        });

        doctoresPorEspecialidad.put("Medicina General", new Doctor[]{
                new Doctor("Dra. Camila Torres", "4.9", "12 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Luis Mendoza", "4.8", "15 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. María González", "4.7", "11 años de experiencia", R.drawable.doctora)
        });

        doctoresPorEspecialidad.put("Pediatría", new Doctor[]{
                new Doctor("Dr. Diego Navarro", "5.0", "17 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Sofía Herrera", "4.9", "13 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Sebastián Paredes", "4.8", "10 años de experiencia", R.drawable.doctor)
        });

        doctoresPorEspecialidad.put("Dermatología", new Doctor[]{
                new Doctor("Dra. Ana Fernández", "4.9", "16 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Javier Campos", "4.8", "12 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Lucía Vega", "4.7", "9 años de experiencia", R.drawable.doctora)
        });

        doctoresPorEspecialidad.put("Neumología", new Doctor[]{
                new Doctor("Dr. Ricardo Fuentes", "4.9", "19 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Daniela Castro", "4.8", "11 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Martín Lozano", "4.7", "14 años de experiencia", R.drawable.doctor)
        });

        doctoresPorEspecialidad.put("Gastroenterología", new Doctor[]{
                new Doctor("Dra. Carolina Medina", "5.0", "18 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Fernando Cabrera", "4.8", "13 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Gabriela León", "4.9", "15 años de experiencia", R.drawable.doctora)
        });

        doctoresPorEspecialidad.put("Neurología", new Doctor[]{
                new Doctor("Dr. Alejandro Rivera", "5.0", "21 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Natalia Ortiz", "4.9", "16 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. José Valdivia", "4.8", "12 años de experiencia", R.drawable.doctor)
        });

        doctoresPorEspecialidad.put("Traumatología", new Doctor[]{
                new Doctor("Dra. Paola Morales", "4.9", "14 años de experiencia", R.drawable.doctora),
                new Doctor("Dr. Cristian Ramírez", "5.0", "19 años de experiencia", R.drawable.doctor),
                new Doctor("Dra. Andrea Silva", "4.8", "10 años de experiencia", R.drawable.doctora)
        });
    }

    private SharedPreferences prefsCitasUsuario() {
        String correo = MainActivity.obtenerCorreoActual(this);
        return getSharedPreferences(LoginActivity.prefsCitas(correo), MODE_PRIVATE);
    }

    private void abrirDoctores(String especialidad) {
        especialidadSeleccionada = especialidad;
        doctoresActuales = doctoresPorEspecialidad.get(especialidad);

        cargarDoctorEnCard(doc1, check1, doctoresActuales[0]);
        cargarDoctorEnCard(doc2, check2, doctoresActuales[1]);
        cargarDoctorEnCard(doc3, check3, doctoresActuales[2]);

        doc1.setVisibility(View.VISIBLE);
        doc2.setVisibility(View.VISIBLE);
        doc3.setVisibility(View.VISIBLE);
        if (doc4 != null) doc4.setVisibility(View.GONE);

        layoutEspecialidades.setVisibility(View.GONE);
        layoutDoctores.setVisibility(View.VISIBLE);

        resetSeleccionDoctorVisual();
    }

    private void cargarDoctorEnCard(LinearLayout card, ImageView check, Doctor doctor) {
        ImageView foto = (ImageView) card.getChildAt(0);
        LinearLayout contenedorTextos = (LinearLayout) card.getChildAt(1);
        TextView nombre = (TextView) contenedorTextos.getChildAt(0);
        LinearLayout filaRating = (LinearLayout) contenedorTextos.getChildAt(1);
        TextView detalle = (TextView) filaRating.getChildAt(1);

        foto.setImageResource(doctor.imagen);
        nombre.setText(doctor.nombre);
        detalle.setText(" " + doctor.rating + " • " + doctor.experiencia);

        card.setBackgroundResource(R.drawable.bg_card_normal);
        check.setVisibility(View.GONE);
    }

    private void seleccionarDoctor(int d) {
        resetSeleccionDoctorVisual();

        if (d == 1) {
            doc1.setBackgroundResource(R.drawable.bg_card_selected);
            check1.setVisibility(View.VISIBLE);
            doctorSeleccionado = doctoresActuales[0].nombre;
        } else if (d == 2) {
            doc2.setBackgroundResource(R.drawable.bg_card_selected);
            check2.setVisibility(View.VISIBLE);
            doctorSeleccionado = doctoresActuales[1].nombre;
        } else if (d == 3) {
            doc3.setBackgroundResource(R.drawable.bg_card_selected);
            check3.setVisibility(View.VISIBLE);
            doctorSeleccionado = doctoresActuales[2].nombre;
        }

        layoutFecha.setVisibility(View.VISIBLE);
        etFecha.setText("");
        layoutHorarios.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE);
        resetHoras();
    }

    private void resetSeleccionDoctorVisual() {
        doc1.setBackgroundResource(R.drawable.bg_card_normal);
        doc2.setBackgroundResource(R.drawable.bg_card_normal);
        doc3.setBackgroundResource(R.drawable.bg_card_normal);

        check1.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);
        check3.setVisibility(View.GONE);
    }

    private boolean horaYaPaso(String fecha, String horaTexto) {
        try {
            String horaLimpia = horaTexto.trim();

            if (horaLimpia.contains("AM") || horaLimpia.contains("PM")) {
                java.text.SimpleDateFormat formato12 =
                        new java.text.SimpleDateFormat("d/M/yyyy hh:mm a", java.util.Locale.US);

                Date fechaHora = formato12.parse(fecha + " " + horaLimpia);
                return fechaHora != null && fechaHora.before(new Date());

            } else {
                java.text.SimpleDateFormat formato24 =
                        new java.text.SimpleDateFormat("d/M/yyyy HH:mm", Locale.US);

                Date fechaHora = formato24.parse(fecha + " " + horaLimpia);
                return fechaHora != null && fechaHora.before(new Date());
            }

        } catch (Exception e) {
            return false;
        }
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(
                this,
                (view, y, m, d) -> {
                    String fechaSeleccionada = d + "/" + (m + 1) + "/" + y;
                    etFecha.setText(fechaSeleccionada);
                    layoutHorarios.setVisibility(View.VISIBLE);

                    SharedPreferences prefs = prefsCitasUsuario();
                    Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());

                    LinearLayout[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};

                    for (LinearLayout h : horas) {
                        String hora = obtenerHora(h);
                        String clave = fechaSeleccionada + "_" + hora;

                        if (ocupados.contains(clave) || horaYaPaso(fechaSeleccionada, hora)) {
                            h.setEnabled(false);
                            h.setAlpha(0.3f);
                        } else {
                            h.setEnabled(true);
                            h.setAlpha(1f);
                        }
                    }
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
        );

        dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dp.show();
    }

    private String obtenerHora(LinearLayout layout) {
        TextView txt = (TextView) layout.getChildAt(1);
        return txt.getText().toString();
    }

    private void seleccionarHora(LinearLayout seleccionada) {
        if (!seleccionada.isEnabled()) return;

        LinearLayout[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};

        for (LinearLayout h : horas) {
            h.setSelected(false);
        }

        seleccionada.setSelected(true);
        horaSeleccionada = obtenerHora(seleccionada);
        btnConfirmar.setVisibility(View.VISIBLE);
    }

    private int contarCitasActivas(Set<String> citas) {
        int contador = 0;

        for (String cita : citas) {
            String[] datos = cita.split("\\|");
            if (datos.length < 6) continue;

            String estado = datos[5];

            if ("PENDIENTE".equals(estado) || "CONFIRMADA".equals(estado)) {
                contador++;
            }
        }

        return contador;
    }

    private void confirmarCita() {
        if (doctorSeleccionado.isEmpty()) {
            Toast.makeText(this, "Selecciona un doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (etFecha.getText().toString().isEmpty()) {
            Toast.makeText(this, "Selecciona una fecha", Toast.LENGTH_SHORT).show();
            return;
        }

        if (horaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Selecciona una hora", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = etFecha.getText().toString();
        String ubicacion = generarConsultorio(especialidadSeleccionada);
        String horaFormateada = convertirHoraAMPM(horaSeleccionada);

        String cita = doctorSeleccionado + "|" +
                especialidadSeleccionada + "|" +
                fecha + "|" +
                horaFormateada + "|" +
                ubicacion + "|PENDIENTE";

        SharedPreferences prefs = prefsCitasUsuario();

        Set<String> citas = prefs.getStringSet("lista", new HashSet<>());
        Set<String> nuevaLista = new HashSet<>(citas);

        Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());
        Set<String> copiaOcupados = new HashSet<>(ocupados);

        String clave = fecha + "_" + horaSeleccionada;

        if (copiaOcupados.contains(clave)) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Este horario ya está ocupado",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (contarCitasActivas(nuevaLista) >= 3) {
            Snackbar.make(findViewById(android.R.id.content),
                    "Máximo 3 citas activas permitidas",
                    Snackbar.LENGTH_SHORT).show();
            return;
        }

        nuevaLista.add(cita);
        copiaOcupados.add(clave);

        prefs.edit()
                .putStringSet("lista", nuevaLista)
                .putStringSet("ocupados", copiaOcupados)
                .apply();

        boolean primeraReserva = !existeNotificacionTipo("clinica");

        NotificacionesActivity.guardarNotificacion(
                this,
                "Recordatorio de cita",
                "Tu cita con " + doctorSeleccionado + " es el " + fecha + " a las " + horaFormateada,
                "recordatorio"
        );

        if (primeraReserva) {
            NotificacionesActivity.guardarNotificacion(
                    this,
                    "Mensaje de la clínica",
                    "Recuerda llegar 15 minutos antes de tu cita",
                    "clinica"
            );

            NotificacionesActivity.guardarNotificacion(
                    this,
                    "Cita confirmada",
                    "Tu cita con " + doctorSeleccionado + " ha sido confirmada para el " + fecha + " a las " + horaFormateada,
                    "cita"
            );

            NotificacionesActivity.guardarNotificacion(
                    this,
                    "Próxima revisión",
                    "Después de tu atención podrás agendar tu próxima revisión médica",
                    "revision"
            );
        }

        mostrarNotificacionInstantanea(
                doctorSeleccionado,
                especialidadSeleccionada,
                fecha,
                horaFormateada,
                ubicacion
        );

        programarRecordatorio(
                doctorSeleccionado,
                especialidadSeleccionada,
                fecha,
                horaFormateada,
                ubicacion
        );

        Snackbar.make(findViewById(android.R.id.content),
                "Cita Registrada",
                Snackbar.LENGTH_SHORT).show();

        Intent intent = new Intent(this, CitasActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    private boolean existeNotificacionTipo(String tipoBuscado) {
        String correo = MainActivity.obtenerCorreoActual(this);

        SharedPreferences prefs = getSharedPreferences(
                LoginActivity.prefsNotificaciones(correo),
                MODE_PRIVATE
        );

        Set<String> lista = prefs.getStringSet("lista", new HashSet<>());

        for (String item : lista) {
            String[] partes = item.split("\\|");

            if (partes.length >= 4 && partes[3].equals(tipoBuscado)) {
                return true;
            }
        }

        return false;
    }

    private void resetHoras() {
        LinearLayout[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};

        for (LinearLayout h : horas) {
            h.setSelected(false);
            h.setEnabled(true);
            h.setAlpha(1f);
        }

        horaSeleccionada = "";
    }

    private void resetSeleccionDoctor() {
        doctorSeleccionado = "";
        especialidadSeleccionada = "";
        horaSeleccionada = "";

        resetSeleccionDoctorVisual();

        layoutFecha.setVisibility(View.GONE);
        layoutHorarios.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE);
        etFecha.setText("");

        resetHoras();
    }

    private String generarConsultorio(String especialidad) {
        int piso = 1;

        switch (especialidad) {
            case "Cardiología": piso = 1; break;
            case "Medicina General": piso = 2; break;
            case "Pediatría": piso = 3; break;
            case "Dermatología": piso = 4; break;
            case "Neumología": piso = 5; break;
            case "Gastroenterología": piso = 6; break;
            case "Neurología": piso = 7; break;
            case "Traumatología": piso = 8; break;
        }

        int numero = (int) (Math.random() * 10) + 1;

        return "Piso " + piso + " - Consultorio " + piso + "0" + numero;
    }

    private void mostrarNotificacionInstantanea(String doctor, String especialidad, String fecha, String hora, String lugar) {
        String channelId = "CITAS_CHANNEL";

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Citas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        String mensaje =
                "📅 Día: " + fecha + "\n" +
                        "🕒 Hora: " + hora + "\n" +
                        "📍 Lugar: " + lugar + "\n" +
                        "👨‍⚕️ " + doctor + "\n" +
                        "🩺 Especialidad: " + especialidad;

        Intent intent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_check)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_logo))
                        .setColor(ContextCompat.getColor(this, R.color.teal_700))
                        .setContentTitle("Cita reservada ✅")
                        .setContentText("Tu cita médica fue registrada")
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private String convertirHoraAMPM(String hora24) {
        try {
            String[] partes = hora24.split(":");
            int hora = Integer.parseInt(partes[0]);
            String minutos = partes[1];

            String periodo = (hora >= 12) ? "PM" : "AM";
            hora = hora % 12;

            if (hora == 0) hora = 12;

            return String.format("%02d:%s %s", hora, minutos, periodo);
        } catch (Exception e) {
            return hora24;
        }
    }

    private void programarRecordatorio(String doctor, String especialidad, String fecha, String hora, String lugar) {
        try {
            String[] fechaParts = fecha.split("/");
            String[] horaSplit = hora.split(" ");
            String[] hm = horaSplit[0].split(":");

            int hora24 = Integer.parseInt(hm[0]);
            int minutos = Integer.parseInt(hm[1]);

            String periodo = horaSplit.length > 1 ? horaSplit[1] : "";

            if (periodo.equals("PM") && hora24 != 12) hora24 += 12;
            if (periodo.equals("AM") && hora24 == 12) hora24 = 0;

            Calendar citaCalendar = Calendar.getInstance();

            citaCalendar.set(
                    Integer.parseInt(fechaParts[2]),
                    Integer.parseInt(fechaParts[1]) - 1,
                    Integer.parseInt(fechaParts[0]),
                    hora24,
                    minutos,
                    0
            );

            citaCalendar.set(Calendar.MILLISECOND, 0);

            long tiempoCita = citaCalendar.getTimeInMillis();
            long ahora = System.currentTimeMillis();
            long diferenciaMinutos = (tiempoCita - ahora) / (1000 * 60);

            if (diferenciaMinutos <= 0) return;

            Calendar recordatorioCalendar = Calendar.getInstance();
            String mensajeRecordatorio;

            if (diferenciaMinutos > 60) {
                recordatorioCalendar.setTimeInMillis(tiempoCita);
                recordatorioCalendar.add(Calendar.HOUR_OF_DAY, -1);
                mensajeRecordatorio = "Tu cita inicia en 1 hora";
            } else if (diferenciaMinutos >= 30) {
                recordatorioCalendar.setTimeInMillis(ahora + 1000);
                mensajeRecordatorio = "Tu cita inicia en " + diferenciaMinutos + " minutos";
            } else if (diferenciaMinutos >= 15) {
                recordatorioCalendar.setTimeInMillis(tiempoCita);
                recordatorioCalendar.add(Calendar.MINUTE, -15);
                mensajeRecordatorio = "Tu cita inicia en 15 minutos";
            } else if (diferenciaMinutos >= 5) {
                recordatorioCalendar.setTimeInMillis(tiempoCita);
                recordatorioCalendar.add(Calendar.MINUTE, -5);
                mensajeRecordatorio = "Tu cita inicia en 5 minutos";
            } else {
                recordatorioCalendar.setTimeInMillis(ahora + 1000);
                mensajeRecordatorio = "Tu cita inicia pronto";
            }

            Intent intent = new Intent(this, RecordatorioActivity.class);

            intent.putExtra("doctor", doctor);
            intent.putExtra("especialidad", especialidad);
            intent.putExtra("fecha", fecha);
            intent.putExtra("hora", hora);
            intent.putExtra("lugar", lugar);
            intent.putExtra("mensajeRecordatorio", mensajeRecordatorio);
            intent.putExtra("tiempoCita", tiempoCita);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (alarmManager != null) {
                alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        recordatorioCalendar.getTimeInMillis(),
                        pendingIntent
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}