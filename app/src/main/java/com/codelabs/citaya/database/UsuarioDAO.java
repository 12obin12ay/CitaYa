package com.codelabs.citaya.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class UsuarioDAO {

    private final DatabaseHelper dbHelper;

    public UsuarioDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    // REGISTRO (Usado en RegistroActivity y Login con Google)
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

    // LOGIN (Usado en LoginActivity)
    public Usuario login(String correo, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE correo = ? AND password = ?",
                new String[]{correo, password}
        );
        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = new Usuario(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
        }
        cursor.close();
        db.close();
        return usuario;
    }

    // BÚSQUEDA (Usado en Perfil y Citas)
    public Usuario buscarPorCorreo(String correo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM usuarios WHERE correo = ?",
                new String[]{correo}
        );
        Usuario usuario = null;
        if (cursor.moveToFirst()) {
            usuario = new Usuario(
                    cursor.getInt(0),
                    cursor.getString(1),
                    cursor.getString(2),
                    cursor.getString(3),
                    cursor.getString(4),
                    cursor.getString(5),
                    cursor.getString(6)
            );
        }
        cursor.close();
        db.close();
        return usuario;
    }

    // 🔥 NOMBRE POR CORREO (Usado en MainActivity y Citas para el QR)
    public String obtenerNombrePorCorreo(String correo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String nombre = "Paciente";
        Cursor cursor = db.rawQuery("SELECT nombre FROM usuarios WHERE correo = ?", new String[]{correo});
        if (cursor != null && cursor.moveToFirst()) {
            nombre = cursor.getString(0);
            cursor.close();
        }
        db.close();
        return nombre;
    }

    // ACTUALIZACIÓN (Usado en PerfilActivity)
    public boolean actualizarUsuario(Usuario usuario) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("nombre", usuario.getNombre());
        values.put("dni", usuario.getDni());
        values.put("telefono", usuario.getTelefono());
        int resultado = db.update("usuarios", values, "id = ?", new String[]{String.valueOf(usuario.getId())});
        db.close();
        return resultado > 0;
    }

    // EXISTENCIA (Usado en Registro)
    public boolean existeCorreo(String correo) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM usuarios WHERE correo = ?", new String[]{correo});
        boolean existe = cursor.moveToFirst();
        cursor.close();
        db.close();
        return existe;
    }
}
