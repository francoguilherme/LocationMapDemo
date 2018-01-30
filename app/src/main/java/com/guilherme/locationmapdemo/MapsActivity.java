package com.guilherme.locationmapdemo;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private GoogleMap mMap;

    LocationManager locationManager;
    String locationProvider;
    Location location;
    LatLng coordinates;

    Boolean isPermissionGranted = false;
    String[] locationPermissions = {Manifest.permission.ACCESS_FINE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // This is only ran the first time the user opens the app, or ran again if the user denies permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, locationPermissions, 1);

        } else {

            // Permission was already granted
            isPermissionGranted = true;
        }

        // If permission hasn't been granted yet, don't even try getting provider or location, that would cause a crash
        if (!isPermissionGranted){
            return;
        }

        locationProvider = locationManager.getBestProvider(new Criteria(), false);
        location = locationManager.getLastKnownLocation(locationProvider);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // This method is ran after requesting permissions

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            isPermissionGranted = true;

            locationProvider = locationManager.getBestProvider(new Criteria(), false);

            try{

                location = locationManager.getLastKnownLocation(locationProvider);

            } catch (SecurityException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // This needs to be checked again because onResume() ir ran right after onCreate(), and before user can grant permissions
        if (!isPermissionGranted){
            return;
        }

        locationProvider = locationManager.getBestProvider(new Criteria(), false);

        try{

            locationManager.requestLocationUpdates(locationProvider, 400, 1, this);

        } catch (SecurityException e){
            e.printStackTrace();
        }

        if (mMap != null) {

            // This is needed because onResume() is called before the map is ready and mMap is set
            onLocationChanged(location);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {

        coordinates = new LatLng(location.getLatitude(), location.getLongitude());

        mMap.clear(); // Clears previous markers
        mMap.addMarker(new MarkerOptions().position(coordinates).title("My Location"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        if (location != null){

            onLocationChanged(location);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
