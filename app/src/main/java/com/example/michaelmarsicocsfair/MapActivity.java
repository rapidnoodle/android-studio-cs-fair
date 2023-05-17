package com.example.michaelmarsicocsfair;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.michaelmarsicocsfair.wifips.ScanBackground;

public class MapActivity extends AppCompatActivity {

    // PLAN:
    // The user will choose a room to get to
    // Constantly scan the user every 15 seconds for position
    // Update their position on a map based off of the location returned
    // This map would be a picture of Lane Tech floor plan
    // Also on each update it will make a path for the user to follow
    // They follow that path to get to their desired location
    // Now pathfinding is going to be the hardest part

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        ScanBackground wifips = new ScanBackground(getApplicationContext());
        wifips.execute();
    }
}