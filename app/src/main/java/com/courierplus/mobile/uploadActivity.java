package com.courierplus.mobile;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import db.DataDB;

public class uploadActivity extends AppCompatActivity {
    private TextView lblTotalPOD, lblTotoalScans,lblTotalPickUpCount,lblSync,lblTotalSignatureCount;
    private Button btnUpload;
    private ImageView imgSyncing;
    private ProgressDialog mProgressView;


    DataDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        // show progress
        mProgressView = ProgressDialog.show(uploadActivity.this, "", "Please wait...", true);
        // set controls
        lblTotalPickUpCount = (TextView) findViewById(R.id.txtUploadPickUpCount);
        lblTotoalScans = (TextView) findViewById(R.id.txtUploadScanCount);
        lblTotalPOD = (TextView) findViewById(R.id.txtUploadPODCount);
        lblTotalSignatureCount=(TextView) findViewById(R.id.txtUploadSignatureCount);

        lblSync = (TextView) findViewById(R.id.lblSync);
        imgSyncing = (ImageView) findViewById(R.id.imgSync);
        // display available records to transfer



        handler.sendEmptyMessage(0);

        btnUpload= (Button) findViewById(R.id.btnUpload);
       if (isMyServiceRunning(SyncData.class)) {
           //btnUpload.setEnabled(false);
           btnUpload.setVisibility(View.INVISIBLE);
           imgSyncing.setVisibility(View.INVISIBLE);
           lblSync.setVisibility(View.INVISIBLE);
       }else{
           btnUpload.setVisibility(View.INVISIBLE);
           imgSyncing.setVisibility(View.INVISIBLE);
           lblSync.setVisibility(View.INVISIBLE);
       }
        btnUpload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                btnUpload.setVisibility(View.INVISIBLE);
                imgSyncing.setVisibility(View.INVISIBLE);
                lblSync.setVisibility(View.INVISIBLE);
            }
        });

        Thread t = new Thread() {

            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(10000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                loadAvailableRecordsToUpload();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();


        // add click listener to all the value
        lblTotalPickUpCount.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (!lblTotalPickUpCount.getText().equals("0")) {
                    Global.globalDataListOpertionType = "PICKUP";
                    Global.globalDataListOpertionCode = "WEIGHT";
                    Intent i = new Intent(uploadActivity.this, DataListActivity.class);
                    i.putExtra("Operation", "PICKUP");
                    startActivity(i);
                }
                //
            }
        } );
        lblTotoalScans.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (!lblTotoalScans.getText().equals("0")) {
                    Global.globalDataListOpertionType = "SCAN";
                    Global.globalDataListOpertionCode = "SCAN STATUS";
                    Intent i = new Intent(uploadActivity.this, DataListActivity.class);
                    i.putExtra("Operation", "SCAN");
                    startActivity(i);
                }
                //
            }
        } );
        lblTotalPOD.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (!lblTotalPOD.getText().equals("0")) {
                    Global.globalDataListOpertionType = "POD";
                    Global.globalDataListOpertionCode = "POD";
                    Intent i = new Intent(uploadActivity.this, DataListActivity.class);
                    i.putExtra("Operation", "POD");
                    startActivity(i);
                }
                //
            }
        } );
    }


    private void loadAvailableRecordsToUpload() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db = new DataDB();
        // load avalable scans
        lblTotoalScans.setText(db.getAvailableScanCount(uploadActivity.this));
        lblTotalPOD.setText(db.getAvailablePODCount(uploadActivity.this));
        lblTotalPickUpCount.setText(db.getAvailablePickUpCount(uploadActivity.this));
        lblTotalSignatureCount.setText(db.getAvailableSignatureCount(uploadActivity.this));

        //imgSyncing.setVisibility(View.VISIBLE);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            mProgressView.dismiss();
        }
    };
}
