package com.example.charles.u_map;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charles.u_map.utils.ViewWeightAnimationWrapper;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import static android.hardware.SensorManager.SENSOR_DELAY_FASTEST;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener, View.OnClickListener, SensorEventListener {

    private final float DEFAULT_ZOOM = 17.5f;
    private final int LOCATION_UPDATE_INTERVAL = 8000;

    private String TAG = "CALCULATE_DIRECTIONS";
    private GoogleMap mMap;
    private FusedLocationProviderClient userLocation;
    private Intent in;
    private Handler handler;
    private Runnable runnable;

    private GeoApiContext mGeoApiContext = null;
    private Location currentStudentLocation;
    private LatLng targetLocation;
    private Marker roomMarker;
    private ArrayList<PolyLineData> mPolyLines= new ArrayList<>();

    private String area;
    private String room;

    /* ----------- RESOURCES REQUIRED FOR SHOWING THE DIRECTIONAL VECTOR ------------ */
    ImageView iv;

    private static SensorManager sensorService;
    private Sensor sensor;

    private float currentDegree = 0;


    /* ------------ RESOURCES REQUIRED FOR EXPANDING - CONTRACTING ANIMATIONS FOR THIS VIEW --------- */

    private static final int MAP_LAYOUT_STATE_CONTRACTED = 0;
    private static final int MAP_LAYOUT_STATE_EXPANDED = 1;
    private int mMapLayoutState = 0;
    private RelativeLayout   mMapContainer;
    private ConstraintLayout compassView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        in = getIntent();

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder().apiKey(getString(R.string.google_api_key)).build();
        }

        area = in.getStringExtra("com.example.charles.u_map.AREA");
        room = in.getStringExtra("com.example.charles.u_map.CLASSROOM");

        //getting the image that'll be rotated to show the direction
        iv = findViewById(R.id.arrow);


        sensorService = (SensorManager)  getSystemService(Context.SENSOR_SERVICE);

        //getting the orientation sensor to detect the changes of the phone
        sensor = sensorService.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        //finding the button that'll be used for contracting/expanding the views
        findViewById(R.id.btn_full_screen_map).setOnClickListener(this);

        //finding the both views used: map one and compass one
        mMapContainer = findViewById(R.id.map_container);
        compassView = findViewById(R.id.compass_container);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnPolylineClickListener(this);
        try {
            getRoomMarker();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (in.getBooleanExtra("com.example.charles.u_map.LOCATION_PERMISSION", true)) {
            startUserLocation();

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);

        }
    }

    private void getRoomMarker() throws SQLException {
        DataBase db = new DataBase();
        ResultSet result;
        LatLng position;

        result = db.makeQuery("SELECT Latitud,Longitud FROM Destinos WHERE CONVERT(VARCHAR,Edificio) = '" + area + "' AND Salon = " + room);

        position = new LatLng(result.getDouble("Latitud"), result.getDouble("Longitud"));

        roomMarker = mMap.addMarker(new MarkerOptions().position(position).title(area + " " + room).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        targetLocation = position;

        db.closeConnection();
    }

    private void startUserLocation(){

        userLocation = LocationServices.getFusedLocationProviderClient(this);

        try {
            boolean hasPermission = in.getBooleanExtra("com.example.charles.u_map.LOCATION_PERMISSION", true);
            if(hasPermission){

                Task location = userLocation.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentUserLocation = (Location) task.getResult();
                            currentStudentLocation = currentUserLocation;
                            calculateDirections();
                            moveCameraZoom(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()), DEFAULT_ZOOM);
                        }
                    }
                });
            }

        }catch (SecurityException e){

        }
    }

    private void getUserLocation(){

        userLocation = LocationServices.getFusedLocationProviderClient(this);

        try {
            boolean hasPermission = in.getBooleanExtra("com.example.charles.u_map.LOCATION_PERMISSION", true);
            if(hasPermission){

                Task location = userLocation.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if(task.isSuccessful()){
                            Location currentUserLocation = (Location) task.getResult();
                            currentStudentLocation = currentUserLocation;
                            moveCamera(new LatLng(currentUserLocation.getLatitude(), currentUserLocation.getLongitude()));
                        }
                    }
                });
            }

        }catch (SecurityException e){
        }
    }


    private void moveCameraZoom(LatLng lat, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat, zoom));


    }

    private void moveCamera(LatLng lat){
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lat));
    }

    private void startUserRunnable(){
        handler = new Handler();
        handler.postDelayed(runnable = new Runnable() {
            @Override
            public void run() {
                getUserLocation();
                handler.postDelayed(runnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }


    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            @Override
            public void run() {
                Log.d("RUN_RESULT_ROUTES", "run: result routes: " + result.routes.length);

                if(mPolyLines.size() > 0){
                    for(PolyLineData polylineData: mPolyLines){
                        polylineData.getPolyline().remove();
                    }
                    mPolyLines.clear();
                    mPolyLines = new ArrayList<>();
                }


                for(DirectionsRoute route: result.routes){
                    Log.d("RUN_LEG", "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setClickable(true);
                    mPolyLines.add(new PolyLineData(polyline,route.legs[0]));
                    polyline.setColor(R.color.colorPrimary);
                    onPolylineClick(polyline);
                    //zoomRoute(polyline.getPoints());
                }
            }
        });
    }


    private void calculateDirections(){
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                roomMarker.getPosition().latitude,
                roomMarker.getPosition().longitude
        );
        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        currentStudentLocation.getLatitude(),
                        currentStudentLocation.getLongitude()

                )
        );
        Log.d(TAG, "UserLocation: " + currentStudentLocation.getLatitude() + " " + currentStudentLocation.getLongitude());
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        startUserRunnable();

        if (sensor != null)
            sensorService.registerListener(this, sensor, SensorManager.SENSOR_DELAY_FASTEST);
        else
            Toast.makeText(this, "Not supported!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorService.unregisterListener(this);
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        for(PolyLineData polylineData: mPolyLines){
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(R.color.colorPrimaryDark);
                polylineData.getPolyline().setZIndex(1);
            }
            else{
                polylineData.getPolyline().setColor(Color.DKGRAY);
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }


    /* CUSTOM COMPASS LOGIC */

    /**
     * Calculate the azimuth between two geographic coordinates
     * The Azimuth is calculated with the formula:
     * AZIMUTH = ARCTAN2[(sin Δλ ⋅ cos φ₂), (cos φ₁ ⋅ sin φ₂ − sin φ₁ ⋅ cos φ₂ ⋅ cos Δλ)]
     * @param phi1      - the initial latitude  [DEGREES]
     * @param lambda1   - the initial longitude [DEGREES]
     * @param phi2      - the final latitude    [DEGREES]
     * @param lambda2   - the final longitude   [DEGREES]
     * @return the azimuth [IN DEGREES] between the geographic coordinates (φ1, λ1) and (φ2, λ2)
     */
    private static double getAzimuth(double phi1, double lambda1, double phi2, double lambda2) {
        double azimuth;

        double deltaLambda = Math.toRadians(lambda2 - lambda1);

        double sinDeltaLambda = Math.sin(deltaLambda);
        double cosDeltaLambda = Math.cos(deltaLambda);

        double sinPhi1 = Math.sin(Math.toRadians(phi1));
        double sinPhi2 = Math.sin(Math.toRadians(phi2));
        double cosPhi1 = Math.cos(Math.toRadians(phi1));
        double cosPhi2 = Math.cos(Math.toRadians(phi2));


        double x = sinDeltaLambda * cosPhi2;
        double y = cosPhi1 * sinPhi2 - sinPhi1 * cosPhi2 * cosDeltaLambda;

        azimuth = Math.atan2(x, y);

        return Math.toDegrees(azimuth);
    }

    /**
     * Every time the sensor changes, the current and target location and the azimuth between them are recalculated
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {

        System.out.println("Degree:: " + -degree + "/" + 0x00B0);


        //If some of the required locations isn't available, the method cannot be performed
        if (currentStudentLocation == null || targetLocation == null)
            return;

        int degree = Math.round(event.values[0]);

        double lat1 = currentStudentLocation.getLatitude();
        double lon1 = currentStudentLocation.getLongitude();

        double lat2 = targetLocation.latitude;
        double lon2 = targetLocation.longitude;

        double azimuth = getAzimuth(lat1, lon1, lat2, lon2);


        //int degreeAz = degree + (int) azimuth + 0x00B0;

        int degreeAz;

        if (degree - (int) azimuth > 0)
            degreeAz = degree - (int) azimuth;
        else
            degreeAz = 360 + (degree - (int) azimuth);



        RotateAnimation ra = new RotateAnimation(currentDegree, -degreeAz, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        ra.setDuration(1000);
        ra.setFillAfter(true);

        iv.startAnimation(ra);

        currentDegree = -degreeAz;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    /* END OF CUSTOM COMPASS LOGIC */


    /* CONTRACT AND EXPAND MAP LOGIC */
    private void expandMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                70,
                100);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(compassView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                30,
                0);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    private void contractMapAnimation(){
        ViewWeightAnimationWrapper mapAnimationWrapper = new ViewWeightAnimationWrapper(mMapContainer);
        ObjectAnimator mapAnimation = ObjectAnimator.ofFloat(mapAnimationWrapper,
                "weight",
                100,
                70);
        mapAnimation.setDuration(800);

        ViewWeightAnimationWrapper recyclerAnimationWrapper = new ViewWeightAnimationWrapper(compassView);
        ObjectAnimator recyclerAnimation = ObjectAnimator.ofFloat(recyclerAnimationWrapper,
                "weight",
                0,
                30);
        recyclerAnimation.setDuration(800);

        recyclerAnimation.start();
        mapAnimation.start();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_full_screen_map: {

                if (mMapLayoutState == MAP_LAYOUT_STATE_CONTRACTED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_EXPANDED;
                    expandMapAnimation();
                } else if (mMapLayoutState == MAP_LAYOUT_STATE_EXPANDED) {
                    mMapLayoutState = MAP_LAYOUT_STATE_CONTRACTED;
                    contractMapAnimation();
                }
                break;
            }

        }
    }
}
