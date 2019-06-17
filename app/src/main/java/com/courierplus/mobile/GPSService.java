package com.courierplus.mobile;

import com.courierplus.mobile.Global;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
//import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by iabdullahi on 6/7/2016.
 */
public class GPSService extends Service  {
//private LocationManager locationManager;
    //private String provider;

    String imei = "";
    boolean flag = false;
    public static String webMethod = "sendGPSLog_Fetch";
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();




        // Acquire a reference to the system Location Manager
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //Location lastKnownLocationNetwork;
        //Location lastKnownLocationGPS;

        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//      //  Global.globalLocationEnabled=gpsEnabled;
//        if (gpsEnabled == false) {
////            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
////            startActivity(settingsIntent);
//            Global.globalLocationEnabled=false;
//        }else{
//            Global.globalLocationEnabled=true;
//            //Toast.makeText(this, "GPS Disabled./n Please enable GPS and Network ", Toast.LENGTH_LONG).show();
//        }



        // Define a listener that responds to location updates
        LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                // Called when a new location is found by the network location provider.
                String longt = "";
                String lattd = "";

                if (location != null)
                {
                    //location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    lattd = Double.toString(location.getLatitude());
                    longt = Double.toString(location.getLongitude());
                }

                try
                {
                    if(Global.globalUserName.toString().equals("") || Global.globalDeviceIMEI.toString().equals("")) {
                        stopSelf();
                        Intent i = new Intent();
                        i.setClass(GPSService.this, LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    }else{
                        Global.globalLatitude = lattd;
                        Global.globalLongitude = longt;

                        new sendGPSLocation().execute(Global.globalDeviceIMEI,Global.globalUserName,Global.globalLatitude,Global.globalLongitude);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };

        // Register the listener with the Location Manager to receive location updates
       // locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 120000, 0, locationListener);
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 180000,200, locationListener);

        //request permission


        try {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, 180000, 200,locationListener);
        } catch (SecurityException ex) {
           // Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
           // Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 180000, 200,locationListener);
        } catch (SecurityException ex) {
            //Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
           // Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

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
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "GPS Disabled./n Please enable GPS and Try again. ", Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
    }
}
