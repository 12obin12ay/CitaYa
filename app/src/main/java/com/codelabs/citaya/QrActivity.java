package com.codelabs.citaya;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class QrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        // Vincular vistas
        ImageView imgQr = findViewById(R.id.imgQr);
        TextView txtPaciente = findViewById(R.id.qrTxtPaciente);
        TextView txtDoctor = findViewById(R.id.qrTxtDoctor);
        TextView txtEspecialidad = findViewById(R.id.qrTxtEspecialidad);
        TextView txtFechaHora = findViewById(R.id.qrTxtFechaHora);
        TextView txtEstado = findViewById(R.id.qrTxtEstado);
        ImageView btnBack = findViewById(R.id.btnBackQr);

        btnBack.setOnClickListener(v -> finish());

        // Obtener datos del Intent
        int reservaId = getIntent().getIntExtra("reservaId", -1);
        String paciente = getIntent().getStringExtra("paciente");
        String doctor = getIntent().getStringExtra("doctor");
        String especialidad = getIntent().getStringExtra("especialidad");
        String fecha = getIntent().getStringExtra("fecha");
        String hora = getIntent().getStringExtra("hora");
        String estado = getIntent().getStringExtra("estado");

        // Mostrar datos en pantalla
        txtPaciente.setText("Paciente: " + paciente);
        txtDoctor.setText("Doctor: " + doctor);
        txtEspecialidad.setText("Especialidad: " + especialidad);
        txtFechaHora.setText("Fecha: " + fecha + " - " + hora);
        txtEstado.setText("Estado: " + estado);

        // Generar Contenido del QR (Opción B: ID + Resumen)
        String qrContent = "CITAYA-RESERVA-" + reservaId + "\n" +
                           "Paciente: " + paciente + "\n" +
                           "Fecha: " + fecha;

        // Generar QR dinámicamente
        try {
            Bitmap bitmap = generateQrCode(qrContent);
            imgQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            Toast.makeText(this, "Error al generar código QR", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private Bitmap generateQrCode(String text) throws WriterException {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, 512, 512);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        for (int x = 0; width > x; x++) {
            for (int y = 0; height > y; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }
}
