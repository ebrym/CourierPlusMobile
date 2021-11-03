package com.courierplus.mobile;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import java.util.ArrayList;
import java.util.HashMap;
import android.widget.Toast;
import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


import com.courierplus.mobile.Tracking.TrackingActivity;

import db.DataDB;

public class MenuActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {
    Button btnScan, btnPOD, btnUpload, btnPickUp, btnTracking, btnCHMPOD ;
    TextView txtUserDetails;
    LinearLayout UserDetails;
    private ProgressDialog mProgressView;
    DataDB db;
    String imei = "";
    boolean flag = false;
    public static String webMethod = "sendGPSLog_Fetch";



    // for location
    private Location location;
    private TextView locationTv;
    private GoogleApiClient googleApiClient;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private LocationRequest locationRequest;
    private static final long UPDATE_INTERVAL = 5000, FASTEST_INTERVAL = 30000; // = 5 seconds


    // for permissions
    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
// we add permissions we need to request location of the users
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);


        permissionsToRequest = permissionsToRequest(permissions);


        db=new DataDB();
        //turnGPSOn();


           // Global.globalLocationEnabled=false;
            showSettingsAlert();

            Global.globalLocationEnabled=true;


        Global.globalBatchStatus = false;

        //start gps service
        //stopService(new Intent(MenuActivity.this, GPSService.class));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {

            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);

            // new LocationTrack(MenuActivity.this);
            //startService(new Intent(MenuActivity.this, LocationTrack.class));
//            String[] per = {Manifest.permission.ACCESS_FINE_LOCATION};
//            requestPermissions(per, 1);
//
//
//                if (ContextCompat.checkSelfPermission(MenuActivity.this,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                        != PackageManager.PERMISSION_GRANTED) {
//
//
//                startService(new Intent(MenuActivity.this, GPSService.class));
//                }

        }else{

            startService(new Intent(MenuActivity.this, GPSService.class));
        }

        // we build google api client
        googleApiClient = new GoogleApiClient.Builder(this).
                addApi(LocationServices.API).
                addConnectionCallbacks(this).
                addOnConnectionFailedListener(this).build();



        // start data sychronisation service
       // stopService(new Intent(MenuActivity.this, SyncData.class));
        startService(new Intent(MenuActivity.this, SyncData.class));

        // load Scans activity
        btnScan=(Button)findViewById(R.id.btnScan);
        btnScan.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, scanActivity.class);
                startActivity(i);
                //
            }
        } );
        // load POD activity
        btnPOD=(Button)findViewById(R.id.btnPod);
        btnPOD.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, podActivity.class);
                startActivity(i);
                //
            }
        } );
        // load upload activity
        btnUpload=(Button)findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, uploadActivity.class);
                startActivity(i);
                //
            }
        } );
        // load tracking activity
        btnTracking=(Button)findViewById(R.id.btnTracking);
        btnTracking.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, TrackingActivity.class);
                startActivity(i);
                //
            }
        } );
                // load Pickup activity
        btnPickUp=(Button)findViewById(R.id.btnPickUp);
        btnPickUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent i = new Intent(MenuActivity.this, PickupActivity.class);
                startActivity(i);
                //
            }
        } );



        // set layout controls
        UserDetails = (LinearLayout)findViewById(R.id.UserDetailsLayout);
        txtUserDetails = (TextView) findViewById(R.id.txtUserDetials);


            if(Global.globalUserName.toString().equals("admin")){
                Cursor dcursor = db.getUserList(MenuActivity.this);
                UserDetails.setVisibility(View.VISIBLE);
                if (dcursor.getCount() > 0) {
                    if (dcursor.moveToFirst()) {
                        do {
                            // Get each items
                            String username = dcursor.getString(dcursor.getColumnIndex("username"));
                            String Password = dcursor.getString(dcursor.getColumnIndex("password"));
                            String DeviceType = dcursor.getString(dcursor.getColumnIndex("DeviceType"));
                            txtUserDetails.setText("UserName : " + username.toString() +
                                                   " Password : " + Password.toString() +
                                                   " Device Type : " +  DeviceType.toString());
                        } while (dcursor.moveToNext());
                        dcursor.close();
                    }
                } else {
                    dcursor.close();
                }
            }else{
                UserDetails.setVisibility(View.INVISIBLE);
            }



    }



    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }




    public void showSettingsAlert() {

        LocationManager lm = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("GPS Not Enabled!!");
            dialog.setPositiveButton("Open Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub
                    Intent myIntent = new Intent( Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                    //get gps
                    Global.globalLocationEnabled=true;
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    // TODO Auto-generated method stub

                }
            });
            dialog.show();
        }

       /*  String provider = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        Intent intent=new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        sendBroadcast(intent);
        Global.globalLocationEnabled=true;

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MenuActivity.this);

        // Setting Dialog Title
        alertDialog.setTitle("GPS settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing the Settings button.
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                Global.globalLocationEnabled=true;
            }
        });



        // On pressing the cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
        */
    }

private class sendGPSLocation extends AsyncTask<String, String, String> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... arg0) {
        // Creating service handler class instance
        WebRequest webreq = new WebRequest();
        // add parameter or query string

        // Log.i("string" , arg0[0]);
        //DeviceID=string&UserID=string&Longitude=string&Latitude=string

        String DeviceID = arg0[0];
        String UserID = arg0[1];
        String Latitude = arg0[2];
        String Longitude = arg0[3];
        // Building Parameters


        HashMap<String, String> params = new HashMap<>();
        params.put("DeviceID", DeviceID);
        params.put("UserID", UserID);
        params.put("Longitude", Longitude);
        params.put("Latitude", Latitude);

        //DeviceID=string&UserID=string&Longitude=string&Latitude=string
        // Making a request to url and getting response
        String jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + webMethod, WebRequest.POST, params);

        //Log.d("Response: ", "> " + jsonStr);


        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
    }

}


    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Global.globalBatchStatus = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!checkPlayServices()) {
                Global.AssetDialog("You need to install Google Play Services to use the App properly", MenuActivity.this).create().show();

            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null  &&  googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            googleApiClient.disconnect();
        }
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // Permissions ok, we get last location
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            Global.globalLatitude = Double.toString(location.getLatitude());
            Global.globalLongitude = Double.toString(location.getLongitude());


            new sendGPSLocation().execute(Global.globalDeviceIMEI,Global.globalUserName,Global.globalLatitude,Global.globalLongitude);

            Log.d("LOCATION ","Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
        }

        startLocationUpdates();
    }
    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                &&  ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "You need to enable permissions to display location !", Toast.LENGTH_SHORT).show();
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            Log.d("LOCATION", "Latitude : " + location.getLatitude() + "\nLongitude : " + location.getLongitude());
            Global.globalLatitude = Double.toString(location.getLatitude());
            Global.globalLongitude = Double.toString(location.getLongitude());


            new sendGPSLocation().execute(Global.globalDeviceIMEI,Global.globalUserName,Global.globalLatitude,Global.globalLongitude);



        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case ALL_PERMISSIONS_RESULT:
                for (String perm : permissionsToRequest) {
                    if (!hasPermission(perm)) {
                        permissionsRejected.add(perm);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            new AlertDialog.Builder(MenuActivity.this).
                                    setMessage("These permissions are mandatory to get your location. You need to allow them.").
                                    setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.
                                                        toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    }).setNegativeButton("Cancel", null).create().show();

                            return;
                        }
                    }
                } else {
                    if (googleApiClient != null) {
                        googleApiClient.connect();
                    }
                }

                break;
        }
    }
}
