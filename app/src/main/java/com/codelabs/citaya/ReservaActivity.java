package com.codelabs.citaya;

import com.google.android.material.snackbar.Snackbar;
import com.codelabs.citaya.database.Usuario;
import com.codelabs.citaya.database.UsuarioDAO;
import com.codelabs.citaya.database.MedicoDAO;
import com.codelabs.citaya.database.ReservaDAO;
import com.codelabs.citaya.database.Reserva;
import com.codelabs.citaya.database.Horario;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ReservaActivity extends AppCompatActivity {

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

    private boolean modoReprogramar = false;
    private int reservaIdOriginal = -1;
    private String fechaAnterior = "";
    private String horaAnterior = "";
    private String estadoOriginal = "PENDIENTE";

    private String doctorForzarInclusion = null;

    private UsuarioDAO usuarioDAO;
    private MedicoDAO medicoDAO;
    private ReservaDAO reservaDAO;

    private HashMap<String, Doctor[]> doctoresPorEspecialidad = new HashMap<>();
    private List<Doctor> doctoresVisiblesActuales = new ArrayList<>();

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
        setContentView(R.layout.activity_reserva);

        usuarioDAO = new UsuarioDAO(this);
        medicoDAO = new MedicoDAO(this);
        reservaDAO = new ReservaDAO(this);

        inicializarDoctores();
        vincularVistas();
        configurarModoReprogramar();
        configurarEventos();
        actualizarContadoresEspecialidades();

        if (modoReprogramar) {
            cargarDatosReprogramacion();
        } else {
            String especialidadExtra = getIntent().getStringExtra("especialidad");
            if (especialidadExtra != null && doctoresPorEspecialidad.containsKey(especialidadExtra)) {
                abrirDoctores(especialidadExtra);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        actualizarContadoresEspecialidades();
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

    private void configurarModoReprogramar() {
        modoReprogramar = "reprogramar".equals(getIntent().getStringExtra("modo"));
        reservaIdOriginal = getIntent().getIntExtra("reservaId", -1);
        fechaAnterior = getIntent().getStringExtra("fecha");
        horaAnterior = getIntent().getStringExtra("hora");
        estadoOriginal = getIntent().getStringExtra("estado");

        if (fechaAnterior == null) fechaAnterior = "";
        if (horaAnterior == null) horaAnterior = "";
        if (estadoOriginal == null || estadoOriginal.isEmpty()) estadoOriginal = "PENDIENTE";

        if (modoReprogramar) {
            btnConfirmar.setText("Guardar cambios");
            doctorForzarInclusion = getIntent().getStringExtra("doctor");
        }
    }

    private void configurarEventos() {
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            if (layoutFecha.getVisibility() == View.VISIBLE || layoutHorarios.getVisibility() == View.VISIBLE) {
                resetSeleccionDoctor();
            } else if (layoutDoctores.getVisibility() == View.VISIBLE) {
                layoutDoctores.setVisibility(View.GONE);
                layoutEspecialidades.setVisibility(View.VISIBLE);
                actualizarContadoresEspecialidades();
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
        if (doc4 != null) doc4.setOnClickListener(v -> seleccionarDoctor(4));

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

    private String[] obtenerHorasFijas() {
        LinearLayout[] horasViews = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};
        String[] horas = new String[horasViews.length];
        for (int i = 0; i < horasViews.length; i++) {
            horas[i] = obtenerHora(horasViews[i]);
        }
        return horas;
    }

    private boolean doctorCompletamenteOcupado(String doctorNombre) {
        int medicoId = medicoDAO.obtenerIdPorNombre(doctorNombre);
        if (medicoId == -1) return false;
        String[] horasFijas = obtenerHorasFijas();
        return !reservaDAO.medicoTieneHoraLibre(medicoId, horasFijas);
    }

    private int contarDoctoresDisponibles(String especialidad) {
        Doctor[] doctores = doctoresPorEspecialidad.get(especialidad);
        if (doctores == null) return 0;
        int disponibles = 0;
        for (Doctor d : doctores) {
            boolean ocupado = doctorCompletamenteOcupado(d.nombre);
            boolean esForzado = doctorForzarInclusion != null && doctorForzarInclusion.equals(d.nombre);
            if (!ocupado || esForzado) disponibles++;
        }
        return disponibles;
    }

    private void actualizarContadoresEspecialidades() {
        actualizarContadorCard(cardCardiologia, "Cardiología");
        actualizarContadorCard(cardMedicina, "Medicina General");
        actualizarContadorCard(cardPediatria, "Pediatría");
        actualizarContadorCard(cardDermatologia, "Dermatología");
        actualizarContadorCard(cardNeumologia, "Neumología");
        actualizarContadorCard(cardGastro, "Gastroenterología");
        actualizarContadorCard(cardNeurologia, "Neurología");
        actualizarContadorCard(cardTraumatologia, "Traumatología");
    }

    private void actualizarContadorCard(LinearLayout card, String especialidad) {
        if (card == null) return;
        try {
            LinearLayout contenedorTextos = (LinearLayout) card.getChildAt(1);
            TextView subtitulo = (TextView) contenedorTextos.getChildAt(1);
            int disponibles = contarDoctoresDisponibles(especialidad);
            if (disponibles <= 0) subtitulo.setText("Sin disponibilidad por ahora");
            else if (disponibles == 1) subtitulo.setText("1 doctor disponible");
            else subtitulo.setText(disponibles + " doctores disponibles");
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void abrirDoctores(String especialidad) {
        especialidadSeleccionada = especialidad;
        Doctor[] todos = doctoresPorEspecialidad.get(especialidad);
        List<Doctor> disponibles = new ArrayList<>();
        for (Doctor d : todos) {
            boolean ocupado = doctorCompletamenteOcupado(d.nombre);
            boolean esForzado = doctorForzarInclusion != null && doctorForzarInclusion.equals(d.nombre);
            if (!ocupado || esForzado) disponibles.add(d);
        }
        if (disponibles.isEmpty()) {
            Toast.makeText(this, "No hay doctores disponibles para " + especialidad, Toast.LENGTH_LONG).show();
            actualizarContadoresEspecialidades();
            return;
        }
        doctoresVisiblesActuales = disponibles;
        LinearLayout[] cards = {doc1, doc2, doc3, doc4};
        ImageView[] checks = {check1, check2, check3, check4};
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == null) continue;
            if (i < doctoresVisiblesActuales.size()) {
                cargarDoctorEnCard(cards[i], checks[i], doctoresVisiblesActuales.get(i));
                cards[i].setVisibility(View.VISIBLE);
            } else cards[i].setVisibility(View.GONE);
        }
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
        if (check != null) check.setVisibility(View.GONE);
    }

    private void seleccionarDoctor(int d) {
        resetSeleccionDoctorVisual();
        int index = d - 1;
        if (doctoresVisiblesActuales == null || index < 0 || index >= doctoresVisiblesActuales.size()) return;
        LinearLayout[] cards = {doc1, doc2, doc3, doc4};
        ImageView[] checks = {check1, check2, check3, check4};
        if (cards[index] != null) cards[index].setBackgroundResource(R.drawable.bg_card_selected);
        if (checks[index] != null) checks[index].setVisibility(View.VISIBLE);
        doctorSeleccionado = doctoresVisiblesActuales.get(index).nombre;
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
        if (doc4 != null) doc4.setBackgroundResource(R.drawable.bg_card_normal);
        check1.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);
        check3.setVisibility(View.GONE);
        if (check4 != null) check4.setVisibility(View.GONE);
    }

    private void cargarDatosReprogramacion() {
        String especialidadIntent = getIntent().getStringExtra("especialidad");
        String doctorIntent = getIntent().getStringExtra("doctor");
        if (especialidadIntent != null && doctoresPorEspecialidad.containsKey(especialidadIntent)) {
            abrirDoctores(especialidadIntent);
        }
        if (doctorIntent != null && doctoresVisiblesActuales != null) {
            for (int i = 0; i < doctoresVisiblesActuales.size(); i++) {
                if (doctorIntent.equals(doctoresVisiblesActuales.get(i).nombre)) {
                    seleccionarDoctor(i + 1);
                    break;
                }
            }
        }
    }

    private boolean horaYaPaso(String fecha, String horaTexto) {
        try {
            String horaLimpia = horaTexto.trim();
            SimpleDateFormat format = (horaLimpia.contains("AM") || horaLimpia.contains("PM")) ? 
                new SimpleDateFormat("d/M/yyyy hh:mm a", Locale.US) : 
                new SimpleDateFormat("d/M/yyyy HH:mm", Locale.US);
            Date fechaHora = format.parse(fecha + " " + horaLimpia);
            return fechaHora != null && fechaHora.before(new Date());
        } catch (Exception e) { return false; }
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();
        DatePickerDialog dp = new DatePickerDialog(this, (view, y, m, d) -> {
            String fechaSeleccionada = d + "/" + (m + 1) + "/" + y;
            etFecha.setText(fechaSeleccionada);
            layoutHorarios.setVisibility(View.VISIBLE);
            int medicoId = medicoDAO.obtenerIdPorNombre(doctorSeleccionado);
            LinearLayout[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};
            for (LinearLayout h : horas) {
                String hora = obtenerHora(h);
                boolean ocupado = reservaDAO.horarioOcupadoActivo(medicoId, fechaSeleccionada, hora);
                boolean esAnterior = modoReprogramar && fechaSeleccionada.equals(fechaAnterior) && convertirHoraAMPM(hora).equals(horaAnterior);
                if (!esAnterior && (ocupado || horaYaPaso(fechaSeleccionada, hora))) {
                    h.setEnabled(false); h.setAlpha(0.3f);
                } else { h.setEnabled(true); h.setAlpha(1f); }
            }
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
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
        for (LinearLayout h : horas) h.setSelected(false);
        seleccionada.setSelected(true);
        horaSeleccionada = obtenerHora(seleccionada);
        btnConfirmar.setVisibility(View.VISIBLE);
    }

    private void confirmarCita() {
        if (doctorSeleccionado.isEmpty() || etFecha.getText().toString().isEmpty() || horaSeleccionada.isEmpty()) {
            Toast.makeText(this, "Completa la selección", Toast.LENGTH_SHORT).show();
            return;
        }

        String fecha = etFecha.getText().toString();
        String ubicacion = generarConsultorio(especialidadSeleccionada);
        String horaAMPM = convertirHoraAMPM(horaSeleccionada);
        String correo = MainActivity.obtenerCorreoActual(this);
        Usuario usuario = usuarioDAO.buscarPorCorreo(correo);
        if (usuario == null) return;

        int medicoId = medicoDAO.obtenerIdPorNombre(doctorSeleccionado);
        if (reservaDAO.contarReservasActivas(usuario.getId()) >= 3) {
            Snackbar.make(findViewById(android.R.id.content), "Máximo 3 citas activas", Snackbar.LENGTH_SHORT).show();
            return;
        }

        if (reservaDAO.usuarioTieneCitaEnHorario(usuario.getId(), fecha, horaSeleccionada)) {
            Snackbar.make(findViewById(android.R.id.content), "Ya tienes una cita en ese horario", Snackbar.LENGTH_LONG).show();
            return;
        }

        if (reservaDAO.doctorTieneCita(medicoId, fecha, horaSeleccionada)) {
            Snackbar.make(findViewById(android.R.id.content), "El doctor ya está ocupado", Snackbar.LENGTH_LONG).show();
            return;
        }

        Horario horario = new Horario(medicoId, fecha, horaSeleccionada, "OCUPADO", ubicacion);
        long hId = reservaDAO.crearHorario(horario);
        if (hId == -1) { Toast.makeText(this, "Error al crear horario", Toast.LENGTH_SHORT).show(); return; }

        // 🔥 CORREGIDO: Pasamos los 9 parámetros requeridos por el nuevo constructor de Reserva
        Reserva reserva = new Reserva(usuario.getId(), (int) hId, "PENDIENTE", fecha,
                doctorSeleccionado, especialidadSeleccionada, fecha, horaAMPM, ubicacion);

        if (reservaDAO.crearReserva(reserva)) {
            NotificacionesActivity.guardarNotificacion(this, "Cita agendada", "Cita con " + doctorSeleccionado + " el " + fecha + " a las " + horaAMPM, "cita");
            mostrarNotificacionInstantanea(doctorSeleccionado, especialidadSeleccionada, fecha, horaAMPM, ubicacion);
            programarRecordatorio(doctorSeleccionado, especialidadSeleccionada, fecha, horaAMPM, ubicacion);
            startActivity(new Intent(this, CitasActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        } else {
            Toast.makeText(this, "Error al registrar reserva", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetHoras() {
        LinearLayout[] horas = {hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9};
        for (LinearLayout h : horas) { h.setSelected(false); h.setEnabled(true); h.setAlpha(1f); }
        horaSeleccionada = "";
    }

    private void resetSeleccionDoctor() {
        doctorSeleccionado = ""; especialidadSeleccionada = ""; horaSeleccionada = "";
        resetSeleccionDoctorVisual();
        layoutFecha.setVisibility(View.GONE); layoutHorarios.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE); etFecha.setText("");
        resetHoras();
    }

    private String generarConsultorio(String esp) {
        int piso = 1;
        switch (esp) {
            case "Cardiología": piso = 1; break;
            case "Medicina General": piso = 2; break;
            case "Pediatría": piso = 3; break;
            case "Dermatología": piso = 4; break;
            case "Neumología": piso = 5; break;
            case "Gastroenterología": piso = 6; break;
            case "Neurología": piso = 7; break;
            case "Traumatología": piso = 8; break;
        }
        return "Piso " + piso + " - Consultorio " + piso + "0" + ((int) (Math.random() * 10) + 1);
    }

    private void mostrarNotificacionInstantanea(String doc, String esp, String f, String h, String l) {
        String cid = "CITAS_CHANNEL";
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) nm.createNotificationChannel(new NotificationChannel(cid, "Citas", NotificationManager.IMPORTANCE_HIGH));
        String msg = "📅 " + f + " 🕒 " + h + "\n📍 " + l + "\n👨‍⚕️ " + doc;
        nm.notify((int) System.currentTimeMillis(), new NotificationCompat.Builder(this, cid)
                .setSmallIcon(R.drawable.ic_check).setColor(ContextCompat.getColor(this, R.color.teal_700))
                .setContentTitle("Cita reservada ✅").setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .setAutoCancel(true).build());
    }

    private String convertirHoraAMPM(String h24) {
        try {
            String[] p = h24.split(":");
            int h = Integer.parseInt(p[0]);
            return String.format("%02d:%s %s", (h % 12 == 0 ? 12 : h % 12), p[1], (h >= 12 ? "PM" : "AM"));
        } catch (Exception e) { return h24; }
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
            citaCalendar.set(Integer.parseInt(fechaParts[2]), Integer.parseInt(fechaParts[1]) - 1, Integer.parseInt(fechaParts[0]), hora24, minutos, 0);
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
                mensajeRecordatorio = "Tu cita inicia in 15 minutos";
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

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.set(AlarmManager.RTC_WAKEUP, recordatorioCalendar.getTimeInMillis(), pendingIntent);
            }
        } catch (Exception e) { e.printStackTrace(); }
    }
}
