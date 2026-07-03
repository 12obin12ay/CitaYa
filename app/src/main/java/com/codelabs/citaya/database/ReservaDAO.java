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
                "SELECT * FROM horarios " +
                        "WHERE medico_id = ? AND fecha = ? AND hora = ?",
                new String[]{
                        String.valueOf(medicoId),
                        fecha,
                        hora
                }
        );

        boolean ocupado = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return ocupado;
    }


    public Cursor obtenerReservasPorUsuario(int usuarioId) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.rawQuery(
                "SELECT r.id, " +
                        "m.nombre, " +
                        "e.nombre, " +
                        "h.fecha, " +
                        "h.hora, " +
                        "r.estado " +
                        "FROM reservas r " +
                        "INNER JOIN horarios h ON r.horario_id = h.id " +
                        "INNER JOIN medicos m ON h.medico_id = m.id " +
                        "INNER JOIN medico_especialidad me ON m.id = me.medico_id " +
                        "INNER JOIN especialidades e ON me.especialidad_id = e.id " +
                        "WHERE r.usuario_id = ?",
                new String[]{
                        String.valueOf(usuarioId)
                }
        );
    }
}
