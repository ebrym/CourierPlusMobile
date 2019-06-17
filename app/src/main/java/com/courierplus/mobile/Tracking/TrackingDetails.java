package com.courierplus.mobile.Tracking;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TableLayout.LayoutParams;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.R;
import com.courierplus.mobile.WebRequest;


import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class TrackingDetails extends Activity {

	TextView txtHeader;
	TextView txtDetails;
	TextView txtBody;
	TextView txtItemHeader; 

	private Timer timer = new Timer();

	ProgressDialog pDialog; 
	String ReturnString = "";

	String _strItem = "";
	String[] _strItemLists;

	String _strItem1 = "";
	String[] _strItemLists1; 
	ViewGroup vwGroup;
	TableLayout table;
	TableLayout tbDetails;
	
	LinearLayout linearLayoutItemDetails;
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tracking_details);

        txtHeader = (TextView)findViewById(R.id.txtHeader); 
        txtDetails = (TextView)findViewById(R.id.txtDetails); 
        txtBody = (TextView)findViewById(R.id.txtBody); 
        txtItemHeader = (TextView)findViewById(R.id.txtItemHeader);
        txtItemHeader.setTypeface(null,Typeface.BOLD);
          
 
		txtHeader.setText(("TRACKING DETAILS"));
		txtDetails.setText("Details of " + Global.globalAWBno + " below.");
		

		txtBody.setText("Loading ...");
		txtBody.setVisibility(View.INVISIBLE);

		timer.schedule(new CountDown(), 100);
		table = (TableLayout) findViewById(R.id.tbTable);
		table.setVisibility(View.VISIBLE);

        linearLayoutItemDetails = (LinearLayout)findViewById(R.id.linearLayoutItemDetails); 
        linearLayoutItemDetails.setVisibility(View.VISIBLE);
		tbDetails = (TableLayout) findViewById(R.id.tbDetails);
    }   
    

	private class CountDown extends TimerTask {
		public void run() {
			DismissThread dThread = new DismissThread(); 
			runOnUiThread(dThread); 
		}
	}
	private class DismissThread implements Runnable {
		public void run() {
			pDialog = ProgressDialog.show(TrackingDetails.this, "","loading Tracking Details.\nPlease wait...", true);
			dismiss();
		}
	}
	public void dismiss() {
		new TrackShipmentTask().execute(Global.globalAWBno);
	}

	private class TrackShipmentTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// Creating service handler class instance
			WebRequest webreq = new WebRequest();

			// add parameter or query string
			String AWBNO = arg0[0];

			// Building Parameters
			HashMap<String, String> params = new HashMap<>();
			params.put("AWBNO", AWBNO);

			// Making a request to url and getting response
			ReturnString = webreq.makeWebServiceCall( Global.globalURLLocal + "TrackShipment_Fetch", WebRequest.POST, params);



			return ReturnString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			RunService();
		}

	}

	private void appendRow(TableLayout table, String LeftText, String RightText, boolean graybg ) {
		TableRow row = new TableRow(this);
 
		TextView col2 = new TextView(this);
		col2.setText(LeftText);
		col2.setPadding(3, 3, 3, 3); 
		col2.setTextColor(Color.BLACK);
		if (graybg == true) {
			col2.setBackgroundColor(Color.WHITE);
		}

		TextView col3 = new TextView(this);
		col3.setText(RightText);
		col3.setPadding(3, 3, 3, 3); 
		col3.setTextColor(Color.BLACK); 
		if (graybg == true) {
			col3.setBackgroundColor(Color.WHITE);
		}
 
		row.addView(col2, new TableRow.LayoutParams(0));
		row.addView(col3, new TableRow.LayoutParams());

		table.addView(row, new LayoutParams());

		table.setColumnStretchable(0, true);
		table.setColumnStretchable(1, true); 

		View sep1 = new View(this); 
		LayoutParams p1 = new LayoutParams();
		p1.height=2;
		sep1.setBackgroundColor(0xFF909090);
		sep1.setLayoutParams(p1);

		table.addView(sep1);
	}



	protected void RunService() {

		Thread thread = new Thread(new Runnable() {
			public void run() {

				try {
					if (ReturnString.indexOf("tempuri.org") != -1) {

						ReturnString = ReturnString.substring(74,ReturnString.length() - 9 );

						String CleanedResp ="";
						CleanedResp = ReturnString;
						CleanedResp = CleanedResp.replace("&gt;", ">");
						CleanedResp = CleanedResp.replace("&lt;", "<");
						SAXParserFactory factory = SAXParserFactory.newInstance();
						SAXParser parser = factory.newSAXParser();

						final XMLHandlerDetails xmlhandler = new XMLHandlerDetails();
						CleanedResp = "<main>" + CleanedResp + "</main>";

						ByteArrayInputStream inputStream = new ByteArrayInputStream(CleanedResp.getBytes());
						parser.parse(inputStream,xmlhandler);

						_strItem = xmlhandler.DecodedHeader;

						_strItemLists = Global.split(_strItem,"#");



						handler.sendEmptyMessage(0);

						if (xmlhandler.ResponseCode.equalsIgnoreCase("0")) {
							runOnUiThread(new Runnable() {
								public void run() {
									txtBody.setText("DELIVERY DETAILS");
									for (int i = 0; i < _strItemLists.length/2; i++) {
										if (i%2 == 0) {
											appendRow(table,_strItemLists[(i * 2)].replace("_"," ").trim(),_strItemLists[(i * 2) + 1].trim(),true);
										}
										else if (i%2 == 1) {
											appendRow(table,_strItemLists[(i * 2)].replace("_"," ").trim(),_strItemLists[(i * 2) + 1].trim(),false);
										}
									}
									table.setVisibility(View.VISIBLE);
									if (!xmlhandler.DecodedDetails.equalsIgnoreCase("")) {
										ShowDetails(xmlhandler.DecodedDetails);
									}

								}
							});

						} else {
							Log.d("Parser: ", "> " + "00");
							runOnUiThread(new Runnable() {
								public void run() {

									linearLayoutItemDetails.setVisibility(View.INVISIBLE);
									table.setVisibility(View.INVISIBLE);

									txtBody.setText("Connection problem. Please try again later.");
									Global.AssetDialog("Connection problem. Please try again later.",TrackingDetails.this).create().show();

									//	GlobalV.AssetDialog(xmlhandler.ResponseMessage,ScanDetails.this).create().show();
								}
							});
						}

					} else {
						handler.sendEmptyMessage(0);
						Log.d("Parser: ", "> " + "01");
						runOnUiThread(new Runnable() {
							public void run() {
								txtBody.setText("Connection problem. Please try again later.");
								table.setVisibility(View.INVISIBLE);
								Global.AssetDialog("Connection problem. Please try again later.",TrackingDetails.this).create().show();
							}
						});
					}
				} catch (final Exception ex) {
					Log.d("Error: ", "> " + ex.getMessage() + ex.getStackTrace().toString());
					handler.sendEmptyMessage(0);

					runOnUiThread(new Runnable() {
						public void run() {
							txtBody.setText("");
							table.setVisibility(View.INVISIBLE);
							Global.AssetDialog("Connection problem. Please try again later.",TrackingDetails.this).create().show();
						}//ex.getMessage()
					});
				}
			}
		});
		thread.start();

	}



	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			pDialog.dismiss();
		}
	}; 
	public void ShowDetails(String strDetails){

        linearLayoutItemDetails.setVisibility(View.VISIBLE);
      
		_strItem = strDetails;
		_strItemLists = Global.split(_strItem,"#");
		
		//Log.d(" XML_Details DETAILS",strDetails);
				for (int i = 0; i < _strItemLists.length/6; i++) {
					appendDetailsTop(tbDetails,_strItemLists[(i * 6) + 3].trim(),_strItemLists[(i * 6) + 5].trim(),_strItemLists[(i * 6) + 1].trim());
					//appendDetailsBottom(tbDetails,"LINE " + Integer.toString(i + 1)," Sub Total : " + _strItemLists1[1].trim(),true);
				} 
		  
	}

	//@SuppressWarnings("unused")
	private void appendDetailsTop(TableLayout table, String LeftText,String MiddleText, String RightText) {
		
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
		
		col1.setText(LeftText.trim());
		col1.setTextSize(9);
		col1.setPadding(3, 3, 3, 3); 
		col1.setTextColor(Color.BLACK); 
		
		col2.setText(MiddleText.trim());
		col2.setTextSize(9);
		col2.setPadding(3, 3, 3, 3); 
		col2.setTextColor(Color.BLACK); 
		
		col3.setText(RightText.trim());
		col3.setTextSize(9);
		col3.setPadding(3, 3, 3, 3); 
		col3.setTextColor(Color.BLACK); 
		
		TableRow.LayoutParams params1 = new TableRow.LayoutParams();
		params1.span = 2;
		params1.column = 0;
 
		row.addView(col1, new TableRow.LayoutParams(0));
		row.addView(col2, new TableRow.LayoutParams());
		row.addView(col3, new TableRow.LayoutParams());
		
		table.addView(row, new LayoutParams());

		table.setColumnStretchable(0, true); 
		
		View sep1 = new View(this); 
		LayoutParams p1 = new LayoutParams();
		p1.height=1;
		sep1.setBackgroundColor(0xFF000000);
		sep1.setLayoutParams(p1);

		table.addView(sep1); 
		
	}

	@SuppressWarnings("unused")
	private void appendDetailsBottom(TableLayout table, String LeftText, String RightText, boolean graybg ) {
		TableRow row = new TableRow(this);

		TextView col2 = new TextView(this);
		col2.setText(LeftText.trim());
		col2.setPadding(3, 3, 3, 3); 
		col2.setTextColor(Color.BLACK);
		if (graybg == true) {
			col2.setBackgroundColor(Color.LTGRAY);
		} 

		TextView col3 = new TextView(this);
		col3.setText(RightText.trim());
		col3.setPadding(3, 3, 3, 3); 
		col3.setTextColor(Color.BLACK); 
		if (graybg == true) {
			col3.setBackgroundColor(Color.LTGRAY);
		} 
 
		row.addView(col2, new TableRow.LayoutParams(0));
		row.addView(col3, new TableRow.LayoutParams());

		table.setColumnStretchable(0, true); 
		table.setColumnStretchable(0, true); 
		
		
		table.addView(row, new LayoutParams());
 
		View sep1 = new View(this); 
		LayoutParams p1 = new LayoutParams();
		p1.height=1;
		sep1.setBackgroundColor(0xFF000000);
		sep1.setLayoutParams(p1);

		table.addView(sep1);
	} 

}

/*
private class TrackShipmentTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// Creating service handler class instance
			WebRequest webreq = new WebRequest();

			// add parameter or query string
			String AWBNO = arg0[0];

			// Building Parameters
			HashMap<String, String> params = new HashMap<>();
			params.put("AWBNO", AWBNO);

			// Making a request to url and getting response
			 ReturnString = webreq.makeWebServiceCall(Global.globalURLLocal + "TrackShipment_Fetch", WebRequest.POST, params);
			Log.d("Response: ", "> " + ReturnString);
			Log.d("AWBNO: ", "> " + AWBNO.toString());


			return ReturnString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

		}

	}
 */