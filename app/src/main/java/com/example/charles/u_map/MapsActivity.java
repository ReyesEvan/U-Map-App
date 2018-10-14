package com.example.charles.u_map;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private final float DEFAULT_ZOOM = 17.5f;
    private final int LOCATION_UPDATE_INTERVAL = 3000;

    private GoogleMap mMap;
    private FusedLocationProviderClient userLocation;
    private Intent in;
    private Handler handler;
    private Runnable runnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        in = getIntent();
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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

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


    @Override
    public void onResume(){
        super.onResume();
        startUserRunnable();
    }

}
