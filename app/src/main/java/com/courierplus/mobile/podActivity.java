package com.courierplus.mobile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.device.scanner.configuration.Symbology;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import barcodereader.BarcodeCaptureActivity;
import db.DataDB;
import me.dm7.barcodescanner.scanner.ScannerActivity;


import android.device.ScanManager;
import android.content.BroadcastReceiver;
import android.media.AudioManager;
import android.media.SoundPool;
import android.content.IntentFilter;

public class podActivity extends AppCompatActivity {
    private Spinner spnPodOrigin, spnPodStatus, spnPodDeliveryStation;
    private EditText  txtPODBy, txtAwbno;//txtFirstDate,
    private TextView txtTotalPOD,txtExitDate;

    private Button btnSavePod,btnSignature,btnBarcode;
    private CheckBox chkMultiplePOD;
    private ProgressDialog mProgressView;
    private TableLayout tbDetails;
    private int year;
    private int month;
    private int day;
    private TextView Output;
    private Button changeDate;
    static final int DATE_PICKER_ID = 1111;
    public int podCount = 0;
    DataDB db;


    // for google camera barcode
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

    String currentDateandTime = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date());

    private BroadcastReceiver mScanReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            isScaning = false;
            soundpool.play(soundid, 1, 1, 0, 0, 1);
            txtAwbno.setText("");

            // check symbologic settings
            if ((mScanManager.isSymbologySupported(Symbology.CODE39) || mScanManager.isSymbologySupported(Symbology.CODE39)) &&
                    ( mScanManager.isSymbologyEnabled(Symbology.CODE39) || mScanManager.isSymbologyEnabled(Symbology.CODE39)) ) {

                byte[] barcode = intent.getByteArrayExtra("barocode");
                //byte[] barcode = intent.getByteArrayExtra("barcode");
                int barocodelen = intent.getIntExtra("length", 0);
                //byte temp = intent.getByteExtra("barcodeType", (byte) 0);
                //Log.d("Device Type : ", Global.globalDeviceType.toString());
                barcodeStr = new String(barcode, 0, barocodelen);

                txtAwbno.setText(barcodeStr);
                mScanManager.stopDecode();
                if(!txtAwbno.getText().toString().matches("^([a-zA-Z0-9]+$)") || txtAwbno.getText().toString().length() < 8)
                {
                    Global.AssetDialog("Invalid waybill number!!!", podActivity.this).create().show();
                }else{
                    processWaybill();
                }
            }else{

                Global.AssetDialog("Invalid waybill!!!", podActivity.this).create().show();
            }
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pod);
        db=new DataDB();



        //set controls
        spnPodStatus = (Spinner) findViewById(R.id.spnPodStatus);
        spnPodOrigin = (Spinner) findViewById(R.id.spnPodOrigin);
        spnPodDeliveryStation = (Spinner) findViewById(R.id.spnPodDeliveryStation);


        txtAwbno = (EditText) findViewById(R.id.txtPodAwbno);
        //txtFirstDate = (EditText) findViewById(R.id.txtFirstDate);
        txtPODBy = (EditText) findViewById(R.id.txtRecievedBy);
        txtExitDate = (TextView) findViewById(R.id.txtExitDate);
        txtTotalPOD = (TextView) findViewById(R.id.txtTotalPOD);
        tbDetails = (TableLayout) findViewById(R.id.tbPodDetails);
        //for camera barcode
        btnBarcode=(Button)findViewById(R.id.btnbarcode);
        //txtExitDate.setEnabled(false);
       // txtFirstDate.setEnabled(false);
        //txtExitDate.setText(currentDateandTime.toString());
        btnSavePod = (Button) findViewById(R.id.btnSavePod);
        btnSignature = (Button)findViewById(R.id.btnSignature);
        btnSignature.setVisibility(View.INVISIBLE);
        chkMultiplePOD = (CheckBox) findViewById(R.id.chkMultiplePOD);
        changeDate = (Button) findViewById(R.id.changeDate);

// Get current date by calender

        final Calendar c = Calendar.getInstance();
        year  = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day   = c.get(Calendar.DAY_OF_MONTH);

        // Show current date

        txtExitDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(year).append("-").append(day).append("-").append(month + 1));


        // Button listener to show date picker dialog

        changeDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // On button click show datepicker dialog
                showDialog(DATE_PICKER_ID);


            }

        });



       // txtFirstDate.setText(currentDateandTime.toString());
        Global.globalMultiplePOD = "No";
        //load drop down
        loadSpnOrigin();
        loadSpnPodStatus();
        loadAvailablePOD();


        // check airwaybill scanned
        txtAwbno.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                //((event.getAction()==KeyEvent.ACTION_DOWN) && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) ||
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {


                    Global.globalPodAwbno = txtAwbno.getText().toString();
                    Global.globalPodOrigin = String.valueOf(spnPodOrigin.getSelectedItem());
                    Global.globalPodStatus = String.valueOf(spnPodStatus.getSelectedItem());
                    Global.globalPodDeliveryStation = String.valueOf(spnPodDeliveryStation.getSelectedItem());

                    Global.globalPodExitDate = txtExitDate.getText().toString();
                    // Global.globalPodFirstDate = txtFirstDate.getText().toString();
                    Global.globalPodBy = txtPODBy.getText().toString();
                    // validate waybill
                    if(!Global.globalPodAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPodAwbno.toString().length() < 8)
                    {
                        Global.AssetDialog("Invalid waybill number!!!", podActivity.this).create().show();
                        txtAwbno.requestFocus();
                        return true;
                    }

                    if(Global.globalPodExitDate.toString().equals(""))
                    {
                        Global.AssetDialog("Exit date is required!", podActivity.this).create().show();
                        txtExitDate.requestFocus();
                        return true;
                    }
                    if(Global.globalPodStatus.toString().equals("") || Global.globalPodStatus.toString().equals(" "))
                    {
                        Global.AssetDialog("Status is required!", podActivity.this).create().show();
                        spnPodStatus.requestFocus();
                        return true;
                    }
                    if(Global.globalPodBy.toString().equals("")) {
                        Global.AssetDialog("Received By is required!", podActivity.this).create().show();
                        txtPODBy.requestFocus();
                        return true;
                    }
                    if(Global.globalPodOrigin.toString().equals("") || Global.globalPodOrigin.toString().equals(" "))
                    {
                        Global.AssetDialog("Origin is required!", podActivity.this).create().show();
                        spnPodOrigin.requestFocus();
                        return true;
                    }
                    if(Global.globalPodDeliveryStation.toString().equals("") || Global.globalPodDeliveryStation.toString().equals(" "))
                    {
                        Global.AssetDialog("Delivery Station is required!", podActivity.this).create().show();
                        spnPodDeliveryStation.requestFocus();
                        return true;
                    }
                    if(txtAwbno.getText().toString().equals("") || txtAwbno.getText().toString().equals(" "))
                    {

                        Global.AssetDialog("AirWay bill number is required!!", podActivity.this).create().show();
                        txtAwbno.requestFocus();
                        return true;
                    }
                    if(db.checkAWBNOPOD(podActivity.this,txtAwbno.getText().toString()))
                    {
                        Global.AssetDialog("AirWay bill number already Exist.!!", podActivity.this).create().show();
                        txtAwbno.setText("");
                        txtAwbno.requestFocus();
                        return true;
                    }else{
                        if( Global.globalMultiplePOD.toString().equals("No")) {

                            txtExitDate.clearFocus();
                            btnSavePod.setVisibility(View.VISIBLE);
                            btnSignature.setVisibility(View.VISIBLE);
                        }else{
                            btnSignature.setVisibility(View.INVISIBLE);
                            mProgressView = ProgressDialog.show(podActivity.this, "", "Please wait...", true);
                            String sql;
                            String currentDateandTime = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date());
                            sql = "INSERT INTO history_pod" +
                                    "(waybillnumber,pod,podby,first_date,dexcodeid," +
                                    "poddate,OriginStation,pod_post_date," +
                                    "DeliveryStation,DeliveryRemarks,outstation_Transfer" +
                                    ")VALUES('" + Global.globalPodAwbno+ "',"+
                                    "'" + Global.globalPodBy + "',"+
                                    "'" + Global.globalUserName + "',"+
                                    "'" + currentDateandTime.toString() + "',"+
                                    "'" + db.getDEXCode(podActivity.this,Global.globalPodStatus) + "',"+
                                    "'" + Global.globalPodExitDate + "',"+
                                    "'" + Global.globalPodOrigin + "',"+
                                    "'" + currentDateandTime.toString() + "',"+
                                    "'" + Global.globalPodDeliveryStation + "',"+
                                    "'" + Global.globalPodRemarks + "',"+
                                    "'N')";
                            if (db.dynamicInsert(podActivity.this,sql)) {
                                //Log.d("Response: ", "> " + jsonStr);
                                // save signature for multiple pod
                                db.insertSignature(podActivity.this, Global.globalMultipleSignature);
                                handler.sendEmptyMessage(0);

                                appendDetailsPOD(tbDetails,Global.globalPodAwbno,Global.globalPodOrigin,Global.globalPodBy, currentDateandTime.toString());

                                //loadAvailablePOD();
                                txtTotalPOD.setText(String.valueOf(podCount+1));
                                txtAwbno.setText("");
                                txtAwbno.requestFocus();
                                //Global.AssetDialog("Record saved!!", podActivity.this).create().show();
                                // resetValues();
                            }else{
                                handler.sendEmptyMessage(0);
                                Global.AssetDialog("Error while trying to  save scan record. /n please try again.!!", podActivity.this).create().show();
                            }
                        }
                    }

                    return true;
                } else {
                    return false;
                }
            }
    });
        // check status
        spnPodStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Status = String.valueOf(parent.getItemAtPosition(position).toString());
                if(!Status.contentEquals("PERFECT DELIVERY"))
                {
                    txtPODBy.setEnabled(false);
                    txtPODBy.setText(Status);
                }else{
                    txtPODBy.setEnabled(true);
                    txtPODBy.setText("");
                    txtPODBy.requestFocus();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        // event for save

        btnSavePod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Global.globalPodAwbno = txtAwbno.getText().toString();
                Global.globalPodOrigin = String.valueOf(spnPodOrigin.getSelectedItem());
                Global.globalPodStatus = String.valueOf(spnPodStatus.getSelectedItem());
                Global.globalPodDeliveryStation = String.valueOf(spnPodDeliveryStation.getSelectedItem());

                Global.globalPodExitDate = txtExitDate.getText().toString();
                Global.globalPodBy = txtPODBy.getText().toString();

                //Log.d(TAG, "validation code : " + Global.isWaybillValid(Global.globalPodAwbno.toString()));
                //Log.d(TAG, "text lenght : " + Global.globalPodAwbno.toString().length());
                // validate waybill
                //if(!Global.isWaybillValid(Global.globalPodAwbno.toString()))
                if(!Global.globalPodAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPodAwbno.toString().length() < 8)
                {
                    Global.AssetDialog("Invalid waybill number!!!", podActivity.this).create().show();
                    txtAwbno.requestFocus();
                    return;
                }

                if(Global.globalPodExitDate.toString().equals(""))
                {
                    Global.AssetDialog("Exit date is required!", podActivity.this).create().show();
                    txtExitDate.requestFocus();
                    return ;
                }
                if(Global.globalPodStatus.toString().equals("") || Global.globalPodStatus.toString().equals(" "))
                {
                    Global.AssetDialog("Status is required!", podActivity.this).create().show();
                    spnPodStatus.requestFocus();
                    return ;
                }
                if(Global.globalPodBy.toString().equals("")) {
                    Global.AssetDialog("Received By is required!", podActivity.this).create().show();
                    txtPODBy.requestFocus();
                    return ;
                }
                if(Global.globalPodOrigin.toString().equals("") || Global.globalPodOrigin.toString().equals(" "))
                {
                    Global.AssetDialog("Origin is required!", podActivity.this).create().show();
                    spnPodOrigin.requestFocus();
                    return ;
                }
                if(Global.globalPodDeliveryStation.toString().equals("") || Global.globalPodDeliveryStation.toString().equals(" "))
                {
                    Global.AssetDialog("Delivery Station is required!", podActivity.this).create().show();
                    spnPodDeliveryStation.requestFocus();
                    return ;
                }
                if(txtAwbno.getText().toString().equals("") || txtAwbno.getText().toString().equals(" "))
                {
                    Global.AssetDialog("AirWay bill number is required!!", podActivity.this).create().show();
                    txtAwbno.requestFocus();
                    return ;
                }
                if(db.checkAWBNOPOD(podActivity.this,txtAwbno.getText().toString()))
                {
                    Global.AssetDialog("AirWay bill number already Exist.!!", podActivity.this).create().show();
                    txtAwbno.setText("");
                    txtAwbno.requestFocus();
                    return;
                }else{
                    mProgressView = ProgressDialog.show(podActivity.this, "", "Please wait...", true);
                    if (Global.globalPodStatus.equals("PERFECT DELIVERY") &&
                            db.checkAWBNOSignature(podActivity.this,Global.globalPodAwbno) == false) {
                        handler.sendEmptyMessage(0);
                        Global.AssetDialog("Please capture Signature for this Waybill number.", podActivity.this).create().show();

                        btnSignature.setVisibility(View.VISIBLE);
                        return;
                    }else {
                        String sql;
                        String currentDateandTime = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date());
                        sql = "INSERT INTO history_pod" +
                                "(waybillnumber,pod,podby,first_date,dexcodeid," +
                                "poddate,OriginStation,pod_post_date," +
                                "DeliveryStation,DeliveryRemarks,outstation_Transfer" +
                                ")VALUES('" + Global.globalPodAwbno+ "',"+
                                "'" + Global.globalPodBy + "',"+
                                "'" + Global.globalUserName + "',"+
                                "'" + currentDateandTime.toString() + "',"+
                                "'" + db.getDEXCode(podActivity.this,Global.globalPodStatus) + "',"+
                                "'" + Global.globalPodExitDate + "',"+
                                "'" + Global.globalPodOrigin + "',"+
                                "'" + currentDateandTime.toString() + "',"+
                                "'" + Global.globalPodDeliveryStation + "',"+
                                "'" + Global.globalPodRemarks + "',"+
                                "'N')";
                        if (db.dynamicInsert(podActivity.this,sql)) {
                            handler.sendEmptyMessage(0);
                            appendDetailsPOD(tbDetails,Global.globalPodAwbno,Global.globalPodOrigin,Global.globalPodBy,currentDateandTime.toString());

                            txtTotalPOD.setText(String.valueOf(podCount+1));
                            Global.AssetDialog("Record saved!!", podActivity.this).create().show();
                            btnSignature.setVisibility(View.INVISIBLE);
                            resetValues();
                        }else{
                            handler.sendEmptyMessage(0);
                            Global.AssetDialog("Error while trying to  save scan record. /n please try again.!!", podActivity.this).create().show();
                        }
                    }

                }// end of aiwaybill check
               // handler.sendEmptyMessage(0);
            }
        });
        chkMultiplePOD.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
              @Override
              public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                          if (isChecked) {
                              Global.globalMultiplePOD = "Yes";
                              btnSavePod.setVisibility(View.INVISIBLE);
                              btnSignature.setVisibility(View.INVISIBLE);
                              mProgressView = ProgressDialog.show(podActivity.this, "", "Please wait...", true);
                              //Intent i = new Intent(podActivity.this, signatureActivity.class);
                              Intent i = new Intent(podActivity.this, CaptureSignature.class);
                              startActivity(i);
                              // clear progress
                              handler.sendEmptyMessage(0);
                          }
                          else {
                              Global.globalMultiplePOD = "No";
                              btnSavePod.setVisibility(View.VISIBLE);
                              btnSignature.setVisibility(View.VISIBLE);
                          }

                      }
              }
        );
        // event for signature button

        btnSignature.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                validateControls();

                if (Global.globalPodStatus.equals("PERFECT DELIVERY")) {
                    if( Global.globalMultiplePOD.equals("No")) {
                        if (db.checkAWBNOSignature(podActivity.this, Global.globalPodAwbno)) {
                            Global.AssetDialog("Signature already exist for the Waybill number.", podActivity.this).create().show();
                        } else {
                            mProgressView = ProgressDialog.show(podActivity.this, "", "Please wait...", true);
                            //Intent i = new Intent(podActivity.this, signatureActivity.class);
                            Intent i = new Intent(podActivity.this, CaptureSignature.class);
                            startActivity(i);
                            // clear progress
                            handler.sendEmptyMessage(0);
                        }
                    }else{


                    }

                }else {
                    Global.AssetDialog("Signature can only be taken for Perfect Delivery!", podActivity.this).create().show();
                }
            }
        });

        // camera barcode reading
        btnBarcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                /* code for gooogle gms vision barcode reader
                boolean autoFocus = true;

                boolean useFlash = true;
               // boolean autoCapture = true;
                //Intent intent = new Intent(podActivity.this, BarcodeCaptureActivity.class);

                //intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus);
                //intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash);
                //intent.putExtra(BarcodeCaptureActivity.AutoCapture, autoCapture);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                */
                Intent intent = new Intent(podActivity.this, ScannerActivity.class);
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

            }
        });
    }
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_PICKER_ID:

                // open datepicker dialog.
                // set date picker for current date
                // add pickerListener listner to date picker
                return new DatePickerDialog(this, pickerListener, year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener pickerListener = new DatePickerDialog.OnDateSetListener() {

        // when dialog box is closed, below method will be called.
        @Override
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {

            year  = selectedYear;
            month = selectedMonth;
            day   = selectedDay;

            // Show selected date
            txtExitDate.setText(new StringBuilder().append(year)
                    .append("-").append(day).append("-").append(month + 1)
                    .append(" "));

        }
    };

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
        spnPodOrigin.setAdapter(dataAdapter);
        spnPodDeliveryStation.setAdapter(dataAdapter);
    }
    private void loadSpnPodStatus() {
        // database handler
        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getPodStatus(this);
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPodStatus.setAdapter(dataAdapter);
    }
    private void loadAvailablePOD() {
        // database handler
        db = new DataDB();
        // Spinner Drop down elements

        String awbNo, origin, receivedBy, podDate;

        Cursor cursorPOD = db.getPODNotUploaded(podActivity.this);
        if (cursorPOD != null ) {
            if (cursorPOD.moveToFirst()) {

                do {
                    awbNo = cursorPOD.getString(cursorPOD.getColumnIndex("waybillnumber"));
                    origin = cursorPOD.getString(cursorPOD.getColumnIndex("OriginStation"));
                    receivedBy = cursorPOD.getString(cursorPOD.getColumnIndex("pod"));
                    podDate = cursorPOD.getString(cursorPOD.getColumnIndex("pod_post_date"));

                    appendDetailsPOD(tbDetails,awbNo,origin,receivedBy,podDate);
                    podCount+=1;
                    // reset the strings
//                    awbNo = "";
//                    origin = "";
//                    receivedBy = "";
//                    podDate = "";

                }while (cursorPOD.moveToNext());
            }

        }
        txtTotalPOD.setText(String.valueOf(podCount));
    }
    public void setValues(){
    Global.globalPodAwbno = txtAwbno.getText().toString();
    Global.globalPodOrigin = String.valueOf(spnPodOrigin.getSelectedItem());
    Global.globalPodStatus = String.valueOf(spnPodStatus.getSelectedItem());

    Global.globalPodExitDate = txtExitDate.getText().toString();
   // Global.globalPodFirstDate = txtFirstDate.getText().toString();
    Global.globalPodBy = txtPODBy.getText().toString();
}
    public void resetValues(){
        txtAwbno.setText("");
        txtAwbno.requestFocus();
       // txtExitDate.setText(currentDateandTime.toString());
       // txtFirstDate.setText(currentDateandTime.toString());
        txtPODBy.setText("");
        spnPodOrigin.setSelection(0);
        spnPodStatus.setSelection(0);

        Global.globalPodAwbno = "";
        Global.globalPodOrigin = "";
        Global.globalPodStatus = "";
        Global.globalPodExitDate = "";
        Global.globalPodFirstDate = "";
        Global.globalPodBy = "";

    }
    protected void validateControls(){
        // set controls values
        setValues();
        // validate controls
        // validate waybill
        if(!Global.globalPodAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPodAwbno.toString().length() < 8)
        {
            Global.AssetDialog("Invalid waybill number!!!", podActivity.this).create().show();
            txtAwbno.requestFocus();
            return;
        }
        if(Global.globalPodExitDate.toString().equals(""))
        {
            Global.AssetDialog("Exit date is required!", podActivity.this).create().show();
            txtExitDate.requestFocus();
            return;
        }
        if(Global.globalPodStatus.toString().equals("") || Global.globalPodStatus.toString().equals(" "))
        {
            Global.AssetDialog("Status is required!", podActivity.this).create().show();
            spnPodStatus.requestFocus();
            return;
        }
        if(Global.globalPodBy.toString().equals("")) {
            Global.AssetDialog("Received By is required!", podActivity.this).create().show();
            txtPODBy.requestFocus();
            return;
        }
//        if(Global.globalPodFirstDate.equals(""))
//        {
//            Global.AssetDialog("FirstDate is required!", podActivity.this).create().show();
//            txtFirstDate.requestFocus();
//            return;
//        }
        if(Global.globalPodOrigin.toString().equals("") || Global.globalPodOrigin.toString().equals(" "))
        {
            Global.AssetDialog("Origin is required!", podActivity.this).create().show();
            spnPodOrigin.requestFocus();
            return;
        }
//        if(Global.globalPodAwbno.toString().equals("") || Global.globalPodAwbno.equals(" "))
//        {
//            Global.AssetDialog("Airway Bill number is required!", podActivity.this).create().show();
//            txtAwbno.requestFocus();
//            return;
//        }

    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            mProgressView.dismiss();
        }
    };

    private void appendDetailsPOD(TableLayout table, String Text1, String Text2, String Text3, String Text4) {

        View sep2 = new View(this);
        TableLayout.LayoutParams p2 = new TableLayout.LayoutParams();
        p2.height=1;
        p2.topMargin=10;
        sep2.setBackgroundColor(0xFF000000);
        sep2.setLayoutParams(p2);

        table.addView(sep2);

        TableRow row = new TableRow(this);

        TextView col1 = new TextView(this);
        TextView col2 = new TextView(this);
        TextView col3 = new TextView(this);
        TextView col4 = new TextView(this);

        col1.setText(Text1.trim());
        col1.setTextSize(9);
        col1.setPadding(3, 3, 3, 3);
        col1.setTextColor(Color.BLACK);

        col2.setText(Text2.trim());
        col2.setTextSize(9);
        col2.setPadding(3, 3, 3, 3);
        col2.setTextColor(Color.BLACK);

        col3.setText(Text3.trim());
        col3.setTextSize(9);
        col3.setPadding(3, 3, 3, 3);
        col3.setTextColor(Color.BLACK);

        col4.setText(Text4.trim());
        col4.setTextSize(9);
        col4.setPadding(3, 3, 3, 3);
        col4.setTextColor(Color.BLACK);

        TableRow.LayoutParams params1 = new TableRow.LayoutParams();
        params1.span = 2;
        params1.column = 0;


        row.addView(col1, new TableRow.LayoutParams(0));
        row.addView(col2, new TableRow.LayoutParams());
        row.addView(col3, new TableRow.LayoutParams());
        row.addView(col4, new TableRow.LayoutParams());

        table.addView(row, new TableLayout.LayoutParams());

        table.setColumnStretchable(0, true);

        View sep1 = new View(this);
        TableLayout.LayoutParams p1 = new TableLayout.LayoutParams();
        p1.height=1;
        sep1.setBackgroundColor(0xFF000000);
        sep1.setLayoutParams(p1);

        table.addView(sep1);

    }

    /**
     * Called when an activity you launched exits i.e. when BarcodeCaptureActivity exits
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       /*// for Google Vision barcode
        if (requestCode == ZBAR_SCANNER_REQUEST) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Toast.makeText(this, R.string.barcode_success, Toast.LENGTH_SHORT).show();

                    txtAwbno.setText(barcode.displayValue);
                    processWaybill();
                   // Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Toast.makeText(this, R.string.barcode_failure, Toast.LENGTH_SHORT).show();

                   // Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        Log.d(TAG, "No barcode captured, intent data is null");*/

        if (requestCode == ZBAR_SCANNER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "OK RESULT");
                // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
                // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
                //Toast.makeText(this, "Scan Result = " + data.getStringExtra(rawResult.getContents().toString()), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Scan Result Type = " + data.getIntExtra(rawResult.getBarcodeFormat().getName(), 0), Toast.LENGTH_SHORT).show();

                String result = data.getStringExtra("BarcodeData");
                txtAwbno.setText(result);
                // validate waybill
                if(!result.toString().matches("^([a-zA-Z0-9]+$)") || result.toString().length() < 8)
                {
                    Global.AssetDialog("Invalid waybill number!!!", podActivity.this).create().show();
                }else{
                    processWaybill();
                }
                // The value of type indicates one of the symbols listed in Advanced Options below.
            } else {
                Toast.makeText(this, "Invalid waybill!!!", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Invalid waybill!!!", Toast.LENGTH_SHORT).show();
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void processWaybill() {


        Global.globalPodAwbno = txtAwbno.getText().toString();
        Global.globalPodOrigin = String.valueOf(spnPodOrigin.getSelectedItem());
        Global.globalPodStatus = String.valueOf(spnPodStatus.getSelectedItem());
        Global.globalPodDeliveryStation = String.valueOf(spnPodDeliveryStation.getSelectedItem());

        Global.globalPodExitDate = txtExitDate.getText().toString();
        // Global.globalPodFirstDate = txtFirstDate.getText().toString();
        Global.globalPodBy = txtPODBy.getText().toString();

        // validate waybill
        //if(!Global.globalPodAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPodAwbno.toString().length() < 8)
        //{
        //    Global.AssetDialog("Invalid waybill number!!!", podActivity.this).create().show();
        //    txtAwbno.requestFocus();
        //    return;
        //}

        if(Global.globalPodExitDate.toString().equals(""))
        {
            Global.AssetDialog("Exit date is required!", podActivity.this).create().show();
            txtExitDate.requestFocus();
            return ;
        }
        if(Global.globalPodStatus.toString().equals("") || Global.globalPodStatus.toString().equals(" "))
        {
            Global.AssetDialog("Status is required!", podActivity.this).create().show();
            spnPodStatus.requestFocus();
            return ;
        }
        if(Global.globalPodBy.toString().equals("")) {
            Global.AssetDialog("Received By is required!", podActivity.this).create().show();
            txtPODBy.requestFocus();
            return ;
        }
        if(Global.globalPodOrigin.toString().equals("") || Global.globalPodOrigin.toString().equals(" "))
        {
            Global.AssetDialog("Origin is required!", podActivity.this).create().show();
            spnPodOrigin.requestFocus();
            return ;
        }
        if(Global.globalPodDeliveryStation.toString().equals("") || Global.globalPodDeliveryStation.toString().equals(" "))
        {
            Global.AssetDialog("Delivery Station is required!", podActivity.this).create().show();
            spnPodDeliveryStation.requestFocus();
            return ;
        }
        if(txtAwbno.getText().toString().equals("") || txtAwbno.getText().toString().equals(" "))
        {

            Global.AssetDialog("AirWay bill number is required!!", podActivity.this).create().show();
            txtAwbno.requestFocus();
            return ;
        }
        if(db.checkAWBNOPOD(podActivity.this,txtAwbno.getText().toString()))
        {
            Global.AssetDialog("AirWay bill number already Exist.!!", podActivity.this).create().show();
            txtAwbno.setText("");
            txtAwbno.requestFocus();
            return ;
        }else{
            if( Global.globalMultiplePOD.toString().equals("No")) {

                txtExitDate.clearFocus();
                btnSavePod.setVisibility(View.VISIBLE);
                btnSignature.setVisibility(View.VISIBLE);
            }else{
                btnSignature.setVisibility(View.INVISIBLE);
                mProgressView = ProgressDialog.show(podActivity.this, "", "Please wait...", true);
                String sql;
                String currentDateandTime = new SimpleDateFormat("yyyy-dd-MM HH:mm:ss").format(new Date());
                sql = "INSERT INTO history_pod" +
                        "(waybillnumber,pod,podby,first_date,dexcodeid," +
                        "poddate,OriginStation,pod_post_date," +
                        "DeliveryStation,DeliveryRemarks,outstation_Transfer" +
                        ")VALUES('" + Global.globalPodAwbno+ "',"+
                        "'" + Global.globalPodBy + "',"+
                        "'" + Global.globalUserName + "',"+
                        "'" + currentDateandTime.toString() + "',"+
                        "'" + db.getDEXCode(podActivity.this,Global.globalPodStatus) + "',"+
                        "'" + Global.globalPodExitDate + "',"+
                        "'" + Global.globalPodOrigin + "',"+
                        "'" + currentDateandTime.toString() + "',"+
                        "'" + Global.globalPodDeliveryStation + "',"+
                        "'" + Global.globalPodRemarks + "',"+
                        "'N')";
                if (db.dynamicInsert(podActivity.this,sql)) {
                    //Log.d("Response: ", "> " + jsonStr);
                    // save signature for multiple pod
                    db.insertSignature(podActivity.this, Global.globalMultipleSignature);
                    handler.sendEmptyMessage(0);

                    appendDetailsPOD(tbDetails,Global.globalPodAwbno,Global.globalPodOrigin,Global.globalPodBy, currentDateandTime.toString());

                    //loadAvailablePOD();
                    txtTotalPOD.setText(String.valueOf(podCount+1));
                    txtAwbno.setText("");
                    txtAwbno.requestFocus();
                    //Global.AssetDialog("Record saved!!", podActivity.this).create().show();
                    // resetValues();
                }else{
                    handler.sendEmptyMessage(0);
                    Global.AssetDialog("Error while trying to  save scan record. /n please try again.!!", podActivity.this).create().show();
                }
            }
        }


    }

    // code for wepoy
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
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        // check if device type is wepoy
        if (Global.globalDeviceType.toString().equals("WEPOY")){
            initScan();
            //txtAwbno.setText("");
            IntentFilter filter = new IntentFilter();
            filter.addAction(SCAN_ACTION);
            registerReceiver(mScanReceiver, filter);
        }
    }
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

        // check if device type is wepoy
        if (Global.globalDeviceType.toString().equals("WEPOY")){
            if(mScanManager != null) {
                mScanManager.stopDecode();
                isScaning = false;
            }
            unregisterReceiver(mScanReceiver);
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyDown(keyCode, event);

    }
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return super.onKeyUp(keyCode, event);

    }
}
