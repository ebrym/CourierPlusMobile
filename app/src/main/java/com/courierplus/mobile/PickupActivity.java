package com.courierplus.mobile;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.admin.DeviceAdminInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.device.ScanManager;
import android.device.scanner.configuration.Symbology;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.courierplus.mobile.PickUp.AccountDetails;
import com.courierplus.mobile.PickUp.AlertDetails;
import com.courierplus.mobile.PickUp.DestinationDetails;
import com.courierplus.mobile.PickUp.SendDetails;
import com.courierplus.mobile.PickUp.ShipmentDetails;
import com.courierplus.mobile.PickUp.pickupInterface;
import com.courierplus.mobile.PickUp.recipientDetails;

import java.text.SimpleDateFormat;
import java.util.Date;

import db.DataDB;




public class PickupActivity extends AppCompatActivity  {

    /**
//     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    private Button btnSavePickUp;
    private Button btnSavePickUpExit;
    private ProgressDialog mProgressView;
    DataDB db;
    /**
     * The {@link ViewPager} that will host the section contents.
     */

    private ScanManager mScanManager;
    private SoundPool soundpool = null;
    private int soundid;
    private String barcodeStr;
    private boolean isScaning = false;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pickup);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(mViewPager);



        // cast the all button
        btnSavePickUp = (Button) findViewById(R.id.btnSavePickUp);
        btnSavePickUpExit = (Button) findViewById(R.id.btnSavePickUpExit);

        // make button invisible
        btnSavePickUp.setVisibility(View.INVISIBLE);
        btnSavePickUpExit.setVisibility(View.INVISIBLE);


        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        // check which if tab 5 is selected and validation passed
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if (position == 5) {
                    btnSavePickUp.setVisibility(View.VISIBLE);
                    btnSavePickUpExit.setVisibility(View.VISIBLE);
                }else {
                    btnSavePickUp.setVisibility(View.INVISIBLE);
                    btnSavePickUpExit.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if (position == 5) {
                    btnSavePickUp.setVisibility(View.VISIBLE);
                    btnSavePickUpExit.setVisibility(View.VISIBLE);
                }else {
                    btnSavePickUp.setVisibility(View.INVISIBLE);
                    btnSavePickUpExit.setVisibility(View.INVISIBLE);
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab){
                int position = tab.getPosition();
                if (position == 5) {
                    btnSavePickUp.setVisibility(View.VISIBLE);
                    btnSavePickUpExit.setVisibility(View.VISIBLE);
                }else {
                    btnSavePickUp.setVisibility(View.INVISIBLE);
                    btnSavePickUpExit.setVisibility(View.INVISIBLE);
                }
            }
        });

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));



        btnSavePickUp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
               // initialise db class
            db=new DataDB();

                // check account number
            if (Global.globalPickupAccountNo.length() == 10)
            {

                if(Global.globalPickupSenderName.toString().equals("") || Global.globalPickupSenderName.toString().equals(" "))
                {
                    Global.AssetDialog("Sender name is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupSenderPhone.toString().equals("") || Global.globalPickupSenderPhone.toString().equals(" "))
                {
                    Global.AssetDialog("Sender phone is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupRecipientName.toString().equals("") || Global.globalPickupRecipientName.toString().equals(" "))
                {
                    Global.AssetDialog("Recipient name is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupRecipientAddress.toString().equals("") || Global.globalPickupRecipientAddress.toString().equals(""))
                {
                    Global.AssetDialog("Recipient address is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupRecipientPhone.toString().equals("") || Global.globalPickupRecipientPhone.toString().equals(" "))
                {
                    Global.AssetDialog("Recipient phone is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupAwbno.toString().equals("") || Global.globalPickupAwbno.toString().equals(" ")
                        || !Global.globalPickupAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPickupAwbno.toString().length() < 8)
                {
                    Global.AssetDialog("invalid Airway Bill Number!!!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupWeight.toString().equals("") || Global.globalPickupWeight.toString().equals("0") ||
                        Global.globalPickupWeight.toString().equals("0.00") || Float.parseFloat(Global.globalPickupWeight.toString()) < 0.5 )
                {
                    Global.AssetDialog("Weight is invalid!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupPieces.toString().equals("") || Integer.parseInt(Global.globalPickupPieces.toString()) < 1)
                {
                    Global.AssetDialog("Invalid piece(s)!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupExpressCenter.toString().equals("") || Global.globalPickupExpressCenter.toString().equals(" "))
                {
                    Global.AssetDialog("Express center is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupOrigin.toString().equals("") || Global.globalPickupOrigin.toString().equals(" "))
                {
                    Global.AssetDialog("Origin is required!", PickupActivity.this).create().show();
                    return;
                }
                if(Global.globalPickupDestination.toString().equals("") || Global.globalPickupDestination.toString().equals(" "))
                {
                    Global.AssetDialog("Destination is required!", PickupActivity.this).create().show();
                    return;
                }

              //mProgressView = ProgressDialog.show(PickupActivity.this, "", "Please wait...", true);
                String sql;
                String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                sql = "INSERT INTO PICKUP_BILLING (AcctNo, AwbNo,COMPANY_NAME,ADDRESS," +
                            "Box_Crating, Box_Crating_Value, " +
                            "Content_Description,  Delivery_Town, Delivery_Type, " +
                            "Department, Destination, Express_Centre, INSURANCE_VALUE," +
                            "Origin, Packaging, Pickup_Date, Pieces," +
                            "Recipient_Gsm, Senders_Gsm, UserID," +
                            "[Weight], senders_name, SENDERS_EMAIL," +
                            "RECIPIENTS_EMAIL, AmountPaid, DeliveryTownID," +
                            "DECLARED_VALUE,WaybillEmailAlert,ScansEmailAlert,PodEmailAlert,Flyer_No," +
                            "Prepaid,CUSTOM_FIELD2)" +
                    "values('"+ Global.globalPickupAccountNo+ "'," +
                        "'" + Global.globalPickupAwbno + "'," +
                        "'" + Global.globalPickupRecipientName + "'," +
                        "'" + Global.globalPickupRecipientAddress + "'," +
                        "'" + Global.globalPickupBoxCrating + "'," +
                        "'" + Global.globalPickupCratingValue + "'," +
                        "'" + Global.globalPickupDescription + "'," +
                        "'" + Global.globalPickupOnforwarding + "'," +
                        "'" + Global.globalPickupDeliveryType + "'," +
                        "'" + Global.globalPickupSenderDepartment + "'," +
                        "'" + Global.globalPickupDestination + "'," +
                        "'" + Global.globalPickupExpressCenter + "'," +
                        "'" + Global.globalPickupInsurance + "'," +
                        "'" + Global.globalPickupOrigin + "'," +
                        "'" + Global.globalPickupPackaging + "'," +
                        "'" + currentDateandTime.toString() + "'," +
                        "'" + Global.globalPickupPieces + "'," +
                        "'" + Global.globalPickupRecipientPhone + "',"+
                        "'" + Global.globalPickupSenderPhone + "'," +
                        "'" + Global.globalUserName + "'," +
                        "'" + Global.globalPickupWeight + "'," +
                        "'" + Global.globalPickupSenderName + "'," +
                        "'" + Global.globalPickupSenderEmail + "'," +
                        "'" + Global.globalPickupRecipientEmail + "'," +
                        "'" + Global.globalPickupAmount + "'," +
                        "'" + db.getDeliveryTownID(PickupActivity.this,Global.globalPickupOnforwarding,Global.globalPickupDestination) + "'," +
                        "'" + Global.globalPickupDeclaredValue + "',"+
                        "'" + Global.globalPickupAlert + "',"+
                        "'" + Global.globalScanAlert + "',"+
                        "'" + Global.globalPODAlert + "',"+
                        "'" + Global.globalPickupFlyerNo + "',"+
                        "'" + Global.globalPrepaidAlert + "',"+
                        "'N')";

                if(db.checkAWBNOPickUp(PickupActivity.this,Global.globalPickupAwbno.toString()))
                {
                    //handler.sendEmptyMessage(0);
                    Global.AssetDialog("AirWay bill number already Exist.!!", PickupActivity.this).create().show();
                    return;
                }else {
                    if (db.dynamicInsert(PickupActivity.this, sql)) {
                        //handler.sendEmptyMessage(0);
                        Global.AssetDialog("Record saved with Waybill number " + Global.globalPickupAwbno.toString(), PickupActivity.this).create().show();
                        Global.globalPickupAwbno = "";
                        //resetValues();
                        //finish();
                    } else {
                        //handler.sendEmptyMessage(0);
                        Global.AssetDialog("Error while trying to  save scan record.  please try again.!!", PickupActivity.this).create().show();
                    }
                }
            }else {
                //handler.sendEmptyMessage(0);
                Global.AssetDialog("Account number is invalid.!!", PickupActivity.this).create().show();
            }

            }
        });
        btnSavePickUpExit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // initialise db class
                db=new DataDB();

                // check account number
                if (Global.globalPickupAccountNo.length() == 10)
                {

                    if(Global.globalPickupSenderName.toString().equals("") || Global.globalPickupSenderName.toString().equals(" "))
                    {
                        Global.AssetDialog("Sender name is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupSenderPhone.toString().equals("") || Global.globalPickupSenderPhone.toString().equals(" "))
                    {
                        Global.AssetDialog("Sender phone is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupRecipientName.toString().equals("") || Global.globalPickupRecipientName.toString().equals(" "))
                    {
                        Global.AssetDialog("Recipient name is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupRecipientAddress.toString().equals("") || Global.globalPickupRecipientAddress.toString().equals(""))
                    {
                        Global.AssetDialog("Recipient address is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupRecipientPhone.toString().equals("") || Global.globalPickupRecipientPhone.toString().equals(" "))
                    {
                        Global.AssetDialog("Recipient phone is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupAwbno.toString().equals("") || Global.globalPickupAwbno.toString().equals(" ")
                            || !Global.globalPickupAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPickupAwbno.toString().length() < 8)
                    {
                        Global.AssetDialog("Invalid Airway Bill Number!!!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupWeight.toString().equals("") || Global.globalPickupWeight.toString().equals("0") ||
                            Global.globalPickupWeight.toString().equals("0.00") || Float.parseFloat(Global.globalPickupWeight.toString()) < 0.5 )
                    {
                        Global.AssetDialog("Weight is invalid!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupPieces.toString().equals("") || Integer.parseInt(Global.globalPickupPieces.toString()) < 1)
                    {
                        Global.AssetDialog("Invalid piece(s)!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupExpressCenter.toString().equals("") || Global.globalPickupExpressCenter.toString().equals(" "))
                    {
                        Global.AssetDialog("Express center is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupOrigin.toString().equals("") || Global.globalPickupOrigin.toString().equals(" "))
                    {
                        Global.AssetDialog("Origin is required!", PickupActivity.this).create().show();
                        return;
                    }
                    if(Global.globalPickupDestination.toString().equals("") || Global.globalPickupDestination.toString().equals(" "))
                    {
                        Global.AssetDialog("Destination is required!", PickupActivity.this).create().show();
                        return;
                    }

                    //mProgressView = ProgressDialog.show(PickupActivity.this, "", "Please wait...", true);
                    String sql;
                    String currentDateandTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                    sql = "INSERT INTO PICKUP_BILLING (AcctNo, AwbNo,COMPANY_NAME,ADDRESS," +
                            "Box_Crating, Box_Crating_Value, " +
                            "Content_Description,  Delivery_Town, Delivery_Type, " +
                            "Department, Destination, Express_Centre, INSURANCE_VALUE," +
                            "Origin, Packaging, Pickup_Date, Pieces," +
                            "Recipient_Gsm, Senders_Gsm, UserID," +
                            "[Weight], senders_name, SENDERS_EMAIL," +
                            "RECIPIENTS_EMAIL, AmountPaid, DeliveryTownID," +
                            "DECLARED_VALUE,WaybillEmailAlert,ScansEmailAlert,PodEmailAlert,Flyer_No," +
                            "Prepaid,CUSTOM_FIELD2)" +
                            "values('"+ Global.globalPickupAccountNo+ "'," +
                            "'" + Global.globalPickupAwbno + "'," +
                            "'" + Global.globalPickupRecipientName + "'," +
                            "'" + Global.globalPickupRecipientAddress + "'," +
                            "'" + Global.globalPickupBoxCrating + "'," +
                            "'" + Global.globalPickupCratingValue + "'," +
                            "'" + Global.globalPickupDescription + "'," +
                            "'" + Global.globalPickupOnforwarding + "'," +
                            "'" + Global.globalPickupDeliveryType + "'," +
                            "'" + Global.globalPickupSenderDepartment + "'," +
                            "'" + Global.globalPickupDestination + "'," +
                            "'" + Global.globalPickupExpressCenter + "'," +
                            "'" + Global.globalPickupInsurance + "'," +
                            "'" + Global.globalPickupOrigin + "'," +
                            "'" + Global.globalPickupPackaging + "'," +
                            "'" + currentDateandTime.toString() + "'," +
                            "'" + Global.globalPickupPieces + "'," +
                            "'" + Global.globalPickupRecipientPhone + "',"+
                            "'" + Global.globalPickupSenderPhone + "'," +
                            "'" + Global.globalUserName + "'," +
                            "'" + Global.globalPickupWeight + "'," +
                            "'" + Global.globalPickupSenderName + "'," +
                            "'" + Global.globalPickupSenderEmail + "'," +
                            "'" + Global.globalPickupRecipientEmail + "'," +
                            "'" + Global.globalPickupAmount + "'," +
                            "'" + db.getDeliveryTownID(PickupActivity.this,Global.globalPickupOnforwarding,Global.globalPickupDestination) + "'," +
                            "'" + Global.globalPickupDeclaredValue + "',"+
                            "'" + Global.globalPickupAlert + "',"+
                            "'" + Global.globalScanAlert + "',"+
                            "'" + Global.globalPODAlert + "',"+
                            "'" + Global.globalPickupFlyerNo + "',"+
                            "'" + Global.globalPrepaidAlert + "',"+
                            "'N')";

                    if(db.checkAWBNOPickUp(PickupActivity.this,Global.globalPickupAwbno.toString()))
                    {
                        //handler.sendEmptyMessage(0);
                        Global.AssetDialog("AirWay bill number already Exist.!!", PickupActivity.this).create().show();
                        return;
                    }else {
                        if (db.dynamicInsert(PickupActivity.this, sql)) {
                            //handler.sendEmptyMessage(0);
                            Global.AssetDialog("Record saved with Waybill number " + Global.globalPickupAwbno.toString(), PickupActivity.this).create().show();
                            Global.globalPickupAwbno = "";
                            resetValues();
                            finish();
                        } else {
                           // handler.sendEmptyMessage(0);
                            Global.AssetDialog("Error while trying to  save scan record.  please try again.!!", PickupActivity.this).create().show();
                        }
                    }
                }else {
                    //handler.sendEmptyMessage(0);
                    Global.AssetDialog("Account number is invalid.!!", PickupActivity.this).create().show();
                }



//                        handler.sendEmptyMessage(0);


            }
        });

    }
    public void validateInput(){
    if(Global.globalPickupSenderName.toString().equals(""))
    {
        Global.AssetDialog("Sender name is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupSenderPhone.toString().equals(""))
    {
        Global.AssetDialog("Sender phone is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupRecipientName.toString().equals(""))
    {
        Global.AssetDialog("Recipient name is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupRecipientAddress.toString().equals(""))
    {
        Global.AssetDialog("Recipient address is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupRecipientPhone.toString().equals(""))
    {
        Global.AssetDialog("Recipient phone is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupAwbno.toString().equals("")
            || !Global.globalPickupAwbno.toString().matches("^([a-zA-Z0-9]+$)") || Global.globalPickupAwbno.toString().length() < 8)
    {
        Global.AssetDialog("invalid Airway Bill Number!!!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupWeight.toString().equals("") || Global.globalPickupWeight.equals("0") ||
            Global.globalPickupWeight.equals("0.00") || Float.parseFloat(Global.globalPickupWeight.toString()) < 0.5 )
    {
        Global.AssetDialog("Weight is invalid!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupPieces.toString().equals("") || Integer.parseInt(Global.globalPickupPieces.toString()) < 1)
    {
        Global.AssetDialog("Invalid piece(s)!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupExpressCenter.toString().equals(""))
    {
        Global.AssetDialog("Express center is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupOrigin.toString().equals("") || Global.globalPickupOrigin.toString().equals(" "))
    {
        Global.AssetDialog("Origin is required!", PickupActivity.this).create().show();
        return;
    }
    if(Global.globalPickupDestination.toString().equals("") || Global.globalPickupDestination.toString().equals(" "))
    {
        Global.AssetDialog("Destination is required!", PickupActivity.this).create().show();
        return;
    }
}
    /*@SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        // @Override
        public void handleMessage(Message msg) {
            mProgressView.dismiss();
        }
    };*/


    public void resetValues(){
        Global.globalPickupAccountNo = "";
        Global.globalPickupAwbno = "";
        Global.globalPickupOrigin = "";
        Global.globalPickupDestination = "";
        Global.globalPickupSenderName = "";
        Global.globalPickupSenderDepartment = "";
        Global.globalPickupSenderAddress = "";
        Global.globalPickupSenderPhone = "";
        Global.globalPickupSenderEmail = "";
        Global.globalPickupRecipientName = "";
        Global.globalPickupRecipientAddress = "";
        Global.globalPickupRecipientPhone = "";
        Global.globalPickupRecipientEmail = "";

        Global.globalPickupWeight = "";
        Global.globalPickupPieces = "";
        Global.globalPickupDescription = "";
        Global.globalPickupDeclaredValue = "";
        Global.globalPickupInsurance = "";
        Global.globalPickupCratingValue = "";

        Global.globalPickupPackaging = "";
        Global.globalPickupBoxCrating = "";
        Global.globalPickupExpressCenter = "";

        Global.globalPickupDeliveryType = "";
        Global.globalPickupOnforwarding = "";
        Global.globalPickupDeliverTownID = "";
        Global.globalPickupAmount = "";
        Global.globalPickupFlyerNo = "";

        Global.globalPickupAlert = "";
        Global.globalPODAlert = "";
        Global.globalScanAlert = "";
        Global.globalPrepaidAlert = "N";
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    //return PlaceholderFragment.newInstance(0);
                    AccountDetails AccountDetails = new AccountDetails();
                    return AccountDetails;
                case 1:
                    SendDetails sndDetails = new SendDetails();
                    return sndDetails;
                case 2:
                    recipientDetails recipientDetails = new recipientDetails();
                    return recipientDetails;
                case 3:
                    ShipmentDetails ShipmentDetails = new ShipmentDetails();
                    return ShipmentDetails;
                case 4:
                    DestinationDetails DestinationDetails = new DestinationDetails();
                    return DestinationDetails;
                case 5:
                    AlertDetails AlertDetails = new AlertDetails();
                    return AlertDetails;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 6 total pages.
            return 6;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ACCOUNT";
                case 1:
                    return "SENDER";
                case 2:
                    return "RECIPIENT";
                case 3:
                    return "SHIPMENT";
                case 4:
                    return "DESTINATION";
                case 5:
                    return "ALERT";
            }
            return null;
        }
    }


}
