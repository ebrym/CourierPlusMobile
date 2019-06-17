package com.courierplus.mobile;

/**
 * Created by iabdullahi on 5/26/2016.
 */
        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.device.scanner.configuration.Symbology;
        import android.graphics.Color;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.View;

        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TableLayout;
        import android.widget.TableRow;
        import android.widget.TableLayout.LayoutParams;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.common.api.CommonStatusCodes;
        import com.google.android.gms.vision.barcode.Barcode;

        import java.text.SimpleDateFormat;
        import java.util.Date;

        import barcodereader.BarcodeCaptureActivity;
        import db.DataDB;
        import me.dm7.barcodescanner.scanner.ScannerActivity;

        import android.device.ScanManager;
        import android.device.scanner.configuration.Triggering;
        import android.content.BroadcastReceiver;
        import android.media.AudioManager;
        import android.media.SoundPool;
        import android.content.IntentFilter;

public class ScanDetailsActivity extends AppCompatActivity {
    private EditText txtAwbno;
    private TextView lblScanCount;
    private Button btnSave;
    private TableLayout tbDetails;
    int scanCount =0;
    DataDB db;
    private Button btnBarcode;
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
                            Global.AssetDialog("Invalid waybill number!!!", ScanDetailsActivity.this).create().show();
                        }else{
                            processWaybillFromCamera();
                        }
            }else{

                Global.AssetDialog("Invalid waybill!!!", ScanDetailsActivity.this).create().show();
            }

            //Log.d("debug", "----HERE Receiver--" + temp);
        }

    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scandetails);



        db=new DataDB();
        // set controls


        txtAwbno = (EditText) findViewById(R.id.txtAwbno);
        btnSave = (Button) findViewById(R.id.btnSave);
        tbDetails = (TableLayout) findViewById(R.id.tbDetails);
        lblScanCount = (TextView) findViewById(R.id.lblScanCount);

        lblScanCount.setText(String.valueOf(scanCount));

        //for camera barcode
        btnBarcode=(Button)findViewById(R.id.btnbarcode);


        txtAwbno.setOnKeyListener(new View.OnKeyListener(){

            public boolean onKey(View v, int keyCode, KeyEvent event){
                //((event.getAction()==KeyEvent.ACTION_DOWN) && (event.getKeyCode()==KeyEvent.KEYCODE_ENTER)) ||
                if(event.getKeyCode()== KeyEvent.KEYCODE_ENTER && event.getAction()== KeyEvent.ACTION_UP )
                {
                    // begin of airwaybillcheck
                if(txtAwbno.getText().toString().equals("") || txtAwbno.getText().toString().equals(" ")
                        || !txtAwbno.getText().toString().matches("^([a-zA-Z0-9]+$)") || txtAwbno.getText().toString().length() < 8)
                {
                    Global.AssetDialog("Invalid Airway bill number!!!", ScanDetailsActivity.this).create().show();
                    txtAwbno.requestFocus();
                    return true;
                }else{
                    if(db.checkAWBNOSCans(ScanDetailsActivity.this,txtAwbno.getText().toString(),Global.globalScanStatus.toString()))
                    {
                        Global.AssetDialog("AirWay bill number already Exist.!!", ScanDetailsActivity.this).create().show();
                        txtAwbno.setText("");
                        txtAwbno.requestFocus();
                        return true;
                    }else{
                        String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                        String sql;
                        sql = "INSERT INTO SCANS" +
                                "(AWBNO,SCAN_STATUS,ORIGIN,DESTINATION,[DATE],WEIGHT," +
                                "BATCHNO,USERID,PIECES,SEALNO,VEHICLENO,TAG,ROUTE," +
                                "CONTENT_TYPE,TransferStatus" +
                                ")VALUES('" + txtAwbno.getText().toString()+ "',"+
                                "'" + Global.globalScanStatus + "',"+
                                "'" + Global.globalOrigin + "',"+
                                "'" + Global.globalDestination + "',"+
                                "'" + currentDateandTime.toString() + "',"+
                                "'" + Global.globalWeight + "',"+
                                "'" + Global.globalBatchNo + "',"+
                                "'" + Global.globalUserName + "',"+
                                "'" + Global.globalPieces + "',"+
                                "'" + Global.globalSealNo + "',"+
                                "'" + Global.globalVehicleNo + "',"+
                                "'" + Global.globalTagNo + "',"+
                                "'" + Global.globalRoute + "',"+
                                "'" + Global.globalContentType + "',"+
                                "'N')";

                        if (db.dynamicInsert(ScanDetailsActivity.this,sql)) {
                            appendDetailsTop(tbDetails,txtAwbno.getText().toString().trim(),Global.globalWeight,Global.globalOrigin,Global.globalDestination);
                            scanCount+=1;
                            lblScanCount.setText(String.valueOf(scanCount));
                            txtAwbno.setText("");
                            txtAwbno.requestFocus();
                        }else{
                            Global.AssetDialog("Error while trying to  save scan record. /n please try again.!!", ScanDetailsActivity.this).create().show();
                        }
                    }

                    return true;

                }// end of airwaybill check

                }else{
                return false;}
            }

        });


        // camera barcode reading
        btnBarcode.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                boolean autoFocus = true;
                boolean useFlash = true;
                //boolean autoCapture = true;
               /* Intent intent = new Intent(ScanDetailsActivity.this, BarcodeCaptureActivity.class);

                intent.putExtra(BarcodeCaptureActivity.AutoFocus, autoFocus);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, useFlash);
                //intent.putExtra(BarcodeCaptureActivity.AutoCapture, autoCapture);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
                */
                Intent intent = new Intent(ScanDetailsActivity.this, ScannerActivity.class);
                startActivityForResult(intent, ZBAR_SCANNER_REQUEST);

            }
        });


    }
    /**
     * Called when an activity you launched exits i.e. when BarcodeCaptureActivity exits
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

       /* if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Toast.makeText(this, R.string.barcode_success, Toast.LENGTH_SHORT).show();

                    txtAwbno.setText(barcode.displayValue);
                    processWaybillFromCamera();
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Toast.makeText(this, R.string.barcode_failure, Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                Toast.makeText(this, String.format(getString(R.string.barcode_error),
                        CommonStatusCodes.getStatusCodeString(resultCode)), Toast.LENGTH_SHORT).show();

            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
        if (requestCode == ZBAR_SCANNER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "OK RESULT");
                // Scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT)
                // Type of the scan result is available by making a call to data.getStringExtra(ZBarConstants.SCAN_RESULT_TYPE)
                //Toast.makeText(this, "Scan Result = " + data.getStringExtra(rawResult.getContents().toString()), Toast.LENGTH_SHORT).show();
                //Toast.makeText(this, "Scan Result Type = " + data.getIntExtra(rawResult.getBarcodeFormat().getName(), 0), Toast.LENGTH_SHORT).show();

                String result = data.getStringExtra("BarcodeData");
                txtAwbno.setText(result);
                if(!result.toString().matches("^([a-zA-Z0-9]+$)") || result.toString().length() < 8)
                {
                    Global.AssetDialog("Invalid waybill number!!!", ScanDetailsActivity.this).create().show();
                }else{
                    processWaybillFromCamera();
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
    private void processWaybillFromCamera() {

        if(txtAwbno.getText().toString().equals("") || txtAwbno.getText().toString().equals(" ")
                || !txtAwbno.getText().toString().matches("^([a-zA-Z0-9]+$)") || txtAwbno.getText().toString().length() < 8)
        {
            Global.AssetDialog("Invalid AirWay bill number!!!", ScanDetailsActivity.this).create().show();
            txtAwbno.requestFocus();

        }else{
            if(db.checkAWBNOSCans(ScanDetailsActivity.this,txtAwbno.getText().toString(),Global.globalScanStatus.toString()))
            {
                Global.AssetDialog("AirWay bill number already Exist.!!", ScanDetailsActivity.this).create().show();
                txtAwbno.setText("");
                txtAwbno.requestFocus();

            }else{
                String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

                String sql;
                sql = "INSERT INTO SCANS" +
                        "(AWBNO,SCAN_STATUS,ORIGIN,DESTINATION,[DATE],WEIGHT," +
                        "BATCHNO,USERID,PIECES,SEALNO,VEHICLENO,TAG,ROUTE," +
                        "CONTENT_TYPE,TransferStatus" +
                        ")VALUES('" + txtAwbno.getText().toString()+ "',"+
                        "'" + Global.globalScanStatus + "',"+
                        "'" + Global.globalOrigin + "',"+
                        "'" + Global.globalDestination + "',"+
                        "'" + currentDateandTime.toString() + "',"+
                        "'" + Global.globalWeight + "',"+
                        "'" + Global.globalBatchNo + "',"+
                        "'" + Global.globalUserName + "',"+
                        "'" + Global.globalPieces + "',"+
                        "'" + Global.globalSealNo + "',"+
                        "'" + Global.globalVehicleNo + "',"+
                        "'" + Global.globalTagNo + "',"+
                        "'" + Global.globalRoute + "',"+
                        "'" + Global.globalContentType + "',"+
                        "'N')";

                if (db.dynamicInsert(ScanDetailsActivity.this,sql)) {
                    appendDetailsTop(tbDetails,txtAwbno.getText().toString().trim(),Global.globalWeight,Global.globalOrigin,Global.globalDestination);
                    scanCount+=1;
                    lblScanCount.setText(String.valueOf(scanCount));
                    txtAwbno.setText("");
                    txtAwbno.requestFocus();
                }else{
                    Global.AssetDialog("Error while trying to  save scan record. /n please try again.!!", ScanDetailsActivity.this).create().show();
                }
            }



        }// end of airwaybill check
    }

    private void appendDetailsTop(TableLayout table, String Text1,String Text2, String Text3, String Text4) {

        View sep2 = new View(this);
        LayoutParams p2 = new LayoutParams();
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

        table.addView(row, new LayoutParams());

        table.setColumnStretchable(0, true);

        View sep1 = new View(this);
        LayoutParams p1 = new LayoutParams();
        p1.height=1;
        sep1.setBackgroundColor(0xFF000000);
        sep1.setLayoutParams(p1);

        table.addView(sep1);

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
            txtAwbno.setText("");
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
