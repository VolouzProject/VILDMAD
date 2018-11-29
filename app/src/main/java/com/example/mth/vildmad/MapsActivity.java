package com.example.mth.vildmad;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,CountPlantAndPlayGame.sendChoosenPlant,CountPlantAndPlayGame.sendStopGame{



    private GoogleMap mMap;
    private LocationCallback mLocationCallback;
    private FusedLocationProviderClient mFusedLocationClient;
    public SeekBar seekBar;
    private Location loc_plant=new Location("my plant");
    private int distanceInMetersToPlant;
    private ArrayList<Marker> AllPlants=new ArrayList<Marker>();
    boolean onPlaying=false;




        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);




        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setSeekbarInvisible();





            mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {


                    SearchPlantsAt5km(location);
                    trackPositionPlayer(location);
                    huntChoosenPlant(loc_plant,location);





                }
            };
        };



    }
    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof CountPlantAndPlayGame) {
           CountPlantAndPlayGame countPlantAndPlayGame = (CountPlantAndPlayGame) fragment;
           countPlantAndPlayGame.setOnPlantSelectedListener(this);
           countPlantAndPlayGame.setOnStopGameListener(this);
        }
    }
    public void onPlantSelected(ArrayList<String> choosenPlant) {
            startGame(choosenPlant);

    }

    @Override
    public void onStopGame(boolean stop) {
        StopGame(stop);
    }

    private void StopGame(Boolean b){
      onPlaying=b;
      setTvVisibleAndButtonChange();
      setSeekbarInvisible();
      loc_plant = new Location("my plant");
        if (!AllPlants.isEmpty()) {

            for (Marker plant : AllPlants) {

                plant.setVisible(true);
            }



        }
    }
    private void setTvInvisibleAndBtChange(){
        if (MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment)!=null){
            View view =MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment).getView();
            TextView tv = view.findViewById(R.id.numberPlant);
            Button btPlay=view.findViewById(R.id.buttonPlay);
            Button btStop=view.findViewById(R.id.buttonStop);
            tv.setVisibility(View.INVISIBLE);
            btPlay.setVisibility(View.INVISIBLE);
            btStop.setVisibility(View.VISIBLE);

        }

    }
    private void startGame(ArrayList<String> choosenPlant){
        if (choosenPlant.size()>1){
            loc_plant.setLatitude(Double.valueOf(choosenPlant.get(1)));
            loc_plant.setLongitude(Double.valueOf(choosenPlant.get(2)));
            loc_plant.setTime(new Date().getTime());
            setSeekbarVisible();
            if (!AllPlants.isEmpty()) {

                for (Marker plant : AllPlants) {

                    plant.setVisible(false);
                }



            }

            onPlaying=true;
            setTvInvisibleAndBtChange();


        }

    }
    private void onGameFinished(){

        AlertDialog.Builder builderChoosen = new AlertDialog.Builder(MapsActivity.this);
        AlertDialog alertDialogChoosen=builderChoosen.create();
        alertDialogChoosen.setTitle("YOU WIN!!!!! Thanks you for playing");
        alertDialogChoosen.show();
        onPlaying=false;
        setTvVisibleAndButtonChange();
        setSeekbarInvisible();

        loc_plant = new Location("my plant");
        if (!AllPlants.isEmpty()) {

            for (Marker plant : AllPlants) {

                plant.setVisible(true);
            }



        }
    }
    private void setTvVisibleAndButtonChange(){
        if (MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment)!=null){
            View view =MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment).getView();
            TextView tv = view.findViewById(R.id.numberPlant);
            Button btPlay=view.findViewById(R.id.buttonPlay);
            Button btStop=view.findViewById(R.id.buttonStop);
            tv.setVisibility(View.VISIBLE);
            btPlay.setVisibility(View.VISIBLE);
            btStop.setVisibility(View.INVISIBLE);

        }

    }
    private void setSeekbarInvisible(){

        if (MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.distanceToPlant)!=null){
            View view =MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.distanceToPlant).getView().findViewById(R.id.seekBar);
            seekBar = view.findViewById(R.id.seekBar);
            seekBar.setVisibility(View.INVISIBLE);

        }

    }
    private void setSeekbarVisible(){

        if (MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.distanceToPlant)!=null){
            View view =MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.distanceToPlant).getView().findViewById(R.id.seekBar);
            seekBar = view.findViewById(R.id.seekBar);
            seekBar.setVisibility(View.VISIBLE);

        }

    }
    private void trackPositionPlayer(Location location){
        LatLng mLocation=new LatLng(location.getLatitude(),location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLocation));
    }
    private void huntChoosenPlant(Location loc_choosen_plant,Location actualLocation){

        if (loc_choosen_plant.getLongitude()!=0.0 && loc_choosen_plant.getLatitude()!=0.0){

            distanceInMetersToPlant=(int)actualLocation.distanceTo(loc_choosen_plant);
            if (distanceInMetersToPlant<=5000){
                seekBar.setProgress(distanceInMetersToPlant);
                if (distanceInMetersToPlant<=4){
                    onGameFinished();

                }
               }

            else{
                seekBar.setProgress(5000);
            }
        }

    }

    protected void createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
        }

    }

    private void SearchPlantsAt5km(Location myLocation){

        ArrayList<ArrayList<String>> plants=new ArrayList<ArrayList<String>>();

            if (!AllPlants.isEmpty()) {

                for (Marker plant : AllPlants) {

                    Location actualPlant = new Location("actual plant");
                    actualPlant.setLatitude(plant.getPosition().latitude);
                    actualPlant.setLongitude(plant.getPosition().longitude);
                    actualPlant.setTime(new Date().getTime());

                    if (actualPlant.distanceTo(myLocation) <= 5000) {
                        ArrayList<String> eachPlant=new ArrayList();
                        eachPlant.add(plant.getTitle());
                        eachPlant.add(String.valueOf(plant.getPosition().latitude));
                        eachPlant.add(String.valueOf(plant.getPosition().longitude));
                        plants.add(eachPlant);


                    }else {
                        ArrayList<String> eachPlant=new ArrayList();
                        eachPlant.add(plant.getTitle());
                        eachPlant.add(String.valueOf(plant.getPosition().latitude));
                        eachPlant.add(String.valueOf(plant.getPosition().longitude));
                        plants.removeAll(eachPlant);


                    }

                }
                if (MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment)!=null) {
                    View view = MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment).getView().findViewById(R.id.numberPlant);
                    TextView numberPlant = view.findViewById(R.id.numberPlant);
                    Bundle b = new Bundle();
                    if (!plants.isEmpty()) {
                        for (ArrayList<String> plant1 : plants
                                ) {
                            b.putStringArrayList("listPlants", plant1);
                            MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment).setArguments(b);
                            Log.i("hello1", plants.toString());
                            numberPlant.setText("Nb Plant<5km: " + String.valueOf(plants.size()));
                        }
                    } else {
                        ArrayList<String> noPlant = new ArrayList<>();
                        noPlant.add("No plant here");
                        b.putStringArrayList("listPlants", noPlant);
                        MapsActivity.this.getSupportFragmentManager().findFragmentById(R.id.playGameFragment).setArguments(b);
                        Log.i("hello", plants.toString());
                        numberPlant.setText("Nb Plant<5km: " + String.valueOf(plants.size()));
                    }
                }






            }

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

        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {

                mMap.setMyLocationEnabled(true);
                onResume();
                createLocationRequest();

            } else {
                checkLocationPermission();


            }
        } else {

            mMap.setMyLocationEnabled(true);
            onResume();
            createLocationRequest();



        }

        Drawable circleDrawable = getResources().getDrawable(R.drawable.ic_lotus_flower);
        final BitmapDescriptor markerIcon = getMarkerIconFromDrawable(circleDrawable);
        LatLng location = new LatLng(48, 2);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,9));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(final LatLng point) {

                if (onPlaying == false) {
                    LayoutInflater description_plant = LayoutInflater.from(MapsActivity.this);
                    final View alertDialogView = description_plant.inflate(R.layout.dialog_plant_description, null);
                    //create alertDialog
                    AlertDialog.Builder adb = new AlertDialog.Builder(MapsActivity.this);
                    //set our view for alertDialog
                    adb.setView(alertDialogView);
                    //if "ok" value of marker's title is value of editText name_plant and description_plant
                    adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {


                            EditText name_plant = (EditText) alertDialogView.findViewById(R.id.plant_name);
                            EditText description_plant = (EditText) alertDialogView.findViewById(R.id.description);
                            Marker plant = mMap.addMarker(new MarkerOptions()
                                    .title(name_plant.getText().toString())
                                    .position(point)
                                    .snippet(description_plant.getText().toString())
                                    .icon(markerIcon)


                            );
                            AllPlants.add(plant);


                        }
                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    adb.show();
                }
            }
        });



    }
    @Override
    public void onResume(){
        super.onResume();

        if(mMap != null){
            Log.i("mMap", mMap.toString());//prevent crashing if the map doesn't exist yet (eg. on starting activity)
            mMap.clear();

            // add markers from database to the map
        }
    }
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MapsActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION );
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION );
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {


                        mMap.setMyLocationEnabled(true);
                        onResume();
                        createLocationRequest();
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}



