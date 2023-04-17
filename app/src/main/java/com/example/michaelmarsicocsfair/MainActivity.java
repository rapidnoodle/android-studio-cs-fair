package com.example.michaelmarsicocsfair;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final String[] permissions = new String[] {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button findMyClassroom = findViewById(R.id.findMyClassroom);
        Button howItWorks = findViewById(R.id.howItWorks);
        Button credits = findViewById(R.id.allowAccess);

        findMyClassroom.setOnClickListener(view -> {
            if (!hasPermissions(permissions)) {
                Intent toActivity = new Intent(MainActivity.this, RequestPermissionsActivity.class);
                toActivity.putExtra("permissions", permissions);
                startActivity(toActivity);
            } else {
                Intent toActivity = new Intent(MainActivity.this, MapActivity.class);
                startActivity(toActivity);
            }
        });

        howItWorks.setOnClickListener(view -> {
            Intent toActivity = new Intent(MainActivity.this, TutorialActivity.class);
            startActivity(toActivity);
        });

        credits.setOnClickListener(view -> {
            Intent toActivity = new Intent(MainActivity.this, CreditsActivity.class);
            startActivity(toActivity);
        });
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions)
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission)
                    == PackageManager.PERMISSION_DENIED) return false;
        return true;
    }
}