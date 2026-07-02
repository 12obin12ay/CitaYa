package com.codelabs.citaya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "citaya.db";

    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_USUARIOS = "usuarios";

    private static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "nombre TEXT NOT NULL, " +
                    "dni TEXT NOT NULL, " +
                    "telefono TEXT NOT NULL, " +
                    "correo TEXT UNIQUE NOT NULL, " +
                    "password TEXT NOT NULL, " +
                    "sexo TEXT NOT NULL" +
                    ");";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);

        onCreate(db);
    }

}
