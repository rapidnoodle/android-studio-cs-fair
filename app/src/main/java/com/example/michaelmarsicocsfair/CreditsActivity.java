package com.example.michaelmarsicocsfair;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class CreditsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        Button returnHome = findViewById(R.id.returnHome);
        returnHome.setOnClickListener(view -> finish());
    }
}