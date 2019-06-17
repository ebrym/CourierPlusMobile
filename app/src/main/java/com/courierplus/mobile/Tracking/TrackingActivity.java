package com.courierplus.mobile.Tracking;


import com.courierplus.mobile.Global;
import com.courierplus.mobile.R;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.View.OnClickListener;

public class TrackingActivity extends Activity {
	EditText Awb;
	ProgressDialog pDialog; 
	String ReturnString = "";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tracking);
	

	Awb = (EditText) findViewById(R.id.txtAWBNO);
    Button b = (Button) findViewById(R.id.btnTracking);
    
    b.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			 //Log.w("WORKING", Awb.getText().toString());
				if (Awb.getText().toString().equalsIgnoreCase("")) { 

					Global.AssetDialog("Enter Tracking Number." , TrackingActivity.this).create().show();
					Awb.requestFocus();
					
				} else {
					
					Global.globalAWBno = Awb.getText().toString();
					
					Intent i = new Intent(TrackingActivity.this, TrackingDetails.class);
					startActivity(i);
					
				}

			}

	});
}
}
