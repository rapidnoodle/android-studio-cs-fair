package com.example.michaelmarsicocsfair.wifips;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;

/**
 * Created by Sony on 4/19/2015.
 */
public class ScanBackground extends AsyncTask<Void, Void, PositionData> {

    PositionData pos;
    private final int readingCount = 15;
    private int currentCount;
    public boolean canRun;
    public String currentPositionName;
    DatabaseHelper db;
    ArrayList<String> buildings;
    String building;
    WifiManager wifi;
    Timer timer;
    TimerTask myTimerTask;


    private Context context;

    public ScanBackground(Context context) {
        this.context = context;
        this.canRun = true;
        this.currentPositionName = "N/A";
        db = new DatabaseHelper(context);
        buildings = db.getBuildings();
        if (buildings.size() > 0) {
            building = buildings.get(0);
        } else {
            Log.d("MICHAEL", "DATABASE ERROR, NO BUILDINGS FOUND");
            this.canRun = false;
        }
    }

    @Override
    protected void onPostExecute(PositionData posi) {
        this.pos = posi;
    }

    public class ResultData {
        private Router router;

        public Router getRouter() {
            return this.router;
        }

        public List<Integer> values;

        public ResultData(Router router) {
            // TODO Auto-generated constructor stub
            this.router = router;
            values = new ArrayList<Integer>();
        }
    }

    private List<ResultData> resultsData;
    private List<PositionData> positionsData;
    private PositionData positionData;

    @Override
    protected PositionData doInBackground(Void... params) {
        Log.i("aaki", "doing");
        wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Log.d("MICHAEL", "WIFI NOT ENABLED, PLEASE CONNECT TO CPS GUEST");
            this.canRun = false;
        }

        if (!this.canRun) {
            Log.d("MICHAEL", "CANNOT SCAN LOCATION IN BACKGROUND");
            return null;
        }

        resultsData = new ArrayList<ResultData>();
        currentCount = 0;
        timer = new Timer();
        myTimerTask = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                refresh();
            }
        };
        timer.schedule(myTimerTask, 0, 1000);
        return null;
    }

    private void refresh() {
        // TODO Auto-generated method stub
        if (currentCount >= readingCount) {
            if (myTimerTask != null) {
                Log.d("MICHAEL", "SCAN COMPLETE");
                returnResults();
                resultsData = new ArrayList<ResultData>();
                currentCount = 0;
            }
        }
        currentCount++;
        Log.d("MICHAEL", "SCANNING: " + currentCount);
        wifi.startScan();
        List<ScanResult> results = wifi.getScanResults();
        for (int i = 0; i < results.size(); i++) {
            String ssid0 = results.get(i).SSID;
            String bssid = results.get(i).BSSID;

            int rssi0 = results.get(i).level;
            boolean found = false;
            for (int pos = 0; pos < resultsData.size(); pos++) {
                if (resultsData.get(pos).getRouter().getBSSID().equals(bssid)) {
                    found = true;
                    resultsData.get(pos).values.add(rssi0);
                    break;
                }
            }
            if (!found) {
                ResultData data = new ResultData(new Router(ssid0, bssid));
                data.values.add(rssi0);
                resultsData.add(data);
            }
        }

    }

    private void returnResults() {
        // TODO Auto-generated method stub

        positionData = new PositionData(null);
        for (int length = 0; length < resultsData.size(); length++) {

            int sum = 0;
            for (int l = 0; l < resultsData.get(length).values.size(); l++) {
                sum += resultsData.get(length).values.get(l);
            }
            int average = sum / resultsData.get(length).values.size();

            positionData.addValue(resultsData.get(length).getRouter(), average);
        }
        Set<String> keys = positionData.values.keySet();
        for(String i: keys) {
            Log.i("aaki", Integer.toString(positionData.values.get(i)));
        }
        positionsData = db.getReadings(building);

        String closestPosition = null;
        ArrayList<Router> wifis = db.getFriendlyWifis(building);

        int min_distance = positionData.uDistance(positionsData.get(0), wifis);
        int j = 0;
        closestPosition = positionsData.get(0).getName();
        String res = "";
        res += closestPosition + "\n" + min_distance;
        res += "\n" + positionsData.get(0).toString();
        for (int i = 1; i < positionsData.size(); i++) {
            int distance = positionData.uDistance(positionsData.get(i), wifis);
            res += "\n" + positionsData.get(i).getName() + "\n" + distance;
            res += "\n" + positionsData.get(i).toString();
            if (distance < min_distance) {
                min_distance = distance;
                j = i;
                closestPosition = positionsData.get(i).getName();
            }
        }

        if (min_distance == PositionData.MAX_DISTANCE){
            closestPosition = "N/A";
            Toast.makeText(context,"You are out of range of the selected building",Toast.LENGTH_LONG).show();
        }

        // BIG MONEY
        // TODO: Update the current location here
        currentPositionName = closestPosition;

        res += "\nCurrent:\n" + positionData.toString();
        Log.v("Result", res);
    }

}
