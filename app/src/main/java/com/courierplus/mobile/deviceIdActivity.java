package com.courierplus.mobile;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class deviceIdActivity extends AppCompatActivity  {

		public Activity c;
		public Dialog d;
		public Button btnSubmit;
		public EditText txtDeviceId;

//		public deviceIdActivity(Activity a) {
//			super(a);
//			// TODO Auto-generated constructor stub
//			c = a;
//		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.activity_deviceid);


			txtDeviceId = (EditText)findViewById(R.id.txtDeviceId);

			btnSubmit = (Button)findViewById(R.id.btnSubmit);
			btnSubmit.setOnClickListener(new View.OnClickListener() {

				public void onClick(View v) {
					// TODO Auto-generated method stub
					String deviceId = txtDeviceId.getText().toString();
					if(!deviceId.isEmpty()){
						Global.globalDeviceIMEI  = deviceId;
						Intent i = new Intent(deviceIdActivity.this, LoginActivity.class);
						startActivity(i);
						finish();
					}
					else
						Global.AssetDialog("Please Enter IMEI of SIM 1!!!", deviceIdActivity.this).create().show();


				}
			});
		}


	}