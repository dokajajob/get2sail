package com.dokajajob.get2sail_p1.Activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.dokajajob.get2sail_p1.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.functions.FirebaseFunctions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import Dgango_API.Login;
import Dgango_API.Register;
import Utils.GPS;
import pl.droidsonroids.gif.GifImageView;
import org.json.JSONException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;


public class MainActivity extends AppCompatActivity  {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private static final String TAG = "MAIN";
    public EditText user;
    public EditText password;
    public Button button;
    public String userName;
    private String userPasswd;
    private Button buttonSignOut;
    private ImageView buttonCreate;
    private GifImageView loginImage;
    private GifImageView logoutImage;
    private FirebaseFunctions mFunctions;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    public double Latitude;
    public double Longitude;
    public GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public Boolean permissionsGranted;
    public Boolean mRequestingLocationUpdates;
    public Location mCurrentLocation;
    private Switch switch_skipper;
    private Boolean isSkipper = false;
    private Boolean switchState = false;
    private Boolean authorized;
    private String UserIsMain;
    private InetAddress ip;
    private String hostname;
    public Context context;


    //Permission checked in checkPermissions()
    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        context = getApplicationContext();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        //Check permissions
        if (checkPermissions()) {
            Toast.makeText(MainActivity.this, "Location permissions granted", Toast.LENGTH_LONG).show();

        } else {
            requestPermission();
            Log.d(TAG, "Requesting location permissions");

        }



        //User switch state
        switch_skipper = findViewById(R.id.switch_skipper);
        switch_skipper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = getSharedPreferences("com.dokajajob.get2sail_p1", MODE_PRIVATE).edit();
                if (isChecked) {
                    //isSkipper = true;
                    editor.putBoolean("isSkipperPref", true);

                } else {
                    //isSkipper = false;
                    editor.putBoolean("isSkipperPref", false);

                }
                editor.apply();
            }
        });




        //Auth instantiate
        firebaseAuth = FirebaseAuth.getInstance();

        //Auth State
        firebaseAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                //User switch state change
                SharedPreferences sharedPrefs = getSharedPreferences("com.dokajajob.get2sail_p1", MODE_PRIVATE);
                if (sharedPrefs != null) {
                    isSkipper = sharedPrefs.getBoolean("isSkipperPref", false);
                    switch_skipper.setChecked(isSkipper);

                }

                System.out.println("isSkipperPref : " + isSkipper);


                if (user != null && checkPermissions()) {


                    //user signed in
                    Log.d(TAG , " signed in" );
                    Log.d(TAG, user.getEmail());
                    Toast.makeText(MainActivity.this,  user.getEmail() + " signed in", Toast.LENGTH_LONG).show();


                    //Going to maps activity
                    Intent intent = new Intent(MainActivity.this, Maps_Activity.class);
                    intent.putExtra("userType", isSkipper);
                    intent.putExtra("authorized", authorized);

                    final Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {



                            startActivity(intent);
                            finish();


                        }
                    }, 1000);



                } else {
                    Log.d(TAG , " signed out");
                    Toast.makeText(MainActivity.this, "User signed out", Toast.LENGTH_LONG).show();
                }

            }
        };

        user = findViewById(R.id.user);
        password = findViewById(R.id.password);
        loginImage = findViewById(R.id.loginImage);

        Login djangoLogin = new Login();
        GPS gps = new GPS();


        //Login [Django]
        loginImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get local ip
                try {
                    ip = InetAddress.getLocalHost();
                    hostname = ip.getHostName();
//                    System.out.println("Your current IP address : " + ip);
//                    System.out.println("Your current Hostname : " + hostname);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                String ip_str = ip.toString();

                //store ip to shared preferences
                SharedPreferences.Editor editor = getSharedPreferences("com.dokajajob.get2sail_p1", MODE_PRIVATE).edit();
                editor.putString("local_ip", ip_str);
                editor.apply();

                userName = user.getText().toString();
                userPasswd = password.getText().toString();

                if (!userName.equals("") && !userPasswd.equals("")) {
                    try {
                        Serializable response = djangoLogin.executePost(userName, userPasswd);
                        Log.d("loginResponse", response.toString());

                        //response check
                        if (response.toString().contains("token")) {
                            gps.setUtoken(response.toString());
                        } else {
                            Log.d("response", response.toString());

                        }


                        //uToken check
//                      Log.d("uToken : ", gps.getUtoken());
                        if (gps.getUtoken() != null) {
                                Log.d(TAG, userName + " is logged in");
                                Toast.makeText(MainActivity.this,   userName + " is logged in", Toast.LENGTH_LONG).show();


                                //Going to maps activity
                                Intent intent = new Intent(MainActivity.this, Maps_Activity.class);
                                intent.putExtra("userType", isSkipper);
                                intent.putExtra("uid", userName);
                                startActivity(intent);
                                finish();


                            } else {

                                Log.d(TAG, userName + " failed to log in");
                                Toast.makeText(MainActivity.this,  userName + " is failed to log in", Toast.LENGTH_LONG).show();
                            }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
                    Toast.makeText(MainActivity.this, "Enter credentials first", Toast.LENGTH_LONG).show();
                }
            }
        });


        Register djangoRegister = new Register();

        //Create User
        buttonCreate = findViewById(R.id.buttonCreate);
        buttonCreate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                userName = user.getText().toString();
                userPasswd = password.getText().toString();

                if (!userName.equals("") && !userPasswd.equals("")) {

                    try {
                        Serializable response = djangoRegister.executePost(userName, userPasswd);
                        Log.d("response", response.toString());
                        Toast.makeText(MainActivity.this, userName + " created", Toast.LENGTH_LONG).show();

                        //response check
                        if (response.toString().contains("HTTP error code : 201")) {
                            gps.setUtoken(response.toString());
                        } else {
                            Log.d("response", response.toString());
                        }

                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, userName + " failed to create user", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }



                } else {
                    Toast.makeText(MainActivity.this, "enter credentials first", Toast.LENGTH_LONG).show();
                }

            }
        });


    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences.Editor editor = getSharedPreferences("com.dokajajob.get2sail_p1", MODE_PRIVATE).edit();
        //editor.clear();
        editor.apply(); // commit changes
    }


    /**
     * Menu builder
     */
    public boolean onOptionsItemSelected(MenuItem item) {

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        switch (item.getItemId()) {



        }
        return super.onOptionsItemSelected(item);

    }

    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Start
     */
    @Override
    protected void onStart() {
        super.onStart();

        firebaseAuth.addAuthStateListener(firebaseAuthListener);


    }

    /**
     * Stop
     */
    @Override
    protected void onStop() {
        super.onStop();

        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);

        }

    }

    /**
     * Check permissions. returns only if granted
     */
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        //permissionsGranted = true;
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * GPS Permissions Request
     */
    public void requestPermission() {
        // Requesting ACCESS_FINE_LOCATION using Dexter library
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this, "Location permissions granted", Toast.LENGTH_LONG).show();


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        if (response.isPermanentlyDenied()) {
                            // open device settings when the permission is
                            // denied permanently
                            //openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }



}