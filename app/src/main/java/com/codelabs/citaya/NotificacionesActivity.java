package com.codelabs.citaya;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class NotificacionesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }
}