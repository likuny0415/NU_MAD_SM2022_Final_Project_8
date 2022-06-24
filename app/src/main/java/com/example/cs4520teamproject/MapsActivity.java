package com.example.cs4520teamproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, GoogleMap.OnMarkerDragListener {
    private final String TAG = "Demo";
    private final int PERMISSION_CODE = 0x001;
    private static final int REQUEST_GET_MAP_LOCATION = 0;

    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mGoogleMap;
    private Geocoder geocoder;
    private Address finalAddress;
    private String finalDestination;

    private EditText editTextSearch;
    private Button buttonSearch, buttonSave;
    private RelativeLayout searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setTitle("Find destination");

        editTextSearch = findViewById(R.id.editTextMapsSearch);
        buttonSearch = findViewById(R.id.buttonMapsSearch);
        buttonSave = findViewById(R.id.buttonMapsSave);
        searchBar = findViewById(R.id.mapsSearchBar);


        buttonSearch.setOnClickListener(this);
        buttonSave.setOnClickListener(this);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        geocoder = new Geocoder(this);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);




    }

    private void displayLocation(double longtitude, double latitude) {
        LatLng curPos = new LatLng(latitude, longtitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 18));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mGoogleMap.setOnMarkerDragListener(this);


        if (getIntent() != null && getIntent().getExtras() != null) {
            int type = getIntent().getIntExtra("type", 0);

            if (type == 2) {
                double longtitude = getIntent().getDoubleExtra("longitude", 0);
                double latitude = getIntent().getDoubleExtra("latitude", 0);
                LatLng latLng = new LatLng(latitude, longtitude);
                TaskShowLocation myTask = new TaskShowLocation(this, mGoogleMap, geocoder);
                try {
                    myTask.execute(new LatLng[]{latLng}).get();
                    searchBar.setVisibility(View.GONE);
                    buttonSave.setVisibility(View.GONE);
                    setTitle("Destination");
                } catch (ExecutionException | InterruptedException e) {

                }
            }
        }
    }


    @Override
    protected void onStart() {
        super.onStart();


        Boolean locationAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (locationAllowed) {
            getLastLocation();
        } else {
            askLocationPermission();
        }
    }

    private void goThisLocation(double longti, double lati) {
        LatLng curPos = new LatLng(lati, longti);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 18));
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        Task<Location> locationTask = fusedLocationClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng curPos = new LatLng(location.getLatitude(), location.getLongitude());
                    mGoogleMap.setMyLocationEnabled(true);
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curPos, 18));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });
    }

    private void askLocationPermission() {
        Boolean locationAllowed = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        if (locationAllowed) {
            boolean shouldShowPermission = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
            if (shouldShowPermission) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                }, PERMISSION_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {

            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonMapsSearch:
                fetchLocation();
                break;
            case R.id.buttonMapsSave:
                saveLocation();
                break;
        }
    }

    private void saveLocation() {
        if (finalAddress != null) {
            Intent toCreateGroup = new Intent(this, CreateGroupActivity.class);
            toCreateGroup.putExtra("address", finalAddress);
            toCreateGroup.putExtra("destination", finalDestination);
            setResult(RESULT_OK, toCreateGroup);
            finish();
        }
    }

    private void fetchLocation() {
        String destination = editTextSearch.getText().toString();
        TaskFindAddressByName myTask = new TaskFindAddressByName(this, mGoogleMap, geocoder);

        try {
            Address result = myTask.execute(new String[]{destination}).get();
            if (result != null) {
                finalAddress = result;
                finalDestination = destination;
            }
        } catch (ExecutionException | InterruptedException e) {

        }
    }

    @Override
    public void onMarkerDrag(@NonNull Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(@NonNull Marker marker) {
        LatLng latLng = marker.getPosition();
        TaskFindAddressByLocation myTask = new TaskFindAddressByLocation(this, mGoogleMap, geocoder);
        try {
            Address result = myTask.execute(new LatLng[]{latLng}).get();
            if (result != null) {
                finalAddress = result;
                marker.setTitle(result.getThoroughfare());
                finalDestination = result.getThoroughfare();
            }
        } catch (ExecutionException | InterruptedException e) {

        }
    }

    @Override
    public void onMarkerDragStart(@NonNull Marker marker) {

    }

    private static class TaskFindAddressByName extends AsyncTask<String, Void, Address> {

        private WeakReference<MapsActivity> activityReference;
        private GoogleMap mGoogleMap;
        private Geocoder geocoder;

        // only retain a weak reference to the activity
        TaskFindAddressByName(MapsActivity context, GoogleMap mGoogleMap, Geocoder geocoder) {
            activityReference = new WeakReference<>(context);
            this.mGoogleMap = mGoogleMap;
            this.geocoder = geocoder;
        }

        @Override
        protected Address doInBackground(String... strings) {
            try {
                List<Address> addresses = geocoder.getFromLocationName(strings[0], 1);
                if (addresses.size() > 0)
                    return addresses.get(0);
            } catch (IOException ex) {

            }
            return null;
        }


        @Override
        protected void onPostExecute(Address address) {
            if (address == null) {
                Toast.makeText(activityReference.get(), "Enter a valid position!", Toast.LENGTH_SHORT).show();
            } else {
                LatLng curPos = new LatLng(address.getLatitude(), address.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(curPos).title(address.getThoroughfare()).draggable(true));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 16));
            }
        }

    }

    private static class TaskFindAddressByLocation extends AsyncTask<LatLng, Void, Address> {

        private WeakReference<MapsActivity> activityReference;
        private GoogleMap mGoogleMap;
        private Geocoder geocoder;

        // only retain a weak reference to the activity
        TaskFindAddressByLocation(MapsActivity context, GoogleMap mGoogleMap, Geocoder geocoder) {
            activityReference = new WeakReference<>(context);
            this.mGoogleMap = mGoogleMap;
            this.geocoder = geocoder;
        }

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            try {
                List<Address> addresses = geocoder.getFromLocation(latLngs[0].latitude, latLngs[0].longitude, 1);
                if (addresses.size() > 0)
                    return addresses.get(0);
            } catch (IOException ex) {

            }
            return null;
        }


        @Override
        protected void onPostExecute(Address address) {
            if (address == null) {
                Toast.makeText(activityReference.get(), "Enter a valid position!", Toast.LENGTH_SHORT).show();
            } else {
                LatLng curPos = new LatLng(address.getLatitude(), address.getLongitude());
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 16));
            }
        }
    }

    private static class TaskShowLocation extends AsyncTask<LatLng, Void, Address> {

        private WeakReference<MapsActivity> activityReference;
        private GoogleMap mGoogleMap;
        private Geocoder geocoder;

        // only retain a weak reference to the activity
        TaskShowLocation(MapsActivity context, GoogleMap mGoogleMap, Geocoder geocoder) {
            activityReference = new WeakReference<>(context);
            this.mGoogleMap = mGoogleMap;
            this.geocoder = geocoder;
        }

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            try {
                List<Address> addresses = geocoder.getFromLocation(latLngs[0].latitude, latLngs[0].longitude, 1);
                if (addresses.size() > 0)
                    return addresses.get(0);
            } catch (IOException ex) {

            }
            return null;
        }


        @Override
        protected void onPostExecute(Address address) {
            if (address == null) {
                Toast.makeText(activityReference.get(), "Enter a valid position!", Toast.LENGTH_SHORT).show();
            } else {
                LatLng curPos = new LatLng(address.getLatitude(), address.getLongitude());
                mGoogleMap.addMarker(new MarkerOptions().position(curPos).title(address.getThoroughfare()));
                mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curPos, 16));
            }
        }
    }
}

