package com.codelabs.citaya.database;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    // 🔥 CORREGIDO: Maneja conflictos si el horario ya existe (re-reserva)
    public long crearHorario(Horario horario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Primero verificamos si ya existe el registro para este médico, fecha y hora
        Cursor cursor = db.rawQuery(
                "SELECT id FROM horarios WHERE medico_id = ? AND fecha = ? AND hora = ?",
                new String[]{String.valueOf(horario.getMedicoId()), horario.getFecha(), horario.getHora()}
        );

        long id = -1;
        if (cursor.moveToFirst()) {
            // Si ya existe, simplemente lo recuperamos y actualizamos a OCUPADO
            id = cursor.getLong(0);
            ContentValues values = new ContentValues();
            values.put("estado", "OCUPADO");
            db.update("horarios", values, "id = ?", new String[]{String.valueOf(id)});
        } else {
            // Si no existe, lo insertamos normalmente
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

    // 🔥 CORREGIDO: Maneja conflictos si ya había una reserva para este horario (aunque fuera cancelada)
    public boolean crearReserva(Reserva reserva) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Verificamos si ya existe una reserva para este horario_id
        Cursor cursor = db.rawQuery(
                "SELECT id FROM reservas WHERE horario_id = ?",
                new String[]{String.valueOf(reserva.getHorarioId())}
        );

        long resultado;
        ContentValues values = new ContentValues();
        values.put("usuario_id", reserva.getUsuarioId());
        values.put("estado", reserva.getEstado());
        values.put("fecha_reserva", reserva.getFechaReserva());

        if (cursor.moveToFirst()) {
            // Si ya existe (ej. una reserva cancelada anteriormente), actualizamos la existente
            int idExistente = cursor.getInt(0);
            resultado = db.update("reservas", values, "id = ?", new String[]{String.valueOf(idExistente)});
        } else {
            // Si es nueva, insertamos
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
        return db.rawQuery(
                "SELECT r.id as reserva_id, h.id as horario_id, m.nombre as doctor, " +
                        "e.nombre as especialidad, h.fecha, h.hora, h.ubicacion, r.estado " +
                        "FROM reservas r " +
                        "INNER JOIN horarios h ON r.horario_id = h.id " +
                        "INNER JOIN medicos m ON h.medico_id = m.id " +
                        "INNER JOIN medico_especialidad me ON m.id = me.medico_id " +
                        "INNER JOIN especialidades e ON me.especialidad_id = e.id " +
                        "WHERE r.usuario_id = ?",
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
                "SELECT r.id " +
                        "FROM reservas r " +
                        "INNER JOIN horarios h ON r.horario_id = h.id " +
                        "WHERE r.usuario_id = ? " +
                        "AND h.fecha = ? " +
                        "AND h.hora = ? " +
                        "AND (r.estado = 'PENDIENTE' OR r.estado = 'CONFIRMADA')",
                new String[]{
                        String.valueOf(usuarioId),
                        fecha,
                        hora
                });

        boolean existe = cursor.moveToFirst();

        cursor.close();
        db.close();

        return existe;
    }
    public boolean doctorTieneCita(int medicoId, String fecha, String hora) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM horarios " +
                        "WHERE medico_id = ? AND fecha = ? AND hora = ? " +
                        "AND estado = 'OCUPADO'",
                new String[]{String.valueOf(medicoId), fecha, hora}
        );

        boolean ocupado = cursor.moveToFirst();
        cursor.close();
        db.close();

        return ocupado;
    }

    /**
     * NUEVO: indica si un médico tiene AL MENOS UNA de las horas fijas del día
     * (ej. "09:00", "10:00", ..., "18:00") libre, sin importar en qué fecha.
     *
     * Devuelve false únicamente cuando TODAS las horas de la lista están
     * ocupadas (estado = 'OCUPADO') en al menos una fecha cada una — es decir,
     * el médico está "completamente ocupado" y debería desaparecer de la lista.
     *
     * Se abre una sola conexión de lectura para las 9 consultas, en vez de
     * abrir/cerrar la BD 9 veces.
     */

    public boolean medicoTieneHoraLibre(int medicoId, String[] horasFijas) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        try {

            String hoy = new SimpleDateFormat("d/M/yyyy", Locale.US)
                    .format(new Date());

            Date ahora = new Date();

            for (String hora : horasFijas) {

                // 1. ¿Existe una reserva para esa hora?
                Cursor cursor = db.rawQuery(
                        "SELECT id FROM horarios " +
                                "WHERE medico_id=? AND hora=? AND estado='OCUPADO' LIMIT 1",
                        new String[]{
                                String.valueOf(medicoId),
                                hora
                        });

                boolean ocupadaBD = cursor.moveToFirst();
                cursor.close();

                // 2. ¿La hora ya pasó hoy?
                boolean pasoHoy = false;

                try {

                    Date fechaHora = new SimpleDateFormat(
                            "d/M/yyyy HH:mm",
                            Locale.US
                    ).parse(hoy + " " + hora);

                    pasoHoy = fechaHora.before(ahora);

                } catch (Exception ignored) {
                }

                // Si encontramos UNA sola hora libre,
                // el doctor sigue disponible.
                if (!ocupadaBD && !pasoHoy) {
                    return true;
                }
            }

            // Todas están ocupadas (por BD o porque ya pasaron hoy)
            return false;

        } finally {
            db.close();
        }
    }
}

