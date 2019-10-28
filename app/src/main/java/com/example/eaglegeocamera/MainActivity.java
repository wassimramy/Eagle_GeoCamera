package com.example.eaglegeocamera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "Location Services";
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private GoogleMap mMap;
    public FusedLocationProviderClient mFusedLocationClient;
    public Location location;
    public ItemDAO dao;
    public Marker currentLocationMarker;
    public Marker marker;
    public Item newItem;
    public File photoFile;
    public Uri photoURI;
    public int resultCamera;
    public List<Item> list = null;
    static final int REQUEST_TAKE_PHOTO = 1;
    String currentPhotoPath;
    public double longitude = 0, latitude = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        setupMapFragment();

        dao = ((BaseApplication) getApplication()).getDatabase().itemDAO(); //retrieve the database information from the BaseApplication class to use the Data Access Object variable DAO
        list =  dao.getAll();
    }

    public void populateMarkers (){
        if (list != null){
            for (int i = 0 ; i < list.size() ; i++){
                LatLng currentLocation = new LatLng(list.get(i).itemLatitude
                        , list.get(i).itemLongitude);
                marker = mMap.addMarker(new MarkerOptions().position(currentLocation));
                marker.setTitle("Location");
            }
        }
    }

    public String getPicturesCount(double itemLatitude, double itemLongitude) {
        int count = 0;
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i).itemLatitude == itemLatitude && list.get(i).itemLongitude == itemLongitude) {
                    count++;
                }
            }
        }
        if (count == 1) {
            return "A picture was taken here";
        } else if (count == 0) {
            return "No pictures were taken here";
        } else {
            return (count) + " pictures were taken here";
        }
    }

    public void setupMapFragment (){
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void showCamera(View view) {
        if (marker != null) {
            marker.hideInfoWindow();
        }
        dispatchTakePictureIntent();
        getLastLocation();
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.eaglegeocamera",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = UUID.randomUUID().toString();
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        resultCamera = resultCode;
        if (resultCode == RESULT_CANCELED) {
            photoFile.delete();
            Log.d("Camera Status", "File will be deleted since no picture was taken");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        populateMarkers();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (!checkPermissions()) {
            requestPermissions();
        } else {
            getLastLocation();
        }
    }

    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            location = task.getResult();
                            longitude = location.getLongitude();
                            latitude = location.getLatitude();
                            LatLng currentLocation = new LatLng(Math.round(latitude * 10000.0) / 10000.0,
                                    Math.round(longitude * 10000.0) / 10000.0);

                            if (currentLocationMarker != null) {
                                currentLocationMarker.remove();
                            }
                            MarkerOptions currentLocationMarkerOptions = new MarkerOptions().position(currentLocation);
                            currentLocationMarker = mMap.addMarker(currentLocationMarkerOptions);
                            currentLocationMarker.setPosition(currentLocation);
                            currentLocationMarker.setTitle("Current Location");
                            currentLocationMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                            if (resultCamera == RESULT_OK) {
                                newItem = new Item(photoFile.getName(), photoURI.toString(),
                                        currentLocationMarker.getPosition().longitude
                                        , currentLocationMarker.getPosition().latitude);

                                dao.insertAll(newItem);
                                setupMapFragment();
                                list = null;
                                list = dao.getAll();
                                resultCamera = RESULT_CANCELED;
                            }

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 17));
                        } else {
                            Log.w("Location Services", "getLastLocation:exception", task.getException());
                        }
                    }
                });
    }

    /**
     * Return the current state of the permissions needed.
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");
            startLocationPermissionRequest();

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionResult");
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i(TAG, "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted.
                getLastLocation();
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {

        if (!marker.getTitle().equals("Current Location")) {
            marker.setTitle(getPicturesCount(marker.getPosition().latitude
                    , marker.getPosition().longitude));
            Log.d("Main Activity", "Click Detected");
            Intent intent = new Intent(this, ShowPicturesActivity.class);
            intent.putExtra("Latitude", marker.getPosition().latitude);
            intent.putExtra("Longitude", marker.getPosition().longitude);
            startActivity(intent);
        }
        return false;
    }

}
