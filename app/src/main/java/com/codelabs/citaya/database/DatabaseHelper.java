package com.codelabs.citaya.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "citaya.db";
    private static final int DATABASE_VERSION = 3;

    // TABLAS

    private static final String TABLE_USUARIOS = "usuarios";
    private static final String TABLE_ESPECIALIDADES = "especialidades";
    private static final String TABLE_MEDICOS = "medicos";
    private static final String TABLE_MEDICO_ESPECIALIDAD = "medico_especialidad";
    private static final String TABLE_HORARIOS = "horarios";
    private static final String TABLE_RESERVAS = "reservas";


    // USUARIOS

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


    // ESPECIALIDADES

    private static final String CREATE_TABLE_ESPECIALIDADES =
            "CREATE TABLE " + TABLE_ESPECIALIDADES + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT UNIQUE NOT NULL" +
                    ");";


    // MEDICOS

    private static final String CREATE_TABLE_MEDICOS =
            "CREATE TABLE " + TABLE_MEDICOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "nombre TEXT NOT NULL," +
                    "rating REAL," +
                    "experiencia TEXT," +
                    "imagen TEXT" +
                    ");";


    // MEDICO ESPECIALIDAD

    private static final String CREATE_TABLE_MEDICO_ESPECIALIDAD =
            "CREATE TABLE " + TABLE_MEDICO_ESPECIALIDAD + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medico_id INTEGER NOT NULL," +
                    "especialidad_id INTEGER NOT NULL," +
                    "FOREIGN KEY(medico_id) REFERENCES medicos(id)," +
                    "FOREIGN KEY(especialidad_id) REFERENCES especialidades(id)," +
                    "UNIQUE(medico_id, especialidad_id)" +
                    ");";


    // HORARIOS
    private static final String CREATE_TABLE_HORARIOS =
            "CREATE TABLE " + TABLE_HORARIOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "medico_id INTEGER NOT NULL," +
                    "fecha TEXT NOT NULL," +
                    "hora TEXT NOT NULL," +
                    "estado TEXT NOT NULL," +
                    "UNIQUE(medico_id, fecha, hora)," +
                    "FOREIGN KEY(medico_id) REFERENCES medicos(id)" +
                    ");";


    // RESERVAS
    private static final String CREATE_TABLE_RESERVAS =
            "CREATE TABLE " + TABLE_RESERVAS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "usuario_id INTEGER NOT NULL," +
                    "horario_id INTEGER NOT NULL," +
                    "estado TEXT NOT NULL," +
                    "fecha_reserva TEXT NOT NULL," +
                    "UNIQUE(horario_id)," +
                    "FOREIGN KEY(usuario_id) REFERENCES usuarios(id)," +
                    "FOREIGN KEY(horario_id) REFERENCES horarios(id)" +
                    ");";


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

        insertarDatosIniciales(db);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS reservas");
        db.execSQL("DROP TABLE IF EXISTS horarios");
        db.execSQL("DROP TABLE IF EXISTS medico_especialidad");
        db.execSQL("DROP TABLE IF EXISTS medicos");
        db.execSQL("DROP TABLE IF EXISTS especialidades");
        db.execSQL("DROP TABLE IF EXISTS usuarios");

        onCreate(db);
    }


    private void insertarDatosIniciales(SQLiteDatabase db) {

        // Especialidades

        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Cardiología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Medicina General')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Pediatría')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Dermatología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Neumología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Gastroenterología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Neurología')");
        db.execSQL("INSERT INTO especialidades(nombre) VALUES('Traumatología')");


        // Médicos

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


        // Relación medico-especialidad

        // Cardiología (1)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(1,1)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(2,1)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(3,1)");

        // Medicina General (2)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(4,2)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(5,2)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(6,2)");

        // Pediatría (3)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(7,3)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(8,3)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(9,3)");

        // Dermatología (4)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(10,4)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(11,4)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(12,4)");

        // Neumología (5)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(13,5)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(14,5)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(15,5)");

        // Gastroenterología (6)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(16,6)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(17,6)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(18,6)");

        // Neurología (7)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(19,7)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(20,7)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(21,7)");

        // Traumatología (8)
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(22,8)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(23,8)");
        db.execSQL("INSERT INTO medico_especialidad(medico_id,especialidad_id) VALUES(24,8)");
    }
}
