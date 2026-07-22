package com.codelabs.citaya.database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReservaDAO {

    private DatabaseHelper dbHelper;

    public ReservaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long crearHorario(Horario horario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM horarios WHERE medico_id = ? AND fecha = ? AND hora = ?",
                new String[]{String.valueOf(horario.getMedicoId()), horario.getFecha(), horario.getHora()}
        );

        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
            ContentValues values = new ContentValues();
            values.put("estado", "OCUPADO");
            db.update("horarios", values, "id = ?", new String[]{String.valueOf(id)});
        } else {
            ContentValues values = new ContentValues();
            values.put("medico_id", horario.getMedicoId());
            values.put("fecha", horario.getFecha());
            values.put("hora", horario.getHora());
            values.put("estado", "OCUPADO");
            values.put("ubicacion", horario.getUbicacion());
            id = db.insert("horarios", null, values);
        }
        cursor.close();
        db.close();
        return id;
    }

    public boolean crearReserva(Reserva reserva) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM reservas WHERE horario_id = ?",
                new String[]{String.valueOf(reserva.getHorarioId())}
        );

        long resultado;
        ContentValues values = new ContentValues();
        values.put("usuario_id", reserva.getUsuarioId());
        values.put("estado", reserva.getEstado());
        values.put("fecha_reserva", reserva.getFechaReserva());
        
        // 🔥 Guardar backup para historial
        values.put("doctor_nombre", reserva.getDoctorNombre());
        values.put("especialidad_nombre", reserva.getEspecialidadNombre());
        values.put("fecha_cita", reserva.getFechaCita());
        values.put("hora_cita", reserva.getHoraCita());
        values.put("ubicacion_cita", reserva.getUbicacionCita());

        if (cursor.moveToFirst()) {
            int idExistente = cursor.getInt(0);
            resultado = db.update("reservas", values, "id = ?", new String[]{String.valueOf(idExistente)});
        } else {
            values.put("horario_id", reserva.getHorarioId());
            resultado = db.insert("reservas", null, values);
        }

        cursor.close();
        db.close();
        return resultado != -1;
    }

    public int contarReservasActivas(int usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM reservas " +
                        "WHERE usuario_id = ? " +
                        "AND (estado = 'PENDIENTE' OR estado = 'CONFIRMADA')",
                new String[]{String.valueOf(usuarioId)}
        );
        int cantidad = 0;
        if (cursor.moveToFirst()) {
            cantidad = cursor.getInt(0);
        }
        cursor.close();
        db.close();
        return cantidad;
    }

    public boolean horarioOcupado(int medicoId, String fecha, String hora) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM horarios " +
                        "WHERE medico_id = ? AND fecha = ? AND hora = ? AND estado = 'OCUPADO'",
                new String[]{String.valueOf(medicoId), fecha, hora}
        );
        boolean ocupado = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ocupado;
    }

    public Cursor obtenerReservasPorUsuario(int usuarioId) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        // 🔥 Ahora leemos directamente de 'reservas' para no perder datos si el horario se borra
        return db.rawQuery(
                "SELECT id as reserva_id, horario_id, doctor_nombre as doctor, " +
                        "especialidad_nombre as especialidad, fecha_cita as fecha, " +
                        "hora_cita as hora, ubicacion_cita as ubicacion, estado " +
                        "FROM reservas WHERE usuario_id = ? ORDER BY id DESC",
                new String[]{String.valueOf(usuarioId)}
        );
    }

    public boolean actualizarEstadoReserva(int reservaId, String nuevoEstado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("estado", nuevoEstado);
        int filas = db.update("reservas", values, "id = ?", new String[]{String.valueOf(reservaId)});
        db.close();
        return filas > 0;
    }

    public void liberarHorario(int horarioId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("estado", "DISPONIBLE");
        db.update("horarios", values, "id = ?", new String[]{String.valueOf(horarioId)});
        db.close();
    }

    public boolean horarioOcupadoActivo(int medicoId, String fecha, String hora) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM horarios WHERE medico_id = ? AND fecha = ? AND hora = ? AND estado = 'OCUPADO'",
                new String[]{String.valueOf(medicoId), fecha, hora}
        );
        boolean ocupado = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ocupado;
    }
    
    public boolean usuarioTieneCitaEnHorario(int usuarioId, String fecha, String hora) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM reservas " +
                        "WHERE usuario_id = ? AND fecha_cita = ? AND hora_cita = ? " +
                        "AND (estado = 'PENDIENTE' OR estado = 'CONFIRMADA')",
                new String[]{String.valueOf(usuarioId), fecha, hora});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }

    public boolean doctorTieneCita(int medicoId, String fecha, String hora) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT id FROM horarios " +
                        "WHERE medico_id = ? AND fecha = ? AND hora = ? AND estado = 'OCUPADO'",
                new String[]{String.valueOf(medicoId), fecha, hora}
        );
        boolean ocupado = cursor.moveToFirst();
        cursor.close();
        db.close();
        return ocupado;
    }

    public boolean medicoTieneHoraLibre(int medicoId, String[] horasFijas) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        try {
            String hoy = new SimpleDateFormat("d/M/yyyy", Locale.US).format(new Date());
            Date ahora = new Date();
            for (String hora : horasFijas) {
                Cursor cursor = db.rawQuery(
                        "SELECT id FROM horarios " +
                                "WHERE medico_id=? AND hora=? AND estado='OCUPADO' LIMIT 1",
                        new String[]{String.valueOf(medicoId), hora});
                boolean ocupadaBD = cursor.moveToFirst();
                cursor.close();
                boolean pasoHoy = false;
                try {
                    Date fechaHora = new SimpleDateFormat("d/M/yyyy HH:mm", Locale.US).parse(hoy + " " + hora);
                    pasoHoy = fechaHora.before(ahora);
                } catch (Exception ignored) {}
                if (!ocupadaBD && !pasoHoy) return true;
            }
            return false;
        } finally {
            db.close();
        }
    }
}
