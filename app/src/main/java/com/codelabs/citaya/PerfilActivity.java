package com.codelabs.citaya;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setDecorFitsSystemWindows(true);

        setContentView(R.layout.activity_perfil);

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }}
