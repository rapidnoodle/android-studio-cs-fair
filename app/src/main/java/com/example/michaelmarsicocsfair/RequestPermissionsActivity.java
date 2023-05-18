package com.example.michaelmarsicocsfair;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.michaelmarsicocsfair.pathfinding.ConfigActivity;

public class RequestPermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permissions);

        Intent getPermissions = getIntent();
        String[] permissions = getPermissions.getStringArrayExtra("permissions");

        Button requestPermissions = findViewById(R.id.allowAccess);

        requestPermissions.setOnClickListener(view -> ActivityCompat.requestPermissions(
                RequestPermissionsActivity.this,
                permissions,
                011));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 011) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent toActivity = new Intent(RequestPermissionsActivity.this, ConfigActivity.class);
                startActivity(toActivity);
            } else {
                Toast.makeText(RequestPermissionsActivity.this, "Location Permissions Failed! Please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }
}