package com.example.charles.u_map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;

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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleMap.OnPolylineClickListener{

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
    private Marker roomMarker;
    private ArrayList<PolyLineData> mPolyLines= new ArrayList<>();

    private String area;
    private String room;

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
}
