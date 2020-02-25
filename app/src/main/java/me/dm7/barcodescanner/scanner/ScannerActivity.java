package me.dm7.barcodescanner.scanner;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.LoginActivity;
import com.google.android.gms.common.api.CommonStatusCodes;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class ScannerActivity extends Activity implements
        ZBarScannerView.ResultHandler{
    private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private static final String CAMERA_ID = "CAMERA_ID";
    private ZBarScannerView mScannerView;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private int mCameraId = -1;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);




        mScannerView = new ZBarScannerView(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            String[] per = {Manifest.permission.CAMERA};
            requestPermissions(per, 1);

            if (ActivityCompat.checkSelfPermission(ScannerActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                if(state != null) {
                    mFlash = state.getBoolean(FLASH_STATE, false);
                    mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
                    mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
                    mCameraId = state.getInt(CAMERA_ID, -1);
                } else {
                    mFlash = false;
                    mAutoFocus = true;
                    mSelectedIndices = null;
                    mCameraId = -1;
                }
                setupFormats();
                setContentView(mScannerView);
            }
        }else{
           // mScannerView = new ZBarScannerView(this);
            if(state != null) {
                mFlash = state.getBoolean(FLASH_STATE, false);
                mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
                mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
                mCameraId = state.getInt(CAMERA_ID, -1);
            } else {
                mFlash = false;
                mAutoFocus = true;
                mSelectedIndices = null;
                mCameraId = -1;
            }
            setupFormats();
            setContentView(mScannerView);
        }


        //setContentView(R.layout.activity_full_scanner);
        ////setupToolbar();
       // ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
//        mScannerView = new ZBarScannerView(this);
//        setupFormats();
//        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();

        mScannerView = new ZBarScannerView(this);

        mFlash = false;
        mAutoFocus = true;
        mSelectedIndices = null;
        mCameraId = -1;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionCheck = ActivityCompat.checkSelfPermission(ScannerActivity.this,
                    Manifest.permission.CAMERA);

            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                Log.e("permission", "granted");
                mScannerView.setResultHandler(ScannerActivity.this);
                mScannerView.startCamera(mCameraId);
                mScannerView.setFlash(mFlash);
                mScannerView.setAutoFocus(mAutoFocus);

            } else {
                ActivityCompat.requestPermissions(ScannerActivity.this,
                        new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                mScannerView.setResultHandler(ScannerActivity.this);
                mScannerView.startCamera(mCameraId);
                mScannerView.setFlash(mFlash);
                mScannerView.setAutoFocus(mAutoFocus);
            }
        } else {
            Log.e("MainActivity on Resume ", "Lower Than MarshMallow");

            mScannerView.setResultHandler(ScannerActivity.this);
            mScannerView.startCamera(mCameraId);
            mScannerView.setFlash(mFlash);
            mScannerView.setAutoFocus(mAutoFocus);
        }
        setupFormats();
        setContentView(mScannerView);
//
//        mScannerView.setResultHandler(this);
//        mScannerView.startCamera(mCameraId);
//        mScannerView.setFlash(mFlash);
//        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
        outState.putInt(CAMERA_ID, mCameraId);
    }


    @Override
    public void handleResult(Result rawResult) {
       /* try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {}
        */
if (rawResult == null)
    mScannerView.stopCamera();
        showMessageDialog("Waybill Number = " + rawResult.getContents());

        String data = rawResult.getContents();
        Intent mIntent = new Intent();
        mIntent.putExtra("BarcodeData", data);
        setResult(Activity.RESULT_OK, mIntent);
        finish();
//        try {
//            showMessageDialog("Waybill Number = " + rawResult.getContents());
//
//            String data = rawResult.getContents();
//            Intent mIntent = new Intent();
//            mIntent.putExtra("BarcodeData", data);
//            setResult(Activity.RESULT_OK, mIntent);
//            mScannerView.stopCamera();
//
//            //finish();
//        } catch (Exception ex) {
//            //Log.e("scannerView.stopCamera:", ex.toString());
//        }

    }

    public void showMessageDialog(String message) {
        Toast.makeText(this,  message , Toast.LENGTH_SHORT).show();
    }

    public void setupFormats() {
        List<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

        formats.add(BarcodeFormat.ALL_FORMATS.get(0));
        formats.add(BarcodeFormat.ALL_FORMATS.get(1));
        if(mScannerView != null) {
            mScannerView.setFormats(formats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }
}
