package com.example.michaelmarsicocsfair;

import static android.view.Window.FEATURE_NO_TITLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;

import com.example.michaelmarsicocsfair.pathfinding.ConfigActivity;

public class MainActivity extends AppCompatActivity {

    /* TODO:
        - The user will choose a room to get to
        - Constantly scan the user every 15 seconds for position
        - Update their position on a map based off of the location returned
        - This map would be a picture of Lane Tech floor plan
        - Also on each update it will make a path for the user to follow
        - They follow that path to get to their desired location
        - Now pathfinding is going to be the hardest part
    */

    private final String[] permissions = new String[] {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                Intent toActivity = new Intent(MainActivity.this, ConfigActivity.class);
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