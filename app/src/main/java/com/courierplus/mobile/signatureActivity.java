package com.courierplus.mobile;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.courierplus.mobile.SignatureView;
import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

import java.util.UUID;
import db.DataDB;

public class signatureActivity extends Activity  {
    //custom drawing view
    private SignatureView signatureView;
    //buttons
    private ImageButton   eraseBtn,  saveBtn;

    // radio buttons
    private RadioGroup ratingRadio;

    //sizes
    private float smallBrush, mediumBrush, largeBrush;
    int[] flag;
    int position;

    DataDB db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        db=new DataDB();

      //  LinearLayout SigLayout = (LinearLayout) findViewById(R.id.SignatureLayout);
        //final SignatureView mSig = new SignatureView(this, null);
        //SigLayout.addView(mSig, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//
//        FILL_PARENT
        //get drawing view
        signatureView = (SignatureView) findViewById(R.id.drawing);
       // SigLayout.addView(signatureView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //set initial size
        signatureView.setBrushSize(2);
        //erase button
        eraseBtn = (ImageButton) findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                Log.w("ERASER : ", "ERASE");
                AlertDialog.Builder newDialog = new AlertDialog.Builder(signatureActivity.this);
                newDialog.setTitle("New signature");
                newDialog.setMessage("Start new signature (you will lose the current signature)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        signatureView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        });


         ratingRadio =  (RadioGroup) findViewById(R.id.ratingRadioGroup);

        ratingRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected

                if(checkedId == R.id.radio_good) {
                    Global.globalPodRemarks = "Good";
                    Toast.makeText(getApplicationContext(), "Good",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.radio_fair) {
                    Global.globalPodRemarks = "Fair";
                    Toast.makeText(getApplicationContext(), "Fair",
                            Toast.LENGTH_SHORT).show();
                } else if(checkedId == R.id.radio_bad){
                    Global.globalPodRemarks = "Bad";
                    Toast.makeText(getApplicationContext(), "Bad",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });


        //save button
        saveBtn = (ImageButton) findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
//save drawing
                AlertDialog.Builder saveDialog = new AlertDialog.Builder(signatureActivity.this);
                saveDialog.setTitle("Save signature");
                saveDialog.setMessage("Save signature?");
                saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        //save drawing

                        signatureView.setDrawingCacheEnabled(true);
                        Bitmap mBitmap;
                        mBitmap =  Bitmap.createBitmap (signatureView.getWidth(), signatureView.getHeight(), Bitmap.Config.RGB_565);;
//                        byte[] signature = mSig.getBytes();
//                        Bitmap signature1 = mSig.getBitmap();

                       if(db.insertSignature(signatureActivity.this,mBitmap))
                        {
                            Toast savedToast = Toast.makeText(getApplicationContext(),
                                    "Signature saved!", Toast.LENGTH_LONG);
                            savedToast.show();

                            finish();
                        }else{
                            Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                    "Oops! Signature could not be saved.", Toast.LENGTH_SHORT);
                            unsavedToast.show();
                        }





                        signatureView.destroyDrawingCache();
                    }
                });
                saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int which){
                        dialog.cancel();
                    }
                });
                saveDialog.show();
            }
        });
    }


}
