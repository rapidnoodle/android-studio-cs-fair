package com.example.michaelmarsicocsfair.pathfinding;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.michaelmarsicocsfair.R;
import com.example.michaelmarsicocsfair.wifips.ScanBackground;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigActivity extends AppCompatActivity {
    private static final String TAG = "ConfigActivity";

    public static final String mapActivity = "com.example.michaelmarsicocsfair.pathfinding.DATA";

    private Spinner fromWhere;
    private Spinner toWhere;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config);

        /**
         *   WifiPS init (Maybe put in MapsActivity?)
         */
        ScanBackground wifips = new ScanBackground(getApplicationContext());
        wifips.execute();

        /**
         *   GUI init
         */
        fromWhere = findViewById(R.id.fromWhere);
        toWhere = findViewById(R.id.toWhere);

        Button startButton = findViewById(R.id.startButton);

        /**
         *  spinners
         */
        List<StringWithTag> listFrom = new ArrayList<StringWithTag>();
        List<StringWithTag> listTo = new ArrayList<StringWithTag>();


        /**
         * We load the nodes and edges, they are
         * csv files in the raw folder
         *
         * this is a list of nodes that we want as starting points
         * preferably we should list many of them with names.
         *
         * menu_from_nodes.csv
         *
         * Example: 1,1412,1673,Node Name,1,1
         *
         * (ID,posX,posY,Node Name,level,importance)
         *
         */
        InputStream inputStreamFromNodes = getResources().openRawResource(R.raw.menu_from_nodes);
        new CSVParse(inputStreamFromNodes);
        List fromNodeList = CSVParse.read();

        listFrom.add(new StringWithTag(getString(R.string.dropdownFromWhere), "0"));

        for (Integer i = 0; i < fromNodeList.size(); i++) {
            String[] node = (String[]) fromNodeList.get(i);
            listFrom.add(new StringWithTag(node[3], node[0]));
        }

        InputStream inputStreamToNodes = getResources().openRawResource(R.raw.menu_to_nodes);
        new CSVParse(inputStreamToNodes);
        List toNodeList = CSVParse.read();

        listTo.add(new StringWithTag(getString(R.string.dropdownToWhere), "0"));

        for (Integer i = 0; i < toNodeList.size(); i++) {
            String[] node = (String[]) toNodeList.get(i);
            listTo.add(new StringWithTag(node[3], node[0]));
        }

        ArrayAdapter<StringWithTag> adapterFrom = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listFrom);

        listTo.set(0, new StringWithTag(getString(R.string.dropdownToWhere), "0"));

        ArrayAdapter<StringWithTag> adapterTo = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, listTo);

        fromWhere.setAdapter(adapterFrom);
        toWhere.setAdapter(adapterTo);

        /**
         *  Start "gomb beállítása" (Google Translation: Button Setting)
         */
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    goToMap();
                }
                return false;
            }
        });
    }

    public void goToMap() {
        StringWithTag fromSelected = (StringWithTag) fromWhere.getSelectedItem();
        StringWithTag toSelected = (StringWithTag) toWhere.getSelectedItem();

        if (fromSelected.tag == "0" || toSelected.tag == "0") {
            Toast.makeText(getApplicationContext(), getString(R.string.error_nothing_selected), Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(mapActivity, fromSelected.tag + ";" + toSelected.tag + ";");
            this.finish();
            startActivity(intent);
        }
    }
}