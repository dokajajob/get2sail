package com.dokajajob.get2sail_p1.Activities;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import org.apache.commons.lang3.StringUtils;
import com.dokajajob.get2sail_p1.databinding.ActivityMapsBinding;
import com.dokajajob.get2sail_p1.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import Dgango_API.GetLocation;
import Dgango_API.GetLocation.CallBack;
import Dgango_API.PostLocation;
import Utils.GPS;

public class Maps_Activity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, CallBack {

    public GoogleMap mMap;
    private ActivityMapsBinding binding;
    private static final String TAG = "MAPS";

    //GPS Location variables
    public Context context;
    public String lat;
    public String lng;
    public String mUID;
    private Boolean userType;
    private Boolean mapReady;
    private String UserIs;
    private FirebaseDatabase mDatabase;
    private List<GPS> gpsList;
    private List<GPS> listItems;
    private DatabaseReference mDatabaseReference;
    private Boolean showOnlyFlag = false;
    public LocationRequest mLocationRequest;
    private ZonedDateTime dateIs = null;
    private ZonedDateTime dateIsForMarker = null;
    private String receivedDate = null;
    private String uID_previous = "";
    private int count = 0;
    private Handler handlerMain = new Handler(Looper.getMainLooper());
    private Handler handlerNew;
    private HandlerThread mHandlerThread;
    public Serializable locationsResult;
    private long UPDATE_INTERVAL = 1800000;  /* 30 min */
    private long FASTEST_INTERVAL = 900000; /* 15 min */
    private long UPDATE_MARKERS_INTERVAL = 300000; /* 5 min */



    /**
     * device self-location update sent to BackEnd [ach 15 min]
     * device self-location added to map each [1 minute]
     * updates From BackEnd added to map each 5 min if [diff (current time - location record time) < 60 min]
     * cronjob cleans locations on backend each 15 min

     */
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = getApplicationContext();

        //Main thread
        Log.d("Threading", "Test class thread is         .     .     >"+Thread.currentThread().getName());

        //GPS Location Google API
        startLocationUpdates();


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        //Get user type
        UserIs = getUserType();
        Log.d("UserIs : ", UserIs);


        //Realtime database
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child(UserIs);
        Log.d("mDatabaseReference: ", mDatabaseReference.toString());
        mDatabaseReference.keepSynced(true);


    }


    /**
     * CurrentDateTime Class
     * @return
     */
    public ZonedDateTime getCurrentDate() {
        ZonedDateTime date = ZonedDateTime.now();
        System.out.println("date : " + date);

        return date;

    }



    /**
     * startLocationUpdates Google API
     */
    @SuppressLint("MissingPermission")
    protected void startLocationUpdates() {

        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        // Check whether location settings are satisfied
        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // Getting location
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }


    /**
     * onLocationChanged Google API. Start upload to backend
     */
    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        //LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        GPS gps = new GPS();


        MarkerOptions mp = new MarkerOptions();
        mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

        gps.setLat(Double.toString(location.getLatitude()));
        gps.setLng(Double.toString(location.getLongitude()));

        lat = gps.getLat();
        lng = gps.getLng();

        Log.d("DeviceUpdateLat : ", String.valueOf(lat));
        Log.d("DeviceUpdateLat : ", String.valueOf(lng));

        UserIs = getUserType();
        Log.d("getLastLocation", UserIs);

        mp.title(lat + " : " + lng + " : " + UserIs);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            mMap.addMarker(mp);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 16));
        }, 1000);


        try {
            uploadToBackend(gps);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * getLastLocation Google API
     */
    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        FusedLocationProviderClient locationClient = getFusedLocationProviderClient(this);

        locationClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // GPS location can be null if GPS is switched off
                        if (location != null) {
                            onLocationChanged(location);

                            //Clear map
                            clearMap();

                            //Add my position marker
                            GPS gps = new GPS();

                            MarkerOptions mp = new MarkerOptions();
                            mp.position(new LatLng(location.getLatitude(), location.getLongitude()));

                            gps.setLat(Double.toString(location.getLatitude()));
                            gps.setLng(Double.toString(location.getLongitude()));

                            lat = gps.getLat();
                            lng = gps.getLng();

                            Log.d("DeviceLat : ", String.valueOf(lat));
                            Log.d("DeviceLat : ", String.valueOf(lng));


                            mp.title(lat + " : " + lng);

                            Handler handler = new Handler();
                            handler.postDelayed(() -> {
                                mMap.addMarker(mp);
                                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)), 16));
                            }, 1000);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("MapDemoActivity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }



    /** get user type **/
    private String getUserType() {

        GPS gps = new GPS();
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            userType = bundle.getBoolean("userType");
            Log.d("userType", userType.toString());

            if (userType) {
                gps.setUser_is("skipper");
                return UserIs = "skipper";

            } else {
                gps.setUser_is("guest");
                return UserIs = "guest";

            }


        }
        return null;
    }

    /** Upload to backend **/
    private void uploadToBackend(GPS gps) throws JSONException {

/*        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Posting ... ");
        mProgress.show();*/


        UserIs = getUserType();
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child(UserIs);
//        DatabaseReference newPost = mDatabaseReference.push();
        PostLocation postLocation = new PostLocation();

        //uid
        Bundle bundle = getIntent().getExtras();
        mUID = bundle.getString("uid");
        Log.d("mUID", mUID);
        gps.setUid(mUID);

        //Current Date
        dateIs = getCurrentDate();
        System.out.println("dateIs " + dateIs);
        gps.setDate(dateIs.toString());

        //Date comparison for Dups check//
        //Current Date
        dateIsForMarker = getCurrentDate();
        System.out.println("dateIsForMarker " + dateIsForMarker);

        //Get date from db
        receivedDate = gps.getDate();
        Log.d("receivedDateBeforeUpload : ", receivedDate);

        //String to ZoneDateTime
        ZonedDateTime receivedDateInDate = ZonedDateTime.parse(receivedDate);
        System.out.println("receivedDateInDate " + receivedDateInDate);

        //Get diff
        long timeDiff = ChronoUnit.MINUTES.between(receivedDateInDate, dateIsForMarker);
        System.out.println("timeDiffBeforeUpload " + timeDiff);


        if (timeDiff < 1) {
             count = count + 1;
            System.out.println("count : " + count);
        }

        //Upload if no dups
        if (count < 2) {
            System.out.println("Posting to backend");
            postLocation.executePost(gps.getLat(), gps.getLng(), gps.getUid(), gps.getUser_is(), gps.getDate());

        }

       // mProgress.dismiss();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapReady = true;

        //Clear map and get time
        //clearMap();
        getCurrentDate();

        //Get first markers
        try {
            addMarkers();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Display other positions of same  user type
        //start new loop thread
        Log.d("addMarkersLoop : ", "started addMarkersLoop : ");

        scheduledAddMarkersLoop();


    }


    /**
     * scheduledAddMarkersLoop thread
     */
    private void scheduledAddMarkersLoop() {

        mHandlerThread = new HandlerThread("addMarkersThread");
        mHandlerThread.start();
        handlerNew = new Handler(mHandlerThread.getLooper());
        handlerNew.post(markersRunnable);

    }


    /**
     * markersRunnable
     */
    private Runnable markersRunnable = new Runnable() {
        @Override
        public void run() {
            // addMarkers
            Log.d("Threading","Running markersRunnable in the Thread  -- > " + Thread.currentThread().getName());
            try {
                addMarkers();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // Repeat every UPDATE_MARKERS_INTERVAL
            handlerNew.postDelayed(markersRunnable, UPDATE_MARKERS_INTERVAL);
        }
    };


    private void addMarkers() throws JSONException {

        //clear map
        clearMapNotMain();
        //mMap.clear();
        System.out.println("map cleared");

        MarkerOptions mp = new MarkerOptions();
        GetLocation getLocation = new GetLocation();

        //Getting locations and moving to callBack function [addMarkersToMap]
        getLocation.executeGet(context, UserIs=null, this);
        System.out.println("addMarker response : " + locationsResult);

    }


    /** clear Markers **/
    private void removeAllMarkers(List<Marker> allMarkers) {
        for (Marker mLocationMarker: allMarkers) {
            mLocationMarker.remove();
            Log.d("mLocationMarker ", "mLocationMarker");
        }
        allMarkers.clear();

        //Call add markers again if not called from showOnly
        try {
            addMarkers();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /** clear Map **/
    private void clearMap() {
        mMap.clear();


    }

    /** clear Map Not From Main Thread **/
    //@Override
    public void clearMapNotMain() {
        Maps_Activity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMap.clear();
            }
        });
    }



    /** Menu **/
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }



    public boolean onOptionsItemSelected(MenuItem item) {


            switch (item.getItemId()) {

                case R.id.action_add:
                    startProfile();
                    break;

//                case  R.id.action_signout:
//                    mAuth.signOut();
//                    startActivity(new Intent(Maps_Activity.this, MainActivity.class));
//                    finish();
//                    break;

                case  R.id.action_show_only_skippers:
                    UserIs = "skipper";
                    showOnly(UserIs);
                    break;

                case  R.id.action_show_only_guests:
                    UserIs = "guest";
                    showOnly(UserIs);
                    break;

                case R.id.action_show_location:
                    getLastLocation();
                    break;

            }

        return super.onOptionsItemSelected(item);


    }



    /** Show Only View **/
    private void showOnly(String UserTypeToDisplay) {

        //mMap.clear();
        clearMapNotMain();
        System.out.println("map cleared");

        GetLocation getLocation = new GetLocation();

        //Getting locations and moving to callBack function [addMarkersToMap]
        getLocation.executeGet(context, UserTypeToDisplay, this);
        System.out.println("addMarker response : " + locationsResult);

    }


    /** To Do **/
    private void startProfile() {
    }


    /** Get Locations Update CallBack **/
    @Override
    public void onSuccess(String response) {

        Log.d("locationsResponse", response);
        addMarkersToMap(response);

    }


    private void addMarkersToMap(String locationsResult) {

        List<Marker> AllMarkers = new ArrayList<Marker>();
        MarkerOptions mp = new MarkerOptions();

        String[] locations = locationsResult.split("\\}");
        for (int i = 0; i < locations.length - 1; i++) {
            Log.d("snapshot : " + i, locations[i]);
            String userLocation = locations[i];
            String uid = StringUtils.substringBetween(userLocation, "\"uid\":\"", "\",");
            Log.d("uid", uid);
            String user = StringUtils.substringBetween(userLocation, "\"user\":\"", "\",");
            Log.d("user", user);
            String lat = StringUtils.substringBetween(userLocation, "\"lat\":\"", "\",");
            Log.d("lat", lat);
            String lng = StringUtils.substringBetween(userLocation, "\"lng\":\"", "\",");
            Log.d("lng", lng);
            String date = StringUtils.substringBetween(userLocation, "\"date\":\"", "\"");
            Log.d("date", date);

            GPS gps = new GPS();
            gps.setUid(uid);
            gps.setUser_is(user);
            gps.setLat(lat);
            gps.setLng(lng);
            gps.setDate(date);

            //Current Date
            dateIsForMarker = getCurrentDate();
            System.out.println("dateIsForMarker " + dateIsForMarker);

            System.out.println("uidFromDb " + gps.getUid());
            System.out.println("uidUser " + mUID);

            //Date comparison//
            //Get date from db
            receivedDate = gps.getDate();
            Log.d("receivedDate : ", receivedDate);

            //String to ZoneDateTime
            ZonedDateTime receivedDateInDate = ZonedDateTime.parse(receivedDate);
            System.out.println("receivedDateInDate " + receivedDateInDate);

            //Get diff
            long timeDiff = ChronoUnit.MINUTES.between(receivedDateInDate, dateIsForMarker);
            System.out.println("timeDiff " + timeDiff);

            int sum = 0;
            //Add marker if user is not local and updated last 60 minutes
            if (!gps.getUid().equals(mUID) && timeDiff < 360000) {

                sum = sum + 1;

                lat = gps.getLat();
                lng = gps.getLng();

                Log.d("latFromFirebase : ", String.valueOf(lat));
                Log.d("latFromFirebase : ", String.valueOf(lng));

                mp.position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                mp.title(lat + " : " + lng + " : "  + gps.getUid());

                if (gps.getUser_is().equals("guest")) {
                    mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                    System.out.println("addMarker guest : " + gps.getUser_is());
                } else {
                    mp.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    System.out.println("addMarker skipper : " + gps.getUser_is());
                }

                //mMap.addMarker(mp);
                Marker mLocationMarker = mMap.addMarker(mp);
                AllMarkers.add(mLocationMarker);

            } else {
                Log.d("uidCheck ", "same uid and/or timeDiff < 5 " + mUID + " : " + timeDiff);
            }
            System.out.println("sum of added markers on addMarkers event " + sum);

        }
    }

    /** Get Locations Update CallBack **/
    @Override
    public void onError() {

    }
}