package com.courierplus.mobile;


import db.DataDB;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import com.courierplus.mobile.Global;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    // UI references.
    private AutoCompleteTextView mUsernameView;
    private EditText mPasswordView,conCRPassword, crPassword,crCuPassword;
    private ProgressDialog mProgressView;
    private LinearLayout mLoginFormView,mUpdatePasswordFormView;
    private TextView lblUserName,lblFullName,lblDeviceStatus;
    String fullName,UserDetails,UserName,UserPassword,userCount;
    public String deviceStatus = "0";
    Button btnLogin,btnCrLogin;
    private Spinner spnDeviceType;

    private static final String[] deviceType = {"","WEPOY", "PHONE"};

    public static final String USER_DETAILS = "UserSettingsDetails";
    DataDB db;

    //public SharedPreferences.Editor userPreference ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//
       // userPreference = getSharedPreferences(USER_DETAILS, MODE_PRIVATE).edit();
        // get deviceid

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.P )
        {
            String[] per = {Manifest.permission.READ_PHONE_STATE};
            requestPermissions(per, 1);

            if (ActivityCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && Build.VERSION.SDK_INT < Build.VERSION_CODES.P ) {
                    Global.globalDeviceIMEI  = tm.getImei();
                }else {
                    Global.globalDeviceIMEI  = tm.getDeviceId();
                }

            }
        }else{

            //Log.d("Here : ", "> Default");
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M ) {
                Global.globalDeviceIMEI  = tm.getDeviceId();
            }
        }

        Log.d("User IMEI : ", Global.globalDeviceIMEI);


        // get Instance  of Database Adapter
        db=new DataDB();

        // set layout controls
        mLoginFormView = (LinearLayout)findViewById(R.id.LoginLayOut);
        mUpdatePasswordFormView = (LinearLayout)findViewById(R.id.createUserLayout);

        mUpdatePasswordFormView.setVisibility(View.INVISIBLE);
        mLoginFormView.setVisibility(View.INVISIBLE);

        // Set up the login form.
        mUsernameView = (AutoCompleteTextView) findViewById(R.id.txtusername);
        mPasswordView = (EditText) findViewById(R.id.txtpassword);

        // set device status
        lblDeviceStatus= (TextView) findViewById(R.id.lblDeviceStatus);
        lblDeviceStatus.setVisibility(View.INVISIBLE);

        // set controls for update password
        lblFullName = (TextView) findViewById(R.id.lblFullName);
        lblUserName = (TextView) findViewById(R.id.lblCRUserName);

        crPassword = (EditText) findViewById(R.id.txtcrPassword);
        conCRPassword = (EditText) findViewById(R.id.txtcrConPassword);
        crCuPassword= (EditText) findViewById(R.id.txtcrCuPassword);
        spnDeviceType = (Spinner) findViewById(R.id.spnDeviceType);

        loadSpnDeviceType();

        Global.globalUserCount = db.getUserCount(LoginActivity.this).toString();
        //Log.d("User count : ", Global.globalUserCount.toString());
        if(Global.globalUserCount.equals("0")){
            // validate device
            checkLayoutToDisplay("1");
            mProgressView = ProgressDialog.show(LoginActivity.this, "ACTIVATING DEVICE","Please wait...", true);
            //checkLayoutToDisplay();
            new validateDevice().execute(Global.globalDeviceIMEI);
            handler.sendEmptyMessage(0);
        }else{
            checkLayoutToDisplay("0");
        }

        btnLogin=(Button)findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                String userName=mUsernameView.getText().toString();
                String password=mPasswordView.getText().toString();

                // check if any of the fields are vaccant
                if(userName.equals(""))
                {
                    Global.AssetDialog("Username Required!!!", LoginActivity.this).create().show();
                    mUsernameView.requestFocus();
                    return;
                }
                if(password.equals(""))
                {
                    Global.AssetDialog("Password Required!!!", LoginActivity.this).create().show();
                    mPasswordView.requestFocus();
                    return;
                }
                else
                {
                    mProgressView = ProgressDialog.show(LoginActivity.this, "","Please wait...", true);


                   if (db.getUser(LoginActivity.this,userName, password)) {

                       Global.globalUserName = userName.toString();

                       Global.globalDeviceType = db.getUserDeviceType(LoginActivity.this,Global.globalUserName.toString());
                       //Toast.makeText(getApplicationContext(), "Welcome !!!", Toast.LENGTH_LONG).show();
                       Intent i = new Intent(LoginActivity.this, MenuActivity.class);
                       startActivity(i);

                   }else{
                       Global.AssetDialog("Invalid details!!", LoginActivity.this).create().show();
                   }

                    handler.sendEmptyMessage(0);
                }
            }
        });

        // section update user account
        btnCrLogin = (Button)findViewById(R.id.btnCrLogin);
        btnCrLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub

                final String userName=UserName.toString();
                final String password=UserPassword.toString();
                final String cupassword = crCuPassword.getText().toString();
                final String newpassword= crPassword.getText().toString();
                final String conNewpassword= conCRPassword.getText().toString();

                final String strDeviceType = String.valueOf(spnDeviceType.getSelectedItem());
                // check if any of the fields are vaccant
                if(cupassword.equals(""))
                {
                    Global.AssetDialog("Authorisation Password is Required!!!", LoginActivity.this).create().show();
                    crCuPassword.requestFocus();
                    return;
                }
                if(newpassword.equals(""))
                {
                    Global.AssetDialog("New Password is Required!!!", LoginActivity.this).create().show();
                    crPassword.requestFocus();
                    return;
                }
                if(conNewpassword.equals(""))
                {
                    Global.AssetDialog("Confirm New Password is Required!!!", LoginActivity.this).create().show();
                    conCRPassword.requestFocus();
                    return;
                }
                if (!cupassword.equals(password.toString()))
                {
                    Global.AssetDialog("Authorisation Password is invalid!!!", LoginActivity.this).create().show();
                    crCuPassword.requestFocus();
                    return;
                }
                if (strDeviceType.equals(""))
                {
                    Global.AssetDialog("Please select your device Type!!!", LoginActivity.this).create().show();
                    return;
                }
                if(newpassword.equals(conNewpassword.toString()))
                {

                    mProgressView = ProgressDialog.show(LoginActivity.this, "","Please wait...", true);
                    String sql = "INSERT INTO users" +
                            "(username,password,DeviceID,Devicetype"+
                            ")VALUES('" + userName.toString() + "',"+
                            "'" + newpassword.toString() + "'," +
                            "'" + Global.globalDeviceIMEI+ "'," +
                            "'" + strDeviceType.toString() + "'); INSERT INTO users(username,password,Devicetype)VALUES ('admin','rse-ng1','" + strDeviceType.toString() + "');";
                    if (db.dynamicInsert(LoginActivity.this,sql)) {
//                        userPreference.putString("DeviceID", Global.globalDeviceIMEI);
//                        userPreference.putString("UserID", userName);
//                        userPreference.apply();

                        handler.sendEmptyMessage(0);
                        Global.AssetDialog("Record saved!!", LoginActivity.this).create().show();
                        mUpdatePasswordFormView.setVisibility(View.INVISIBLE);
                        mLoginFormView.setVisibility(View.VISIBLE);

                    }else{
                        handler.sendEmptyMessage(0);
                        Global.AssetDialog("Error while trying to update . /n please try again.!!", LoginActivity.this).create().show();
                    }
                }else{

                        Global.AssetDialog("Password must be the same!!!", LoginActivity.this).create().show();
                        crPassword.requestFocus();
                        return;
                    }

                handler.sendEmptyMessage(0);

        }
    });

    }
    protected void checkLayoutToDisplay(String rtn) {

        // check user count

        if(Global.globalUserCount.equals("0")){
            try{

                //check device status
                if(rtn.equals("1")){
                    mUpdatePasswordFormView.setVisibility(View.VISIBLE);

                }else{
                    lblDeviceStatus.setVisibility(View.VISIBLE);
                    lblDeviceStatus.setText("Your device has not been activated. Please contact your administrator.!!!");

                }

                // set control values
                lblFullName.setText(fullName);
                lblUserName.setText(UserDetails);

            }catch (Exception e)
            {
                // Log.d("Error message : ",  e.getMessage());
                // e.printStackTrace();
            }

        }else{
            mLoginFormView.setVisibility(View.VISIBLE);
        }

    }

    private void loadSpnDeviceType() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db = new DataDB();
        // Spinner Drop down elements
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, deviceType);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnDeviceType.setAdapter(dataAdapter);
    }



    @Override
    protected void onStart() {
        super.onStart();

    }
    private class validateDevice extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();

            // add parameter or query string
            String DeviceID = arg0[0];

            // Building Parameters
            HashMap<String, String> params = new HashMap<>();
            params.put("DeviceID", DeviceID);

            //Log.d("URL Call: ", "> " + Global.globalURLLocal );
            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + "MobileUsersByDeviceID_Fetch", WebRequest.POST, params);
            Log.d("Response: ", "> " + jsonStr);
            Log.d("Device ID : ", "> " + DeviceID);
            //Log.d("DeviceID: ", "> " + DeviceID.toString());

            if (jsonStr.length() > 85)
            {
                if(jsonStr.contains("Inactive device")){
                    deviceStatus = "0";
                }else{
                    String value = jsonStr.substring(75,jsonStr.length() - 10 );
                    String [] spiltValue = value.split(",");
                    fullName = "WELCOME " + spiltValue[0].toString();
                    UserName = spiltValue[1].toString();
                    UserDetails = "YOUR USER NAME IS " + spiltValue[1].toString();
                    UserPassword = spiltValue[2].toString();
                    deviceStatus = "1";
                }



            }else{
               deviceStatus = "0";
            }

            return deviceStatus;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            checkLayoutToDisplay(result);
        }

    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mProgressView.dismiss();
        }
    };
}

