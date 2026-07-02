package com.codelabs.citaya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDAO {

    private DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }


    public boolean registrarUsuario(Usuario usuario) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("nombre", usuario.getNombre());
        values.put("dni", usuario.getDni());
        values.put("telefono", usuario.getTelefono());
        values.put("correo", usuario.getCorreo());
        values.put("password", usuario.getPassword());
        values.put("sexo", usuario.getSexo());

        long resultado = db.insert("usuarios", null, values);

        db.close();

        return resultado != -1;
    }

    public boolean existeCorreo(String correo) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE correo = ?",
                new String[]{correo}
        );

        boolean existe = cursor.getCount() > 0;

        cursor.close();
        db.close();

        return existe;
    }

    public Usuario login(String correo, String password) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE correo = ? AND password = ?",
                new String[]{correo, password}
        );

        Usuario usuario = null;

        if (cursor.moveToFirst()) {

            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            String dni = cursor.getString(2);
            String telefono = cursor.getString(3);
            String correoDb = cursor.getString(4);
            String passwordDb = cursor.getString(5);
            String sexo = cursor.getString(6);

            usuario = new Usuario(
                    id,
                    nombre,
                    dni,
                    telefono,
                    correoDb,
                    passwordDb,
                    sexo
            );
        }

        cursor.close();
        db.close();

        return usuario;
    }

    public Usuario buscarPorCorreo(String correo) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE correo = ?",
                new String[]{correo}
        );

        Usuario usuario = null;

        if (cursor.moveToFirst()) {

            int id = cursor.getInt(0);
            String nombre = cursor.getString(1);
            String dni = cursor.getString(2);
            String telefono = cursor.getString(3);
            String correoDb = cursor.getString(4);
            String passwordDb = cursor.getString(5);
            String sexo = cursor.getString(6);

            usuario = new Usuario(
                    id,
                    nombre,
                    dni,
                    telefono,
                    correoDb,
                    passwordDb,
                    sexo
            );
        }

        cursor.close();
        db.close();

        return usuario;
    }
}
