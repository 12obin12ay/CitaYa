package com.codelabs.citaya;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class RecordatorioActivity extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String doctor = intent.getStringExtra("doctor");
        String especialidad = intent.getStringExtra("especialidad");
        String fecha = intent.getStringExtra("fecha");
        String hora = intent.getStringExtra("hora");
        String lugar = intent.getStringExtra("lugar");
        long tiempoCita = intent.getLongExtra("tiempoCita", 0);
        String mensajeRecordatorio;

        if (tiempoCita > 0) {
            long diferenciaMinutos = (tiempoCita - System.currentTimeMillis()) / (1000 * 60);

            if (diferenciaMinutos >= 60) {
                mensajeRecordatorio = "Tu cita inicia en 1 hora";
            } else if (diferenciaMinutos > 1) {
                mensajeRecordatorio = "Tu cita inicia en " + diferenciaMinutos + " minutos";
            } else if (diferenciaMinutos == 1) {
                mensajeRecordatorio = "Tu cita inicia en 1 minuto";
            } else {
                mensajeRecordatorio = "Tu cita inicia pronto";
            }
        } else {
            mensajeRecordatorio = intent.getStringExtra("mensajeRecordatorio");

            if (mensajeRecordatorio == null || mensajeRecordatorio.isEmpty()) {
                mensajeRecordatorio = "Tu cita está próxima";
            }
        }
        intent.putExtra("tiempoCita", tiempoCita);

        if (mensajeRecordatorio == null || mensajeRecordatorio.isEmpty()) {
            mensajeRecordatorio = "Tu cita está próxima";
        }

        String channelId = "CITAS_CHANNEL";

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Citas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        Intent abrirApp = new Intent(context, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                abrirApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String mensaje =
                "⏰ " + mensajeRecordatorio + "\n" +
                        "📅 Día: " + fecha + "\n" +
                        "🕒 Hora: " + hora + "\n" +
                        "📍 Lugar: " + lugar + "\n" +
                        "👨‍⚕️ " + doctor + "\n" +
                        "🩺 Especialidad: " + especialidad;

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.drawable.ic_check)
                        .setLargeIcon(
                                BitmapFactory.decodeResource(
                                        context.getResources(),
                                        R.drawable.ic_logo
                                )
                        )
                        .setColor(
                                ContextCompat.getColor(context, R.color.teal_700)
                        )
                        .setContentTitle("Recordatorio de cita ⏰")
                        .setContentText(mensajeRecordatorio)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(mensaje))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}