package com.codelabs.citaya;

import com.google.android.material.snackbar.Snackbar;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.app.PendingIntent;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class ReservaActivity extends AppCompatActivity {

    LinearLayout doc1, doc2, doc3, doc4;
    ImageView check1, check2, check3, check4;
    LinearLayout layoutFecha;
    EditText etFecha;

    LinearLayout layoutHorarios;

    LinearLayout hora1, hora2, hora3, hora4, hora5, hora6, hora7, hora8, hora9;

    MaterialButton btnConfirmar;

    private String horaSeleccionada = "";
    private String doctorSeleccionado = "";
    private String especialidadSeleccionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

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

        // DOCTORES
        doc1.setOnClickListener(v -> {
            seleccionar(1);
            doctorSeleccionado = "Dr. Carlos Rodríguez";
            especialidadSeleccionada = "Cardiología";
        });
        doc2.setOnClickListener(v -> {
            seleccionar(2);
            doctorSeleccionado = "Dra. María González";
            especialidadSeleccionada = "Medicina General";
        });
        doc3.setOnClickListener(v -> {
            seleccionar(3);
            doctorSeleccionado = "Dr. Luis Mendoza";
            especialidadSeleccionada = "Pediatría";
        });
        doc4.setOnClickListener(v -> {
            seleccionar(4);
            doctorSeleccionado = "Dra. Ana Fernández";
            especialidadSeleccionada = "Dermatología";
        });

        etFecha.setOnClickListener(v -> mostrarCalendario());

        LinearLayout[] horas = {
                hora1, hora2, hora3,
                hora4, hora5, hora6,
                hora7, hora8, hora9
        };

        for (LinearLayout h : horas) {
            h.setOnClickListener(v -> seleccionarHora((LinearLayout) v));
        }

        // CONFIRMAR
        btnConfirmar.setOnClickListener(v -> {

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

            // ✅ Declaradas UNA sola vez, después de las validaciones
            String fecha = etFecha.getText().toString();
            String ubicacion = generarConsultorio(especialidadSeleccionada);
            String horaFormateada = convertirHoraAMPM(horaSeleccionada);

            String cita = doctorSeleccionado + "|" +
                    especialidadSeleccionada + "|" +
                    fecha + "|" +
                    horaFormateada + "|" +
                    ubicacion + "|PENDIENTE";

            SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);

            Set<String> citas = prefs.getStringSet("lista", new HashSet<>());
            Set<String> nuevaLista = new HashSet<>(citas);

            Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());
            Set<String> copiaOcupados = new HashSet<>(ocupados);

            String clave = fecha + "_" + horaSeleccionada;

            // VALIDAR DUPLICADO
            if (copiaOcupados.contains(clave)) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Este horario ya está ocupado",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }

            // MÁXIMO 3 CITAS
            if (nuevaLista.size() >= 3) {
                Snackbar.make(findViewById(android.R.id.content),
                        "Máximo 3 citas permitidas",
                        Snackbar.LENGTH_SHORT).show();
                return;
            }

            nuevaLista.add(cita);
            copiaOcupados.add(clave);

            prefs.edit()
                    .putStringSet("lista", nuevaLista)
                    .putStringSet("ocupados", copiaOcupados)
                    .apply();


            // ✅ Notificación instantánea con todos los datos correctos
            mostrarNotificacionInstantanea(
                    doctorSeleccionado,
                    especialidadSeleccionada,
                    fecha,
                    horaFormateada,
                    ubicacion
            );

            // ✅ Recordatorio programado con todos los datos correctos
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

            startActivity(new Intent(this, CitasActivity.class));
        });
    }

    private void mostrarCalendario() {
        Calendar c = Calendar.getInstance();

        DatePickerDialog dp = new DatePickerDialog(this,
                (view, y, m, d) -> {

                    String fechaSeleccionada = d + "/" + (m + 1) + "/" + y;
                    etFecha.setText(fechaSeleccionada);
                    layoutHorarios.setVisibility(View.VISIBLE);

                    SharedPreferences prefs = getSharedPreferences("citas", MODE_PRIVATE);
                    Set<String> ocupados = prefs.getStringSet("ocupados", new HashSet<>());

                    LinearLayout[] horas = {
                            hora1, hora2, hora3,
                            hora4, hora5, hora6,
                            hora7, hora8, hora9
                    };

                    for (LinearLayout h : horas) {
                        String hora = obtenerHora(h);
                        String clave = fechaSeleccionada + "_" + hora;

                        if (ocupados.contains(clave)) {
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
                c.get(Calendar.DAY_OF_MONTH));

        dp.show();
    }

    private String obtenerHora(LinearLayout layout) {
        TextView txt = (TextView) layout.getChildAt(1);
        return txt.getText().toString();
    }

    private void seleccionarHora(LinearLayout seleccionada) {

        if (!seleccionada.isEnabled()) return;

        LinearLayout[] horas = {
                hora1, hora2, hora3,
                hora4, hora5, hora6,
                hora7, hora8, hora9
        };

        for (LinearLayout h : horas) {
            h.setSelected(false);
        }

        seleccionada.setSelected(true);
        horaSeleccionada = obtenerHora(seleccionada);

        btnConfirmar.setVisibility(View.VISIBLE);
    }

    private void seleccionar(int d) {

        doc1.setBackgroundResource(R.drawable.bg_card_normal);
        doc2.setBackgroundResource(R.drawable.bg_card_normal);
        doc3.setBackgroundResource(R.drawable.bg_card_normal);
        doc4.setBackgroundResource(R.drawable.bg_card_normal);

        check1.setVisibility(View.GONE);
        check2.setVisibility(View.GONE);
        check3.setVisibility(View.GONE);
        check4.setVisibility(View.GONE);

        if (d == 1) {
            doc1.setBackgroundResource(R.drawable.bg_card_selected);
            check1.setVisibility(View.VISIBLE);
        } else if (d == 2) {
            doc2.setBackgroundResource(R.drawable.bg_card_selected);
            check2.setVisibility(View.VISIBLE);
        } else if (d == 3) {
            doc3.setBackgroundResource(R.drawable.bg_card_selected);
            check3.setVisibility(View.VISIBLE);
        } else if (d == 4) {
            doc4.setBackgroundResource(R.drawable.bg_card_selected);
            check4.setVisibility(View.VISIBLE);
        }

        layoutFecha.setVisibility(View.VISIBLE);

        etFecha.setText("");
        layoutHorarios.setVisibility(View.GONE);
        btnConfirmar.setVisibility(View.GONE);

        resetHoras();
    }

    private void resetHoras() {
        LinearLayout[] horas = {
                hora1, hora2, hora3,
                hora4, hora5, hora6,
                hora7, hora8, hora9
        };

        for (LinearLayout h : horas) {
            h.setSelected(false);
            h.setEnabled(true);
            h.setAlpha(1f);
        }

        horaSeleccionada = "";
    }

    private String generarConsultorio(String especialidad) {

        int piso = 1;

        switch (especialidad) {
            case "Cardiología":      piso = 1; break;
            case "Medicina General": piso = 2; break;
            case "Pediatría":        piso = 3; break;
            case "Dermatología":     piso = 4; break;
        }

        int numero = (int) (Math.random() * 10) + 1;

        return "Piso " + piso + " - Consultorio " + piso + "0" + numero;
    }

    private void mostrarNotificacionInstantanea(
            String doctor,
            String especialidad,
            String fecha,
            String hora,
            String lugar
    ) {
        String channelId = "CITAS_CHANNEL";

        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // CANAL ANDROID 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Citas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notificaciones de citas médicas");
            manager.createNotificationChannel(channel);
        }

        // TEXTO DETALLADO
        String mensaje =
                "📅 Día: " + fecha + "\n" +
                        "🕒 Hora: " + hora + "\n" +
                        "📍 Lugar: " + lugar + "\n" +
                        "👨‍⚕️ " + doctor + "\n" +
                        "🩺 Especialidad: " + especialidad;

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

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

    private void programarRecordatorio(
            String doctor,
            String especialidad,
            String fecha,
            String hora,
            String lugar
    ) {
        try {
            // fecha: dd/MM/yyyy
            String[] fechaParts = fecha.split("/");

            // hora: 09:30 AM o 09:30 PM
            String[] horaSplit = hora.split(" ");
            String[] hm = horaSplit[0].split(":");

            int hora24 = Integer.parseInt(hm[0]);
            int minutos = Integer.parseInt(hm[1]);
            String periodo = horaSplit.length > 1 ? horaSplit[1] : "";

            // Convertir a formato 24h
            if (periodo.equals("PM") && hora24 != 12) hora24 += 12;
            if (periodo.equals("AM") && hora24 == 12) hora24 = 0;

            Calendar calendar = Calendar.getInstance();
            calendar.set(
                    Integer.parseInt(fechaParts[2]),      // año
                    Integer.parseInt(fechaParts[1]) - 1,  // mes
                    Integer.parseInt(fechaParts[0]),       // día
                    hora24,
                    minutos,
                    0
            );

            // Restar 20 minutos para recordatorio anticipado
            calendar.add(Calendar.MINUTE, -20);

            Intent intent = new Intent(this, RecordatorioActivity.class);
            intent.putExtra("doctor", doctor);
            intent.putExtra("especialidad", especialidad);
            intent.putExtra("fecha", fecha);
            intent.putExtra("hora", hora);
            intent.putExtra("lugar", lugar);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this,
                    (int) System.currentTimeMillis(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (!alarmManager.canScheduleExactAlarms()) {
                    Intent intent1 = new Intent(
                            android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    );
                    startActivity(intent1);
                    return;
                }
            }

            // ✅ Esta línea faltaba — sin ella la alarma nunca se disparaba
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    pendingIntent
            );

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}