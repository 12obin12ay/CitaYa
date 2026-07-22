package com.codelabs.citaya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "citaya.db";
    private static final int DATABASE_VERSION = 7; // 🔥 Incrementado a 7 para backup de datos

    // TABLAS
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String TABLE_ESPECIALIDADES = "especialidades";
    public static final String TABLE_MEDICOS = "medicos";
    public static final String TABLE_MEDICO_ESPECIALIDAD = "medico_especialidad";
    public static final String TABLE_HORARIOS = "horarios";
    public static final String TABLE_RESERVAS = "reservas";

    // SQL CREACIÓN
    private static final String CREATE_TABLE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "dni TEXT NOT NULL," +
                    "telefono TEXT NOT NULL," +
                    "correo TEXT UNIQUE NOT NULL," +
                    "password TEXT NOT NULL," +
                    "sexo TEXT NOT NULL" +
                    ");";

    private static final String CREATE_TABLE_ESPECIALIDADES =
            "CREATE TABLE " + TABLE_ESPECIALIDADES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT UNIQUE NOT NULL" +
                    ");";

    private static final String CREATE_TABLE_MEDICOS =
            "CREATE TABLE " + TABLE_MEDICOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "rating REAL," +
                    "experiencia TEXT," +
                    "imagen TEXT" +
                    ");";

    private static final String CREATE_TABLE_MEDICO_ESPECIALIDAD =
            "CREATE TABLE " + TABLE_MEDICO_ESPECIALIDAD + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medico_id INTEGER NOT NULL," +
                    "especialidad_id INTEGER NOT NULL," +
                    "FOREIGN KEY(medico_id) REFERENCES medicos(id)," +
                    "FOREIGN KEY(especialidad_id) REFERENCES especialidades(id)," +
                    "UNIQUE(medico_id, especialidad_id)" +
                    ");";

    private static final String CREATE_TABLE_HORARIOS =
            "CREATE TABLE " + TABLE_HORARIOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medico_id INTEGER NOT NULL," +
                    "fecha TEXT NOT NULL," +
                    "hora TEXT NOT NULL," +
                    "estado TEXT NOT NULL," +
                    "ubicacion TEXT NOT NULL," +
                    "UNIQUE(medico_id, fecha, hora)," +
                    "FOREIGN KEY(medico_id) REFERENCES medicos(id)" +
                    ");";

    private static final String CREATE_TABLE_RESERVAS =
            "CREATE TABLE " + TABLE_RESERVAS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER NOT NULL," +
                    "horario_id INTEGER," + 
                    "estado TEXT NOT NULL," +
                    "fecha_reserva TEXT NOT NULL," +
                    "doctor_nombre TEXT," +      // 🔥 Backup para historial
                    "especialidad_nombre TEXT," + // 🔥 Backup para historial
                    "fecha_cita TEXT," +         // 🔥 Backup para historial
                    "hora_cita TEXT," +          // 🔥 Backup para historial
                    "ubicacion_cita TEXT," +     // 🔥 Backup para historial
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id)," +
                    "FOREIGN KEY(horario_id) REFERENCES horarios(id) ON DELETE SET NULL" +
                    ");";

    // 🔥 TRIGGER: Cuando una reserva se cancela, se borra el horario para liberarlo físicamente
    private static final String CREATE_TRIGGER_CANCELAR =
            "CREATE TRIGGER IF NOT EXISTS trg_liberar_horario_cancelado " +
            "AFTER UPDATE OF estado ON " + TABLE_RESERVAS + " " +
            "WHEN NEW.estado = 'CANCELADA' " +
            "BEGIN " +
            "    DELETE FROM " + TABLE_HORARIOS + " WHERE id = OLD.horario_id; " +
            "END;";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_ESPECIALIDADES);
        db.execSQL(CREATE_TABLE_MEDICOS);
        db.execSQL(CREATE_TABLE_MEDICO_ESPECIALIDAD);
        db.execSQL(CREATE_TABLE_HORARIOS);
        db.execSQL(CREATE_TABLE_RESERVAS);
        db.execSQL(CREATE_TRIGGER_CANCELAR);

        insertarDatosIniciales(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESERVAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HORARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICO_ESPECIALIDAD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDICOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ESPECIALIDADES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db);
    }

    private void insertarDatosIniciales(SQLiteDatabase db) {
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Cardiología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Medicina General')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Pediatría')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Dermatología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Neumología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Gastroenterología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Neurología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Traumatología')");

        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Miguel Salazar',4.9,'18 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Valeria Rojas',4.8,'14 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Andrés Cárdenas',5.0,'20 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Camila Torres',4.9,'12 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Luis Mendoza',4.8,'15 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. María González',4.7,'11 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Diego Navarro',5.0,'17 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Sofía Herrera',4.9,'13 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Sebastián Paredes',4.8,'10 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Ana Fernández',4.9,'16 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Javier Campos',4.8,'12 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Lucía Vega',4.7,'9 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Ricardo Fuentes',4.9,'19 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Daniela Castro',4.8,'11 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Martín Lozano',4.7,'14 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Carolina Medina',5.0,'18 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Fernando Cabrera',4.8,'13 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Gabriela León',4.9,'15 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Alejandro Rivera',5.0,'21 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Natalia Ortiz',4.9,'16 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. José Valdivia',4.8,'12 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Paola Morales',4.9,'14 años','doctora')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dr. Cristian Ramírez',5.0,'19 años','doctor')");
        db.execSQL("INSERT INTO medicos(nombre,rating,experiencia,imagen) VALUES('Dra. Andrea Silva',4.8,'10 años','doctora')");

        for (int i=1; i<=3; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",1)");
        for (int i=4; i<=6; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",2)");
        for (int i=7; i<=9; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",3)");
        for (int i=10; i<=12; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",4)");
        for (int i=13; i<=15; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",5)");
        for (int i=16; i<=18; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",6)");
        for (int i=19; i<=21; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",7)");
        for (int i=22; i<=24; i++) db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES("+i+",8)");
    }
}
