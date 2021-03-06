package com.app.travel.flare;

import android.Manifest;
import android.app.PendingIntent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.app.travel.flare.databinding.ActivityMapsBinding;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap mMap;
    private GeofencingClient geofencingClient;
    private GeofenceHelper geofenceHelper;

    private int FINE_LOCATION_ACCESS_REQUEST_CODE = 10011;
    private int BACKGROUND_LOCATION_ACCESS_REQUEST_CODE = 10012;

    private float GEOFENCE_RADIUS = 200;

    private String GEOFENCE_ID = "SOME_GEOFENCE_ID";

    private ActivityMapsBinding binding;
    private String cur_coordinates = "";

    //TRYING to trigger the API every 2 mins
    int INTERVAL = 1000 * 60 * 2; //2 minutes
    Handler mhandler = new Handler();

    AnimationDrawable animation;

    //Coordinated recieved from the API to be saved in this latlong datatype
    ArrayList<LatLng> geoCoordinates = new ArrayList<>();

    //ADDED HERE

    int LOCATION_REQUEST_CODE = 10001;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult == null) {
                return;
            }
            for (Location location : locationResult.getLocations()) {
                Log.d(TAG, "From the first one - onLocationResult: " + location.toString());
            }
        }
    };

    //ENDS HERE

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        //Added here
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //Ends here
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng effiel = new LatLng(48.8589, 2.29365);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(effiel, 16));

        enableUserLocation();

        mMap.setOnMapLongClickListener(this);

        tryAddingGeofenceS();

        //This itself can be in a runnable with a loading gif
        startRepeating();
    }

    public void startRepeating(){
        Thread thread = new Thread(runnable);
        thread.start();
        //runnable.run();
    }

    private Runnable runnable = new Runnable() {
        LatLng latLng;
        @Override
        public void run() {
            synchronized (this){
                try {
                    wait(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //Toast.makeText(MapsActivity.this, "2 SECONDS HAS PASSED", Toast.LENGTH_SHORT).show();
            //Trigger the API here
            callAPItoGetCoordinates(latLng);
            mhandler.postDelayed(this, 120000);
        }
    };
    //runnable.run()

    private void callAPItoGetCoordinates(LatLng latLng){
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MapsActivity.this);
        getLastLocation();
        //String url ="http://52.12.113.171:8080/incident/get?location="+cur_coordinates;

        String url = "http://52.12.113.171:8080/incident/get?location=37.330507371913846,-121.88447946024233";
        Log.d(TAG, "This is the constructed string:" + url);

        //Start the loading image to run here


        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            Float latitude;
            Float longitude;

            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject geofenceDets = response.getJSONObject(i).getJSONObject("location");

                        latitude = Float.parseFloat(geofenceDets.getString("latitude"));
                        longitude = Float.parseFloat(geofenceDets.getString("longitude"));

                        geoCoordinates.add(new LatLng(latitude,longitude) );

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Stop the animation image just after getting the response

                Toast.makeText(MapsActivity.this, "THE GEOFENCES OBTAINED ARE: " + geoCoordinates, Toast.LENGTH_SHORT).show();
                Log.d(TAG, "THE GEOFENCES OBTAINED ARE: " + geoCoordinates);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Error Adding Geofences", Toast.LENGTH_SHORT).show();
                Log.d(TAG,"This is the API calling error: " + error);

            }
        });
        queue.add(request);

    }


    public void stopRepeating(){
        mhandler.removeCallbacks(runnable);
    }

    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            //Ask for Permission
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Show dialog why the permission is needed
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, FINE_LOCATION_ACCESS_REQUEST_CODE);

            }
        }

    }
//Main Activity's
    //    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        if (requestCode == LOCATION_REQUEST_CODE){
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                //Permission Granted
//                getLastLocation();
//                checkSettingsAndStartLocationUpdates();
//            }else{
//                //Permission not granted
//            }
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                //ADDED HERE
                //Permission Granted
                getLastLocation();
                checkSettingsAndStartLocationUpdates();
                //ENDS HERE

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mMap.setMyLocationEnabled(true);

            } else {
                //Do not have the permission

            }
        }

        if (requestCode == BACKGROUND_LOCATION_ACCESS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can add Geofences..", Toast.LENGTH_SHORT).show();

            } else {
                //Do not have the permission
                Toast.makeText(this, "Background Location access is neccessary to add geofences..", Toast.LENGTH_SHORT).show();

            }
        }
    }


    @Override
    public void onMapLongClick(LatLng latLng) {
        if (Build.VERSION.SDK_INT >=29){
            //We need background permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                tryAddingGeofence(latLng);
            }else {
                //Request for the permission
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                    //Show dialog and ask for permission
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }else {
                    ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_BACKGROUND_LOCATION}, BACKGROUND_LOCATION_ACCESS_REQUEST_CODE);
                }
            }
        }else {
            tryAddingGeofence(latLng);
        }

    }

    private void tryAddingGeofenceS(){

        ArrayList<LatLng> ll = new ArrayList<>();
//        ll.add(new LatLng(48.863510, 2.288960));
        ll.add(new LatLng(37.37467956542969,-122.02188110351562));

        Log.d(TAG, "This is the working one!!: " + ll);
        for (LatLng temp : ll) {
            tryAddingGeofence(temp);
        }

//        for (LatLng temp: geoCoordinates){
//            tryAddingGeofence(temp);
//        }
    }

    private void tryAddingGeofence(LatLng latLng){
        //mMap.clear();
        Log.v(TAG, "onSuccess: Circle Added..");
        Log.d(TAG, "This is what the LAtLong obj looks like: "+ latLng);
        addMarker(latLng);
        addCircle(latLng, GEOFENCE_RADIUS);
        addGeofence(latLng, GEOFENCE_RADIUS);
    }

    private void addGeofence(LatLng latLng, float radius) {

        Geofence geofence = geofenceHelper.getGeofence(GEOFENCE_ID, latLng, radius, Geofence
                .GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_DWELL | Geofence.GEOFENCE_TRANSITION_EXIT);
        GeofencingRequest geofencingRequest = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent pendingIntent = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        Log.v(TAG, "onSuccess: Geofence Added..");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        String errorMessage = geofenceHelper.getErrorString(e);
                        Log.v(TAG, "onFailure: " + errorMessage);

                    }
                });

    }

    private void addMarker(LatLng latLng){
        MarkerOptions markerOptions = new MarkerOptions().position(latLng);
        mMap.addMarker(markerOptions);

    }

    private void addCircle(LatLng latLng, float radius){

        CircleOptions circleOptions = new CircleOptions();
        Log.v(TAG, "onSuccess: Circle Added..");
        circleOptions.center(latLng);
        circleOptions.radius(radius);
        circleOptions.strokeColor(Color.argb(255, 255, 0,0));
        circleOptions.fillColor(Color.argb(64, 255, 0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);
    }

    //----------------------------------------
    //ADDED HERE
    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getLastLocation();
            checkSettingsAndStartLocationUpdates();

            //howMuchHasTheUserMoved();
        } else {
            askLocationPermission();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    private void checkSettingsAndStartLocationUpdates() {
        LocationSettingsRequest request = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).build();
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> locationSettingsResponseTask = client.checkLocationSettings(request);
        locationSettingsResponseTask.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                //Settings of the device location satisfies
                startLocationUpdates();
            }
        });

        locationSettingsResponseTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    ResolvableApiException apiException = (ResolvableApiException) e;
                    try {
                        apiException.startResolutionForResult(MapsActivity.this, 1001);
                    } catch (IntentSender.SendIntentException ex) {
                        ex.printStackTrace();
                    }
                }

            }
        });

    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                .checkSelfPermission(this,(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();

        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    //We have a location
                    //Log.d(TAG, "onSuccess: " + location.toString());
                    Log.d(TAG, "onSuccess: " + location.getLatitude());
                    Log.d(TAG, "onSuccess: " + location.getLongitude());
                    //Log.d(TAG, "onSuccess: " + location.getSpeed());
                    cur_coordinates =""+location.getLatitude()+','+location.getLongitude();
                    Log.d(TAG, "THIS IS FROM INSIDE THE GETLASLOCATION(): " + cur_coordinates);
                }else {
                    Log.d(TAG, "onSuccess: Location was null.. ");
                }
            }
        });

        locationTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "onFailure: "+ e.getLocalizedMessage() );

            }
        });

    }

    private void askLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                Log.d(TAG, "askLocationPermission: Show a dialog box here");
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST_CODE);
            }else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, LOCATION_REQUEST_CODE);
            }
        }
    }

    //ENDS HERE

    private void howMuchHasTheUserMoved(){
        Log.d(TAG, "RIGHT HERE-");
        //Get inital location and compare with latest location at an interval or after travelling 1 mile
        getLastLocation();

    }


}
