package com.example.michaelmarsicocsfair.pathfinding;

import static android.view.Window.FEATURE_NO_TITLE;
import static com.example.michaelmarsicocsfair.pathfinding.ConfigActivity.mapActivity;
import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.example.michaelmarsicocsfair.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    /**
     * Debugging. TAG for your Log calls
     */
    private static final String TAG = "MapsActivity";

    private static Context context;

    /**
     *  In this version there are only one level,
     *  also there is no buttons to change level,
     *  but you have the basics to build level
     *  selector
     */
    private static Integer selectedLevel = 1;

    public String[] mapData;

    private Polyline pathPolyline;
    private ArrayList<Circle> circleList;

    /**
     * Path, nodes and edges
     * These will contain everything needed for
     * path calculation
     */
    private static List nodeList;
    private static Edge[] edges;
    private ArrayList<Integer> path;

    /**
     * Google Maps stuff
     */
    private static GoogleMap mMap;
    private static final LatLng startLatLng = new LatLng(47.524936, 19.045960); // could be anything, this will be the reference
                                                                                       // for everything. Doesn't matter if using blank
                                                                                       // map
        /**
         * MAP OVERLAY
         */
        private static GroundOverlay mapOverlay;
        private static GroundOverlayOptions groundOverlayOptions;


        private static final int overlayImageWidth = 1024; //on newer devices both width and height should be powers of 2
        private static final int overlayImageHeight = 1024;//set these for the size of the overlay image in pixels.

        private static LatLngBounds bounds;

    /**
     * APP behaviour.
     *
     * You need this for device rotation, because
     * when you do that it have to draw the path
     * again.
     */
    private static Boolean savedInstance = false;

    /**
     * This is where you save data you want to keep
     * in case of reload
     * @param outState
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("levelNow", selectedLevel);
        outState.putIntegerArrayList("pathNow", path);
    }


    /**
     * Start the activity
     * @param savedInstanceState
     */
    @SuppressLint({"" +
            "lity", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
         * get rid of the bar
         */
        requestWindowFeature(FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        MapsActivity.context = getApplicationContext();
        circleList = new ArrayList<>();

        /**
         * if this is not the first load (device rotated for example) we
         * load the saved instance state. For example we save the
         * calculated path so no need calculating it again.
         */
        if (savedInstanceState != null) {
            selectedLevel = savedInstanceState.getInt("levelNow");
            path = savedInstanceState.getIntegerArrayList("pathNow");
            savedInstance = true;
        }

        /**
         * We load the nodes and edges, they are
         * csv files in the raw folder
         *
         * nodes.csv
         *
         * Example: 1,1412,1673,Node Name,1,1
         *
         * (ID,posX,posY,Node Name,level,importance)
         *
         */
        InputStream inputStreamNodes = getResources().openRawResource(R.raw.nodes);
        new CSVParse(inputStreamNodes);
        nodeList = CSVParse.read();

        /**
         * edges.csv
         *
         * Example: 1,2,121,3,Edge name
         *
         * (id,from node,to node,length,Edge name)
         *
         */
        InputStream inputStreamEdges = getResources().openRawResource(R.raw.edges);
        new CSVParse(inputStreamEdges);
        edges = CSVParse.readEdge();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        /**
         * Back button
         */
        Button buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    backToMenu();
                }
                return false;
            }

        });
    }


    /**
     * Memory check
     */
    private ActivityManager.MemoryInfo getAvailableMemory(){
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }


/**
 * Custom methods
 */
    /**
     * Back to main activity
     */
    public void backToMenu() {
        Intent intent = new Intent(this, ConfigActivity.class);
        this.finish();
        startActivity(intent);
    }

    /**
     * Change Maps Overlay
     */
    private static void setOverlay() {


        /**
         * for bigger maps consider using tiles instead.
         * point drawing based on pixels, so you have
         * to write down pixels on a full image, then
         * make tiles from it.
         */
        Bitmap bitmap = null;
        BitmapFactory.Options ops = new BitmapFactory.Options();
        switch (selectedLevel) {
            //If you have more than one levels, you can add more "case" here
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.lane_tech_floorplans);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + selectedLevel);
        }

        ops.inSampleSize = 10; // help device not to eat memory

        groundOverlayOptions.image(BitmapDescriptorFactory.fromBitmap(bitmap));
        mapOverlay = mMap.addGroundOverlay(groundOverlayOptions);

        bitmap.recycle();

        /**
         * //this is another method for the image. This works too. But seems slower
         BitmapDescriptor image = null;
         switch (selectedLevel) {
            case 1:
                image = BitmapDescriptorFactory.fromResource(R.drawable.maps_f1);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + selectedLevel);
         }

         customMapOverlay.image(image);
         mapOverlay = mMap.addGroundOverlay(customMapOverlay);

         */


    }

    private void deletePolylines() {
        if (pathPolyline != null) pathPolyline.remove();
        for (Circle circle : circleList) {
            circle.remove();
        }
    }

    /**
     * draw overlay, points (nodes) and lines (edges)
     */
    private void drawOnLevel() {

        deletePolylines();

        int nodeID;

        /**
         * Set up lines and points
         */
        PolylineOptions pathLineOptions = new PolylineOptions();
        CircleOptions pathCircleOptions = new CircleOptions();

        new latLngMapper(overlayImageWidth, overlayImageHeight, bounds);

        for (int pathNumber = 0; pathNumber < path.size(); pathNumber++) {

            nodeID = path.get(pathNumber);
            String[] nodeNow = (String[]) nodeList.get(nodeID-1);
            //Log.v(TAG, "X=" + nodeNow[1] + " Y=" + nodeNow[2]);

            /**
             * only draw points that are on the level selected
             */
            if (selectedLevel == parseInt(nodeNow[4])) {

                int col = getResources().getColor(R.color.blue);

                /**
                 *  all the nodes have a name that you can show by touching them
                 */
                String tag = nodeNow[3];

                /**
                 * The starting point is red. The path is backwards in the list
                 * so we color tha last one
                 */
                if (pathNumber == path.size() - 1) {
                    col = Color.RED;
                    tag = getText(R.string.startPoint) + tag;
                }

                pathCircleOptions.center(latLngMapper.calculateLatLng(parseInt(nodeNow[1]), parseInt(nodeNow[2])))
                        .radius(15).strokeWidth(0)
                        .fillColor(col)
                        .zIndex(1000)
                        .clickable(true);
                Circle circle = mMap.addCircle(pathCircleOptions);
                circleList.add(circle);
                circle.setTag(tag);

                mMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {

                    @Override
                    public void onCircleClick(Circle circle) {

                        DisplayMetrics dM = new DisplayMetrics();
                        getWindowManager().getDefaultDisplay().getMetrics(dM);

                        Toast placeToast = Toast.makeText(getApplicationContext(), (String) circle.getTag(), Toast.LENGTH_LONG);

                        placeToast.setGravity(Gravity.CENTER_HORIZONTAL, 0, -(dM.heightPixels / 2 / 2)); //so it is a little higher than middle of the screen
                        placeToast.show();

                    }
                });

                pathLineOptions.add(
                        latLngMapper.calculateLatLng(parseInt(nodeNow[1]), parseInt(nodeNow[2]))
                )
                        .color(getResources().getColor(R.color.blue_dark))
                        .zIndex(500);//you need z-index so it isn't behind the overlay


            }

        }

        pathPolyline = mMap.addPolyline(pathLineOptions);

    }

    /**
     * If you make level buttons or any other way to change level
     * you can use this function to change the image.
     * @param sLevel
     */
    private void levelButtonPress(int sLevel) {

        mMap.clear(); // this is very important. without this crashes are many

        selectedLevel = sLevel;

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        if(!memoryInfo.lowMemory) {
            setOverlay();
        } else {
            Toast.makeText(getApplicationContext(), getText(R.string.error_memory),Toast.LENGTH_LONG);
        }

        drawOnLevel();
    }

    public void setCurrentPath(String fromTag, String toTag) {
        /**
         * If we have no saved instance this is the first load
         * so we have to calculate everything.
         */
        if (!savedInstance) {
            Graph g = new Graph(edges);

            try {
                g.setUpEdgeLengths(new ArrayList());
                g.calculateShortestDistances(parseInt(fromTag), parseInt(toTag));
                g.calculatePath();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), getString(R.string.error_path), Toast.LENGTH_SHORT).show();
            }

            path = g.getPath();

            int pathEndNodeID = path.get(path.size() - 1) - 1;
            String[] pathEndNode = (String[]) nodeList.get(pathEndNodeID);
            selectedLevel = Integer.parseInt(pathEndNode[4]);
        }

        drawOnLevel();
    }

    public void updatePositionOnMap(String newPos) {
        deletePolylines();
        mapData[0] = newPos;
        setCurrentPath(mapData[0], mapData[1]);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        /**
         * Map ini
         */
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.setMinZoomPreference(13);
        mMap.setMaxZoomPreference(20);


        groundOverlayOptions = new GroundOverlayOptions()
                .position(startLatLng, overlayImageWidth, overlayImageHeight);


        /**
         * we get the data from main activity
         */
        Intent intent = getIntent();
        String message = intent.getStringExtra(mapActivity);

        mapData = message.split(";");

        ArrayList<String> edgeAvoid = new ArrayList(); //edges we want to avoid

        if (mapData.length > 2) {
            for (int i = 2; i < mapData.length; i++) {
                edgeAvoid.add(mapData[i]);
            }
        }

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();
        if(!memoryInfo.lowMemory) {
            setOverlay();
            bounds = mapOverlay.getBounds();
            mMap.setLatLngBoundsForCameraTarget(bounds);
            setCurrentPath(mapData[0], mapData[1]);
        } else {
            Toast.makeText(getApplicationContext(), getText(R.string.error_memory),Toast.LENGTH_LONG);
        }

    }
}