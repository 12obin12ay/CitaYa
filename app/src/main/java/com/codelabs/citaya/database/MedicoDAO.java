package com.codelabs.citaya.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MedicoDAO {

    private DatabaseHelper dbHelper;

    public MedicoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public int obtenerIdPorNombre(String nombre) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id FROM medicos WHERE nombre = ?",
                new String[]{nombre}
        );

        int medicoId = -1;

        if (cursor.moveToFirst()) {
            medicoId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return medicoId;
    }
}
