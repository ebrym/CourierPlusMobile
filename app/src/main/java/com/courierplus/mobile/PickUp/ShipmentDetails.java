package com.courierplus.mobile.PickUp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.Symbology;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.courierplus.mobile.Global;
import com.courierplus.mobile.PickupActivity;
import com.courierplus.mobile.R;
import com.courierplus.mobile.ScanDetailsActivity;
import com.courierplus.mobile.podActivity;

import java.util.ArrayList;
import java.util.List;

import barcodereader.BarcodeCaptureActivity;
import db.DataDB;
import me.dm7.barcodescanner.scanner.ScannerActivity;


public class ShipmentDetails extends Fragment {
    private Spinner spnPickUpPackaging, spnPickUpBoxCrating,spnPickUpExpressCenter;
    private EditText txtPickUpWaybillNumber,txtPickupWeight,txtPickupPieces,
            txtDescription,txtPickUpDeclaredValue,txtPickUpInsurance,txtPickUpCratingValue;
    private Button btnBarcode;
    DataDB db;
    // for camera barcode
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String TAG = "BarcodeMain";
    // for ZBAR barcode
    private static final int ZBAR_SCANNER_REQUEST = 0;
    //private static final int ZBAR_QR_SCANNER_REQUEST = 1;

    // for wepoy device

    private final static String SCAN_ACTION = "urovo.rcv.message";

    private int type;
    private int outPut;


    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;
    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            txtPickUpWaybillNumber.setText("");

            // check symbologic settings
            if ((mScanManager.isSymbologySupported(Symbology.CODE39) || mScanManager.isSymbologySupported(Symbology.CODE39)) &&
                    ( mScanManager.isSymbologyEnabled(Symbology.CODE39) || mScanManager.isSymbologyEnabled(Symbology.CODE39)) ) {

                byte[] barcode = intent.getByteArrayExtra("barocode");
                //byte[] barcode = intent.getByteArrayExtra("barcode");
                int barocodelen = intent.getIntExtra("length", 0);
                //byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                //Log.d("Device Type : ", Global.globalDeviceType.toString());
                barcodeStr = new String(barcode, 0, barocodelen);

                txtPickUpWaybillNumber.setText(barcodeStr);

                mScanManager.stopDecode();

            }else{

                Global.AssetDialog("Invalid waybill!!!", getActivity()).create().show();
            }
        }

    };

    private void initScan() {
        // TODO Auto-generated method stub
        // check if device type is wepoy
        if (Global.globalDeviceType.toString().equals("WEPOY")){

            mScanManager = new ScanManager();
            // disable all symbologic
            mScanManager.enableAllSymbologies(false);
            // enable the needed symbologic
            mScanManager.enableSymbology(Symbology.CODE39, true);
            mScanManager.enableSymbology(Symbology.CODE39, true);
            // open the scanner
            mScanManager.openScanner();

            mScanManager.switchOutputMode( 0);
            soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
            soundid = soundpool.load("/etc/Scan_new.ogg", 1);
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_shipment_details, container, false);

        spnPickUpPackaging = (Spinner) rootView.findViewById(R.id.spnPickUpPackaging);
        spnPickUpBoxCrating = (Spinner) rootView.findViewById(R.id.spnPickUpBoxCrating);
        spnPickUpExpressCenter = (Spinner) rootView.findViewById(R.id.spnPickUpExpressCenter);

                // load spinners
                loadspnPickUpPackaging();
                loadspnPickUpBoxCrating();
                loadspnPickUpExpressCenter();

        txtPickUpWaybillNumber = (EditText) rootView.findViewById(R.id.txtPickUpWaybillNumber);
        txtPickupWeight = (EditText) rootView.findViewById(R.id.txtPickupWeight);
        txtPickupPieces = (EditText) rootView.findViewById(R.id.txtPickupPieces);
        txtDescription = (EditText) rootView.findViewById(R.id.txtDescription);
        txtPickUpDeclaredValue = (EditText) rootView.findViewById(R.id.txtPickUpDeclaredValue);
        txtPickUpInsurance = (EditText) rootView.findViewById(R.id.txtPickUpInsurance);
        txtPickUpCratingValue = (EditText) rootView.findViewById(R.id.txtPickUpCratingValue);
        //for camera barcode
        btnBarcode=(Button)rootView.findViewById(R.id.btnbarcode);

        txtPickUpWaybillNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(!txtPickUpWaybillNumber.getText().toString().matches("^([a-zA-Z0-9]+$)")
                        || txtPickUpWaybillNumber.getText().toString().length() < 8)
                {
                    //Global.AssetDialog("Invalid waybill number!!!", getActivity()).create().show();
                }else{
                    Global.globalPickupAwbno = txtPickUpWaybillNumber.getText().toString();
                }
                //Global.globalPickupAwbno = txtPickUpWaybillNumber.getText().toString();
            }
        });
        txtPickupWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupWeight = txtPickupWeight.getText().toString();

            }
        });
        txtPickupPieces.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupPieces = txtPickupPieces.getText().toString();

            }
        });
        txtDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupDescription = txtDescription.getText().toString();

            }
        });

        txtPickUpDeclaredValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupDeclaredValue = txtPickUpDeclaredValue.getText().toString();

            }
        });
        txtPickUpInsurance.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupInsurance = txtPickUpInsurance.getText().toString();

            }
        });
        txtPickUpCratingValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupCratingValue = txtPickUpCratingValue.getText().toString();

            }
        });

        spnPickUpPackaging.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String packaging = String.valueOf(parent.getItemAtPosition(position).toString());
                Global.globalPickupPackaging = packaging.toString();
//                if(!packaging.contentEquals(""))
//                {
//                    Global.globalPickupPackaging = packaging.toString();
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spnPickUpBoxCrating.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String box = String.valueOf(parent.getItemAtPosition(position).toString());
                Global.globalPickupBoxCrating = box.toString();
//                if(!box.contentEquals(""))
//                {
//                    Global.globalPickupBoxCrating = box.toString();
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spnPickUpExpressCenter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String express = String.valueOf(parent.getItemAtPosition(position).toString());
                Global.globalPickupExpressCenter = express.toString();
//                if(!express.contentEquals(""))
//                {
//                    Global.globalPickupExpressCenter = express.toString();
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // camera barcode reading
        btnBarcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                /* For google gms
                boolean autoFocus = true;
                boolean useFlash = true;
                Intent intent = new Intent(getActivity(), BarcodeCaptureActivity.class);

                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                */
                Intent intent = new Intent(getActivity(), ScannerActivity.class);
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

            }
        });
        return rootView;
    }
    private void loadspnPickUpPackaging() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getPackagingType(getActivity());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPickUpPackaging.setAdapter(dataAdapter);
    }

    private void loadspnPickUpBoxCrating() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getCratingType(getActivity());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPickUpBoxCrating.setAdapter(dataAdapter);
    }
    private void loadspnPickUpExpressCenter() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getExpressCenter(getActivity());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPickUpExpressCenter.setAdapter(dataAdapter);
    }
    @Override
    public void onResume() {
        Log.e("DEBUG", "onResume of LoginFragment");
        super.onResume();
        // check if device type is wepoy
        if (Global.globalDeviceType.toString().equals("WEPOY")){
            initScan();
            //txtAwbno.setText("");
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            getActivity().registerReceiver(mScanReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        Log.e("DEBUG", "OnPause of loginFragment");
        super.onPause();
        // check if device type is wepoy
        if (Global.globalDeviceType.toString().equals("WEPOY")){
            if(mScanManager != null) {
                mScanManager.stopDecode();
                isScaning = false;
            }
            getActivity().unregisterReceiver(mScanReceiver);
        }
    }

    /**
     * Called when an activity you launched exits i.e. when BarcodeCaptureActivity exits
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* old for google vision
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                   // Toast.makeText(this, R.string.barcode_success, Toast.LENGTH_SHORT).show();

                    txtPickUpWaybillNumber.setText(barcode.displayValue);
                    Global.globalPickupAwbno = txtPickUpWaybillNumber.getText().toString();
                    //processWaybillFromCamera();
                    // Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    //Toast.makeText(this, R.string.barcode_failure, Toast.LENGTH_SHORT).show();

                    // Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                //Toast.makeText(this, String.format(getString(R.string.barcode_error),
                 //       CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        if (requestCode == ZBAR_SCANNER_REQUEST) {
                if (resultCode == -1 )
                {
                    // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
                    // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
                    //Toast.makeText(this, "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT), Toast.LENGTH_SHORT).show();
                    //Toast.makeText(this, "Scan Result Type = " + data.getIntExtra(ZBarConstants.SCAN_RESULT_TYPE, 0), Toast.LENGTH_SHORT).show();

                    txtPickUpWaybillNumber.setText(data.getStringExtra("BarcodeData"));
                    if(!txtPickUpWaybillNumber.getText().toString().matches("^([a-zA-Z0-9]+$)") || txtPickUpWaybillNumber.getText().toString().length() < 8)
                    {
                        Global.AssetDialog("Invalid waybill number!!!", getActivity()).create().show();
                    }else{
                        Global.globalPickupAwbno = txtPickUpWaybillNumber.getText().toString();
                    }
                    // The value of type indicates one of the symbols listed in Advanced Options below.
                } else {
                    Toast.makeText(getActivity(), "Invalid waybill!!!", Toast.LENGTH_SHORT).show();
                }
        }else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
