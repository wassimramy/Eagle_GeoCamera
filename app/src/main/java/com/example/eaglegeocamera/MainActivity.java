package com.example.eaglegeocamera;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    public LocationManager locationManager;
    public Location location;
    public ItemDAO dao;
    public Marker marker;
    public List<Item> list = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupLocation();
        setupMapFragment();

        dao = ((BaseApplication) getApplication()).getDatabase().itemDAO(); //retrieve the database information from the BaseApplication class to use the Data Access Object variable DAO
        list =  dao.getAll();
    }

    public void populateMarkers (){
        if (list != null){
            for (int i = 0 ; i < list.size() ; i++){
                LatLng currentLocation = new LatLng(list.get(i).itemLatitude, list.get(i).itemLongitude);
                marker = mMap.addMarker(new MarkerOptions().position(currentLocation).title("Hey"));
            }
        }

    }

    public void setupMapFragment (){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    public void setupLocation (){
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }

    public void showMaps(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void showCamera(View view) {
        Intent intent = new Intent(this, CameraActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        onLocationChanged(location);
        populateMarkers();
    }

    @Override
    public void onLocationChanged(Location location) {

        double lng = location.getLongitude() , lat = location.getLatitude();

        // Add a marker in current location and move the camera
        LatLng currentLocation = new LatLng(lat, lng);
        //mMap.addMarker(new MarkerOptions().position(currentLocation).title("Marker in Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLocation));
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


    @Override
    public boolean onMarkerClick(final Marker marker) {

        Log.d("Main Activity", "Click Detected");
            Intent intent = new Intent(this, ShowPicturesActivity.class);
            intent.putExtra("Latitude", marker.getPosition().latitude);
            intent.putExtra("Longitude", marker.getPosition().longitude);
            startActivity(intent);

        return false;
    }

}
