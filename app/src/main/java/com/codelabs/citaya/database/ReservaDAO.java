package com.codelabs.citaya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReservaDAO {

    private DatabaseHelper dbHelper;

    public ReservaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    // CREAR HORARIO

    public long crearHorario(Horario horario) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("medico_id", horario.getMedicoId());
        values.put("fecha", horario.getFecha());
        values.put("hora", horario.getHora());
        values.put("estado", horario.getEstado());
        values.put("ubicacion", horario.getUbicacion());

        long resultado = db.insert("horarios", null, values);

        db.close();

        return resultado;
    }


    // CREAR RESERVA

    public boolean crearReserva(Reserva reserva) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("usuario_id", reserva.getUsuarioId());
        values.put("horario_id", reserva.getHorarioId());
        values.put("estado", reserva.getEstado());
        values.put("fecha_reserva", reserva.getFechaReserva());

        long resultado = db.insert("reservas", null, values);

        db.close();

        return resultado != -1;
    }


    // CONTAR RESERVAS ACTIVAS

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


    // VERIFICAR SI HORARIO ESTA OCUPADO

    public boolean horarioOcupado(int medicoId, String fecha, String hora) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM horarios " +
                        "WHERE medico_id = ? AND fecha = ? AND hora = ?",
                new String[]{
                        String.valueOf(medicoId),
                        fecha,
                        hora
                }
        );

        boolean ocupado = cursor.moveToFirst();

        cursor.close();
        db.close();

        return ocupado;
    }


    public Cursor obtenerReservasPorUsuario(int usuarioId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT " +
                        "r.id as reserva_id, " +
                        "h.id as horario_id, " +
                        "m.nombre as doctor, " +
                        "e.nombre as especialidad, " +
                        "h.fecha, " +
                        "h.hora, " +
                        "h.ubicacion, " +
                        "r.estado " +
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

        int filas = db.update(
                "reservas",
                values,
                "id = ?",
                new String[]{String.valueOf(reservaId)}
        );

        db.close();

        return filas > 0;
    }


    public boolean actualizarEstadoHorario(int horarioId, String nuevoEstado) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("estado", nuevoEstado);

        int filas = db.update(
                "horarios",
                values,
                "id = ?",
                new String[]{String.valueOf(horarioId)}
        );

        db.close();

        return filas > 0;
    }

    public void liberarHorario(int horarioId) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("estado", "DISPONIBLE");

        db.update(
                "horarios",
                values,
                "id = ?",
                new String[]{String.valueOf(horarioId)}
        );

        db.close();
    }

    public boolean actualizarReserva(int reservaId, int nuevoHorarioId, String nuevoEstado) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("horario_id", nuevoHorarioId);
        values.put("estado", nuevoEstado);

        int filas = db.update(
                "reservas",
                values,
                "id = ?",
                new String[]{String.valueOf(reservaId)}
        );

        return filas > 0;
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
}
