package com.courierplus.mobile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.PersistableBundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import db.DataDB;

public class scanActivity extends AppCompatActivity {


    private Spinner spnorigin, spndestination,spnStatus,spnContent,spnRoute;
    private EditText txtWeight, txtPieces, txtTag, txtSeal, txtVehicleNo;
    private TextView lblBatchNo;
    private Button btnStartScan, btnNewBatch;
    private ProgressDialog mProgressView;
    DataDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);


        // set controls
        spnorigin = (Spinner) findViewById(R.id.spnOrigin);
        spndestination = (Spinner) findViewById(R.id.spnDestination);
        spnStatus = (Spinner) findViewById(R.id.spnScanStatus);
        spnContent = (Spinner) findViewById(R.id.spnContent);
        spnRoute = (Spinner) findViewById(R.id.spnRoute);

        txtWeight = (EditText) findViewById(R.id.txtWeight);
        txtPieces = (EditText) findViewById(R.id.txtPieces);
        txtTag = (EditText) findViewById(R.id.txtTag);
        txtSeal = (EditText) findViewById(R.id.txtSeal);
        txtVehicleNo = (EditText) findViewById(R.id.txtVehicleNo);


        lblBatchNo=(TextView) findViewById(R.id.lblBatchNo);
        // load dropdowns
        loadSpnOrigin();
        loadSpnScanStatus();
        loadSpnContent();

        if (Global.globalBatchNo.isEmpty()){
            Long tsLong = System.currentTimeMillis()/1000;
            String ts = tsLong.toString();
            lblBatchNo.setText(Global.globalUserName + ts);
        }



// check if status is 'WITH DELIVERY COURIER' and load route
        spnStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Destination: ", "> " + String.valueOf(parent.getItemAtPosition(position).toString()));
                String status = String.valueOf(parent.getItemAtPosition(position).toString());
                if(status.contentEquals("With Delivery Courier"))
                {
                    loadSpnRoute();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        // event for scans
        btnNewBatch = (Button)findViewById(R.id.btnNewBatch);
        btnNewBatch.setOnClickListener(new View.OnClickListener()
        {

            public void onClick (View v){
                Long tsLong = System.currentTimeMillis()/1000;
                String ts = tsLong.toString();
                lblBatchNo.setText(Global.globalUserName + ts);
            }
        });

        // event for scans
        btnStartScan = (Button)findViewById(R.id.btnStartScan);
        btnStartScan.setOnClickListener(new View.OnClickListener()
        {

            public void onClick (View v){
                // set global variable to controls
                Global.globalOrigin = String.valueOf(spnorigin.getSelectedItem());
                Global.globalDestination = String.valueOf(spndestination.getSelectedItem());
                Global.globalScanStatus = String.valueOf(spnStatus.getSelectedItem());
                Global.globalContentType = String.valueOf(spnContent.getSelectedItem());
                Global.globalRoute = String.valueOf(spnRoute.getSelectedItem());

                Global.globalWeight = txtWeight.getText().toString();
                Global.globalPieces = txtPieces.getText().toString();


                if (txtTag.getText().toString() == "") {
                    Global.globalTagNo = null;
                }else{
                    Global.globalTagNo = txtTag.getText().toString();
                }
                if (txtVehicleNo.getText().toString() == "") {
                    Global.globalVehicleNo = null;
                }else{
                    Global.globalVehicleNo = txtTag.getText().toString();
                }
                if (txtSeal.getText().toString() == "") {
                    Global.globalSealNo = null;
                }else{
                    Global.globalSealNo = txtSeal.getText().toString();
                }


                Global.globalBatchNo = lblBatchNo.getText().toString();

                validateControls();

            }
        });
    }
    // add items into spinner origin dynamically

    private void loadSpnOrigin() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getStation(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnorigin.setAdapter(dataAdapter);
        spndestination.setAdapter(dataAdapter);
    }
    private void loadSpnScanStatus() {
        // database handler
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getScanOperations(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnStatus.setAdapter(dataAdapter);
    }
    private void loadSpnContent() {
        // database handler
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getContent(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnContent.setAdapter(dataAdapter);
    }
    private void loadSpnRoute() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getRoutesByDestination(this,String.valueOf(spndestination.getSelectedItem()));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnRoute.setAdapter(dataAdapter);
    }
    protected void validateControls(){
        // set controls values


       // validate controls
        if(Global.globalOrigin.toString().equals(" "))
        {
            Global.AssetDialog("Origin is required!", scanActivity.this).create().show();
            spnorigin.requestFocus();
            return;
        }

        if(Global.globalDestination.toString().equals(" "))
        {
            Global.AssetDialog("Destination is required!", scanActivity.this).create().show();
            spndestination.requestFocus();
            return;
        }
        if(Global.globalScanStatus.toString().equals(" "))
        {
            Global.AssetDialog("Scan status is required!", scanActivity.this).create().show();
            spnStatus.requestFocus();
            return;
        }


        if(Global.globalWeight.toString().equals("") || Global.globalWeight.toString().equals("0"))
            {
                Global.AssetDialog("Weight is required!", scanActivity.this).create().show();
                txtWeight.requestFocus();
                return;
            }
        if(Global.globalPieces.toString().equals("") || Global.globalPieces.toString().equals("0"))
        {
            Global.AssetDialog("Pieces is required!", scanActivity.this).create().show();
            txtPieces.requestFocus();
            return;
        }
        if(Global.globalScanStatus.toString().equals("In Transit") && Global.globalTagNo.toString().equals(" "))
        {
            Global.AssetDialog("Tag number is required for shipment In Transit!", scanActivity.this).create().show();
            txtTag.requestFocus();
            return;
        }
        if(Global.globalScanStatus.toString().equals("In Transit") && Global.globalSealNo.toString().equals(""))
        {
            Global.AssetDialog("Seal number is required for shipment In Transit!", scanActivity.this).create().show();
            txtSeal.requestFocus();
            return;
        }
        if(Global.globalScanStatus.toString().equals("With Delivery Courier") && Global.globalRoute.toString().equals(" "))
        {
            Global.AssetDialog("Please select route!", scanActivity.this).create().show();
            spnRoute.requestFocus();
            return;
        }
        if(Global.globalContentType.toString().equals(" "))
        {
            Global.AssetDialog("Content Type is required!", scanActivity.this).create().show();
            spnContent.requestFocus();
            return;
        }
        else
        {
            mProgressView = ProgressDialog.show(scanActivity.this, "","Please wait...", true);
            Intent i = new Intent(scanActivity.this, ScanDetailsActivity.class);
            startActivity(i);
            // clear progress
            handler.sendEmptyMessage(0);
        }
    }


    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            mProgressView.dismiss();
        }
    };

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        lblBatchNo.setText(Global.globalUserName + ts);
    }

}
