package com.example.mth.vildmad;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {



    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    public SeekBar seekBar;
    private Location loc_plant;
    private int distanceInMetersToPlant;
    private Drawable circleDrawable;
    private BitmapDescriptor markerIcon;
    private String plantURL = "https://mysterious-fjord-16136.herokuapp.com/api/Plants/";
    private RequestQueue queue;
    private JsonObjectRequest jsonRequest;
    private CountDownLatch waiter;
    private ArrayList<Plant> plants = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        queue = Volley.newRequestQueue(this);
        //Get plants from database
        getPlants();
        circleDrawable = getResources().getDrawable(R.drawable.ic_lotus_flower);
        markerIcon = getMarkerIconFromDrawable(circleDrawable);
        loc_plant = new Location("my plant");

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    View view =MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.distanceToPlant).getView().findViewById(R.id.seekBar);
                    seekBar = view.findViewById(R.id.seekBar);


                    if (loc_plant.getLongitude()!=0.0 && loc_plant.getLatitude()!=0.0){

                        distanceInMetersToPlant=(int)location.distanceTo(loc_plant);
                        if (distanceInMetersToPlant>=5000){
                            seekBar.setProgress(distanceInMetersToPlant);
                            Toast.makeText(MapsActivity.this,"Too far From plant",
                                    Toast.LENGTH_SHORT).show();}
                        else if(distanceInMetersToPlant<5000 && distanceInMetersToPlant>2000){
                            seekBar.setProgress(distanceInMetersToPlant);
                            Toast.makeText(MapsActivity.this,"Not that far from plant",
                                    Toast.LENGTH_SHORT).show();}
                        else if(distanceInMetersToPlant<=2000 && distanceInMetersToPlant>500){
                            seekBar.setProgress(distanceInMetersToPlant);
                            Toast.makeText(MapsActivity.this,"Not that that far from plant",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            seekBar.setProgress(distanceInMetersToPlant);
                            Toast.makeText(MapsActivity.this,"close to plant",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    Bundle b=new Bundle();
                    b.putDouble("key",location.getLongitude());
                    MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.distanceToPlant).setArguments(b);
                }

            }
        };



    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback,
                null);
    }



    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        createLocationRequest();
        LatLng location = new LatLng(48, 2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,9));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng point) {
                createPlant(point, markerIcon);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(point));
            }
        });



    }
    private void createPlant(final LatLng point, final BitmapDescriptor markerIcon){
        LayoutInflater description_plant = LayoutInflater.from(MapsActivity.this);
        final View alertDialogView = description_plant.inflate(R.layout.dialog_plant_description, null);
        //create alertDialog
        AlertDialog.Builder adb = new AlertDialog.Builder(MapsActivity.this);
        //set our view for alertDialog
        adb.setView(alertDialogView);
        //if "ok" value of marker's title is value of editText name_plant and description_plant
        adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                loc_plant.setLatitude(point.latitude);
                loc_plant.setLongitude(point.longitude);
                loc_plant.setTime(new Date().getTime());

                EditText name_plant = (EditText)alertDialogView.findViewById(R.id.plant_name);
                EditText description_plant = (EditText)alertDialogView.findViewById(R.id.description);

                //Put Plant in DB
                JSONObject jsonObject = new JSONObject();
                try{
                    jsonObject.put("username","admin");
                    jsonObject.put("plantname",name_plant.getText().toString());
                    jsonObject.put("lat",point.latitude);
                    jsonObject.put("lon",point.longitude);
                    jsonObject.put("description",description_plant.getText().toString());
                }catch (JSONException e){
                    e.printStackTrace();
                }
                jsonRequest = new JsonObjectRequest(Request.Method.POST, plantURL, jsonObject, new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("GOT A RESPONSE: " + response.toString());
                    }
                }, new com.android.volley.Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Got and error :( --------------------------------------- " + error.toString());
                    }
                });
                queue.add(jsonRequest);
                //Refresh plants list to get the new plant with its id...
                getPlants();
            }
        });
        adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        adb.show();
    }

    private void getPlants(){
        //Get the plants
        JsonArrayRequest jsonRequest = new JsonArrayRequest(Request.Method.GET, plantURL, null, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Does this only clear the markers???
                mMap.clear();
                plants = new ArrayList<>();
                try {
                    for(int i = 0; i < response.length(); i++){
                        JSONObject jsonPlant = response.getJSONObject(i);
                        Plant newPlant = new Plant(jsonPlant.getString("plantname"),jsonPlant.getString("_id"),jsonPlant.getString("username"),Float.parseFloat(jsonPlant.getString("lat")),Float.parseFloat(jsonPlant.getString("lon")),jsonPlant.getString("description"));
                        plants.add(newPlant);
                    }
                    for(int i = 0; i < plants.size();i++){
                        System.out.println(plants.get(i).toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                waiter.countDown();
            }
        }, new com.android.volley.Response.ErrorListener(){
            @Override
            public void onErrorResponse(VolleyError error) {
                waiter.countDown();
            }
        });
        queue.add(jsonRequest);
        waiter = new CountDownLatch(1);

        try{
            waiter.await(30,TimeUnit.SECONDS);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        //put plants on the map
        showPlants();
    }

    private void showPlants(){
        for(int i = 0; i < plants.size(); i++){
            Plant plant = plants.get(i);
            mMap.addMarker(new MarkerOptions()
                    .title(plant.getPlantName())
                    .position(new LatLng(plant.getLat(),plant.getLon()))
                    .snippet(plant.getDescription())
                    .icon(markerIcon)
            );
        }
    }
}