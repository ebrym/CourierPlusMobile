package com.courierplus.mobile;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import db.DataDB;

/**
 * Created by iabdullahi on 6/10/2016.
 */
public class SyncData extends Service {

    DataDB db;
    String sql;
    String retMsg,Uploadtype;
    String jsonStr;
    public SharedPreferences prefs;
    private static Timer timer = new Timer();
    public static final String USER_DETAILS = "UserSettingsDetails";
    private Context ctx;
    uploadData  upData =   new uploadData();
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();

        db=new DataDB();
       // uploadData.;

//            String prefsDeviceID = prefs.getString("DeviceID", "");
//            String prefsUserID = prefs.getString("UserID", "");

//            Log.d("SYNC: DEVICEID ", prefs.getString("DeviceID", "NA") );
//           Log.d("SYNC: USERID ", prefs.getString("UserID", "NA") );

        timer.scheduleAtFixedRate(new mainTask(), 60000, 120000);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {


           /* if(Global.globalUserName.toString().equals("") || Global.globalDeviceIMEI.toString().equals("")) {

                Intent i = new Intent();
                i.setClass(SyncData.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                //stopService(new Intent (SyncData.this, SyncData.class));
                //onDestroy();
            }*/
            String podCount = db.getAvailablePODCount(SyncData.this).toString();
            String PickupCount = db.getAvailablePickUpCount(SyncData.this).toString();
            String scanCount = db.getAvailableScanCount(SyncData.this).toString();
            String sigCount = db.getAvailableSignatureCount(SyncData.this).toString();

           // Log.d("SYNC: ", "BEGIN " );
            uploadPOD();
            uploadPICKUP();
            uploadSCANS();
            uploadSignature();
            //Log.d("SYNC: ", "END " );

            /*if (Global.globalSyncPOD == false && Global.globalSyncPickUp == false &&
                    Global.globalSyncScans == false && Global.globalSyncSignature == false &&
                    Global.globalSyncCHMPOD == false) {
                // check avalable records to upload
                if (!podCount.toString().equals("0")){
                    if(upData.getStatus() == AsyncTask.Status.FINISHED){
                        // My AsyncTask is done and onPostExecute was called
                        Log.d("SYNC: ", "POD " );
                        upData.cancel(true);
                        uploadPOD();
                    }

                }
            }
            if (Global.globalSyncPOD == false && Global.globalSyncPickUp == false &&
                    Global.globalSyncScans == false && Global.globalSyncSignature == false &&
                    Global.globalSyncCHMPOD == false ) {
                if (!CHMPODCount.toString().equals("0")){
                    if(upData.getStatus() == AsyncTask.Status.FINISHED) {
                        Log.d("SYNC: ", "CHM " );
                        upData.cancel(true);
                        uploadCHMPOD();
                    }
                }
            }
            if (Global.globalSyncPOD == false && Global.globalSyncPickUp == false &&
                    Global.globalSyncScans == false && Global.globalSyncSignature == false &&
                    Global.globalSyncCHMPOD == false) {
                if (!PickupCount.toString().equals("0")){
                    if(upData.getStatus() == AsyncTask.Status.FINISHED) {
                        Log.d("SYNC: ", "PICKUP " );
                        upData.cancel(true);
                        uploadPICKUP();
                    }
                }

            }
            if (Global.globalSyncPOD == false && Global.globalSyncPickUp == false &&
                    Global.globalSyncScans == false && Global.globalSyncSignature == false &&
                    Global.globalSyncCHMPOD == false) {
                if (!scanCount.toString().equals("0")){
                    if(upData.getStatus() == AsyncTask.Status.FINISHED) {
                        Log.d("SYNC: ", "SCANS " );
                        upData.cancel(true);
                        uploadSCANS();
                    }
                }
            }
            if (Global.globalSyncPOD == false && Global.globalSyncPickUp == false &&
                    Global.globalSyncScans == false && Global.globalSyncSignature == false &&
                    Global.globalSyncCHMPOD == false) {
                if (!sigCount.toString().equals("0")){
                    if(upData.getStatus() == AsyncTask.Status.FINISHED) {
                        Log.d("SYNC: ", "SIGNATURE " );
                        upData.cancel(true);
                        uploadSignature();
                    }
                }
            }*/
             podCount = "";
             PickupCount = "";
             scanCount = "";
             sigCount = "";
        }
    }

    public void uploadPOD()
    {
        // SECTION POD SYNC
        Uploadtype = "";
        Uploadtype = "POD";

        Cursor cursor = db.getPODNotUploaded(SyncData.this);
        if (cursor.getCount() > 0)
        {
            // POD RECORD EXIST
            if (cursor.moveToFirst()) {
                do {
                    // Get each items
                    String AwbNo = cursor.getString(cursor.getColumnIndex("waybillnumber"));
                    String pod = cursor.getString(cursor.getColumnIndex("pod"));
                    String podby = cursor.getString(cursor.getColumnIndex("podby"));
                    String dexcodeid = cursor.getString(cursor.getColumnIndex("dexcodeid"));
                    String first_date = cursor.getString(cursor.getColumnIndex("first_date"));
                    String poddate = cursor.getString(cursor.getColumnIndex("poddate"));
                    String OriginStation = cursor.getString(cursor.getColumnIndex("OriginStation"));
                    String pod_post_date = cursor.getString(cursor.getColumnIndex("pod_post_date"));
                    String DeliveryRemarks = cursor.getString(cursor.getColumnIndex("DeliveryRemarks"));


                    //if (upData==null){
                        upData = new uploadData();
                        upData.execute(Uploadtype,Global.globalDeviceIMEI,Global.globalUserName,
                                AwbNo,pod,podby,dexcodeid,first_date,
                                poddate,OriginStation,pod_post_date,DeliveryRemarks);
                    //}

                    Global.globalSyncPOD = true;


                    AwbNo = "";
                    pod = "";
                    podby = "";
                    dexcodeid = "";
                    first_date = "";
                    poddate = "";
                    OriginStation = "";
                    pod_post_date = "";
                    DeliveryRemarks = "";
                } while (cursor.moveToNext());

                cursor.close();
//

                Global.globalSyncPOD = false;
            }

        } else {
            cursor.close();
            Global.globalSyncPOD = false;
        }

    }

    //upload scans
    public void uploadSCANS()
    {
        // SECTION SCANS SYNC
        Uploadtype = "";
        Uploadtype = "SCANS";


            Cursor dcursor = db.getSCANSNotUploaded(SyncData.this);
            if (dcursor.getCount() > 0)
            {
                // SCAN RECORD EXIST
                if (dcursor.moveToFirst()) {
                    do {
                        // Get each items

                        String AwbNo = dcursor.getString(dcursor.getColumnIndex("AWBNO"));
                        String Scan_Status = dcursor.getString(dcursor.getColumnIndex("SCAN_STATUS"));
                        String Origin = dcursor.getString(dcursor.getColumnIndex("ORIGIN"));
                        String Destination = dcursor.getString(dcursor.getColumnIndex("DESTINATION"));
                        String Date = dcursor.getString(dcursor.getColumnIndex("DATE"));
                        String Weight = dcursor.getString(dcursor.getColumnIndex("WEIGHT"));
                        String BatchNo = dcursor.getString(dcursor.getColumnIndex("BATCHNO"));
                        String Pieces = dcursor.getString(dcursor.getColumnIndex("PIECES"));
                        String SealNo = dcursor.getString(dcursor.getColumnIndex("SEALNO"));
                        String VehicleNo = dcursor.getString(dcursor.getColumnIndex("VEHICLENO"));
                        String Tag = dcursor.getString(dcursor.getColumnIndex("TAG"));
                        String Express_Centre_Code = dcursor.getString(dcursor.getColumnIndex("EXPRESS_CENTRE_CODE"));
                        String Content_Type = dcursor.getString(dcursor.getColumnIndex("CONTENT_TYPE"));
                        String Route = dcursor.getString(dcursor.getColumnIndex("ROUTE"));

                        upData = new uploadData();
                        upData.execute(Uploadtype, Global.globalDeviceIMEI, Global.globalUserName,
                                AwbNo, Scan_Status, Origin, Destination, Date,
                                Weight, BatchNo, Pieces, SealNo,
                                VehicleNo, Tag, Express_Centre_Code, Content_Type, Route);
                        Global.globalSyncScans=true;

                        AwbNo = "";
                        Scan_Status = "";
                        Origin = "";
                        Destination = "";
                        Date = "";
                        Weight = "";
                        BatchNo = "";
                        Pieces = "";
                        SealNo = "";
                        VehicleNo = "";
                        Tag = "";
                        Express_Centre_Code = "";
                        Content_Type = "";
                        Route = "";
                    } while (dcursor.moveToNext());
                }
                dcursor.close();
//
                Global.globalSyncScans=false;
            } else {
                dcursor.close();
                Global.globalSyncScans=false;
            }



    }
    public void uploadPICKUP()
    {
        // SECTION PICKUP SYNC
        Uploadtype = "";
        Uploadtype = "PICKUP";

            Cursor dcursor = db.getPickUPNotUploaded(SyncData.this);
            if (dcursor.getCount() > 0)
            {
                // SCAN RECORD EXIST
                if (dcursor.moveToFirst()) {
                    do {
                        // Get each items
                        String AcctNo = dcursor.getString(dcursor.getColumnIndex("ACCTNO"));
                        String AwbNo = dcursor.getString(dcursor.getColumnIndex("AWBNO"));
                        String COMPANY_NAME = dcursor.getString(dcursor.getColumnIndex("COMPANY_NAME"));
                        String ADDRESS = dcursor.getString(dcursor.getColumnIndex("ADDRESS"));
                        String Box_Crating = dcursor.getString(dcursor.getColumnIndex("BOX_CRATING"));
                        String Box_Crating_Value = dcursor.getString(dcursor.getColumnIndex("BOX_CRATING_VALUE"));
                        String Content_Description = dcursor.getString(dcursor.getColumnIndex("CONTENT_DESCRIPTION"));
                        String Delivery_Town = dcursor.getString(dcursor.getColumnIndex("DELIVERY_TOWN"));
                        String Delivery_Type = dcursor.getString(dcursor.getColumnIndex("DELIVERY_TYPE"));
                        String Department = dcursor.getString(dcursor.getColumnIndex("DEPARTMENT"));
                        String Destination = dcursor.getString(dcursor.getColumnIndex("DESTINATION"));
                        String Express_Centre = dcursor.getString(dcursor.getColumnIndex("EXPRESS_CENTRE"));
                        String INSURANCE_VALUE = dcursor.getString(dcursor.getColumnIndex("INSURANCE_VALUE"));
                        String Origin = dcursor.getString(dcursor.getColumnIndex("ORIGIN"));
                        String Packaging = dcursor.getString(dcursor.getColumnIndex("PACKAGING"));
                        String Pickup_Date = dcursor.getString(dcursor.getColumnIndex("PICKUP_DATE"));
                        String Pieces = dcursor.getString(dcursor.getColumnIndex("PIECES"));
                        String Recipient_Gsm = dcursor.getString(dcursor.getColumnIndex("RECIPIENT_GSM"));
                        String Senders_Gsm = dcursor.getString(dcursor.getColumnIndex("SENDERS_GSM"));
                        String UserID = dcursor.getString(dcursor.getColumnIndex("USERID"));
                        String Weight = dcursor.getString(dcursor.getColumnIndex("WEIGHT"));
                        String senders_name = dcursor.getString(dcursor.getColumnIndex("SENDERS_NAME"));
                        String SENDERS_EMAIL = dcursor.getString(dcursor.getColumnIndex("SENDERS_EMAIL"));
                        String RECIPIENTS_EMAIL = dcursor.getString(dcursor.getColumnIndex("RECIPIENTS_EMAIL"));
                        String AmountPaid = dcursor.getString(dcursor.getColumnIndex("AmountPaid"));
                        String DeliveryTownID = dcursor.getString(dcursor.getColumnIndex("DeliveryTownID"));
                        String DECLARED_VALUE = dcursor.getString(dcursor.getColumnIndex("DECLARED_VALUE"));
                        String WaybillEmailAlert = dcursor.getString(dcursor.getColumnIndex("WaybillEmailAlert"));
                        String ScansEmailAlert = dcursor.getString(dcursor.getColumnIndex("ScansEmailAlert"));
                        String PodEmailAlert = dcursor.getString(dcursor.getColumnIndex("PODEmailAlert"));
                        String Flyer_No = dcursor.getString(dcursor.getColumnIndex("FLYER_NO"));
                        String Prepaid = dcursor.getString(dcursor.getColumnIndex("Prepaid"));

                        upData = new uploadData();
                        upData.execute(Uploadtype, Global.globalDeviceIMEI, AcctNo,
                                AwbNo, COMPANY_NAME, ADDRESS, Box_Crating, Box_Crating_Value, Content_Description, Delivery_Town,
                                Delivery_Type, Department, Destination, Express_Centre, INSURANCE_VALUE,
                                Origin, Packaging, Pickup_Date, Pieces, Recipient_Gsm, Senders_Gsm, UserID,
                                Weight, senders_name, SENDERS_EMAIL, RECIPIENTS_EMAIL, AmountPaid,
                                DeliveryTownID, DECLARED_VALUE, WaybillEmailAlert, ScansEmailAlert,
                                PodEmailAlert, Flyer_No,Prepaid);


                        AcctNo = "";
                        AwbNo = "";
                        COMPANY_NAME = "";
                        ADDRESS = "";
                        Box_Crating = "";
                        Box_Crating_Value = "";
                        Content_Description = "";
                        Delivery_Town = "";
                        Delivery_Type = "";
                        Department = "";
                        Destination = "";
                        Express_Centre = "";
                        INSURANCE_VALUE = "";
                        Origin = "";
                        Packaging = "";
                        Pickup_Date = "";
                        Pieces = "";
                        Recipient_Gsm = "";
                        Senders_Gsm = "";
                        UserID = "";
                        Weight = "";
                        senders_name = "";
                        SENDERS_EMAIL = "";
                        RECIPIENTS_EMAIL = "";
                        AmountPaid = "";
                        DeliveryTownID = "";
                        DECLARED_VALUE = "";
                        WaybillEmailAlert = "";
                        ScansEmailAlert = "";
                        PodEmailAlert = "";
                        Flyer_No = "";
                        Prepaid = "";
                        Global.globalSyncPickUp = true;

                    } while (dcursor.moveToNext());
                }
                dcursor.close();
//
                Global.globalSyncPickUp = false;
            } else {
                dcursor.close();
                Global.globalSyncPickUp = false;
            }

    }
    public void uploadSignature()
    {
        // SECTION POD SYNC
        Uploadtype = "";
        Uploadtype = "SIGNATURE";

            Cursor cursor = db.getSignatureNotUploaded(SyncData.this);
            if (cursor.getCount() > 0)
            {
                // POD RECORD EXIST
                if (cursor.moveToFirst()) {
                    do {
                        // Get each items
                        Bitmap bitmap = null;
                        String AwbNo = cursor.getString(cursor.getColumnIndex("AWBNO"));
                        byte[] bSignature = cursor.getBlob(cursor.getColumnIndex("Signature"));
                        String DateCreated = cursor.getString(cursor.getColumnIndex("DateCreated"));


                        String Signature = Base64.encodeToString(bSignature, Base64.DEFAULT);

                        upData = new uploadData();
                        upData.execute(Uploadtype, Global.globalDeviceIMEI, Global.globalUserName, AwbNo, Signature, DateCreated);
                        Global.globalSyncSignature = true;


                        AwbNo = "";
                        Signature = "";
                        DateCreated = "";

                    } while (cursor.moveToNext());
                }
                cursor.close();
//
                Global.globalSyncSignature = false;
            } else {
                cursor.close();
                Global.globalSyncSignature = false;
            }

    }
    //SECTION TO SYNC DATA TO SERVER
    private class uploadData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... arg0) {
            // Creating service handler class instance
            WebRequest webreq = new WebRequest();
            //prefs = getSharedPreferences(USER_DETAILS,MODE_PRIVATE);
//            String prefsDeviceID = prefs.getString("DeviceID", null);
//            String prefsUserID = prefs.getString("UserID", null);
//  String DeviceID = prefs.getString("DeviceID", "NA");//arg0[1];
//                String UserID = prefs.getString("UserID", "NA");//arg0[2];
//            Log.d("SYNC: DEVICEID ", prefsDeviceID );
//            Log.d("SYNC: USERID ", prefsUserID );
            // add parameter or query string
            // if task is canceled return null
            if (isCancelled()) return null;
            // create new instance of the DB class

            if(arg0[0] == "POD"){
                String DeviceID = db.getDeviceID(SyncData.this);//arg0[1];
                String UserID = db.getUserID(SyncData.this);//arg0[2];
                String AwbNo = arg0[3];
                String pod = arg0[4];
                String podby = arg0[5];
                String dexcodeid = arg0[6];
                String first_date = arg0[7];
                String poddate = arg0[8];
                String OriginStation = arg0[9];
                String pod_post_date = arg0[10];
                String CustomerFeedBack = arg0[11];



                // Building Parameters


                HashMap<String, String> params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("AwbNo", AwbNo);
                params.put("pod", pod);
                params.put("podby", podby);
                params.put("dexcodeid", dexcodeid);
                params.put("first_date", first_date);
                params.put("poddate", poddate);
                params.put("OriginStation", OriginStation);
                params.put("pod_post_date", pod_post_date);
                params.put("CustomerFeedBack", CustomerFeedBack);

                // Making a request to url and getting response
                 jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + "rvcPODDataNew_Insert", WebRequest.POST, params);

                if (jsonStr.contains("rCode : 0")) {
                    // FLAG THE WAYBILL AS UPDATED
                    sql = "UPDATE history_pod set outstation_Transfer='Y' where waybillnumber='" + AwbNo + "'";
                    db.dynamicInsert(SyncData.this, sql);
                    //DELETE THE RECORD
                    sql="DELETE FROM history_pod where outstation_Transfer='Y' and waybillnumber='"+ AwbNo.toString() + "'";
                    db.dynamicInsert(SyncData.this,sql);
                }
            }
            if(arg0[0] == "CHMPOD"){
                String DeviceID = db.getDeviceID(SyncData.this);//arg0[1];
                String UserID = db.getUserID(SyncData.this);//arg0[2];
                String AwbNo = arg0[3];
                String ReceivedBy = arg0[4];
                String DateReceived = arg0[5];
                String ServiceType = arg0[6];

                // Building Parameters
                HashMap<String, String> params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("AwbNo", AwbNo);
                params.put("ReceivedBy", ReceivedBy);
                params.put("DateReceived", DateReceived);
                params.put("ServiceType", ServiceType);

                // Making a request to url and getting response
                jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + "rvcChemonicPODData_Insert", WebRequest.POST, params);

                if (jsonStr.contains("rCode : 0")) {
                    // FLAG THE WAYBILL AS UPDATED
                    sql = "UPDATE ChemonicsPOD set TransferStatus='Y' where WayBillNumber='" + AwbNo + "'";
                    db.dynamicInsert(SyncData.this, sql);
                    //DELETE THE RECORD
                    sql="DELETE FROM ChemonicsPOD where TransferStatus='Y' and WayBillNumber='"+ AwbNo.toString() + "'";
                    db.dynamicInsert(SyncData.this,sql);
                }
            }
            if(arg0[0] == "SCANS"){
                String DeviceID = db.getDeviceID(SyncData.this);//arg0[1];
                String UserID = db.getUserID(SyncData.this);//arg0[2];
                String AwbNo = arg0[3];
                String Scan_Status = arg0[4];
                String Origin = arg0[5];
                String Destination = arg0[6];
                String Date = arg0[7];
                String Weight = arg0[8];
                String BatchNo = arg0[9];
                String Pieces = arg0[10];
                String SealNo = arg0[11];
                String VehicleNo = arg0[12];
                String Tag = arg0[13];
                String Express_Centre_Code = arg0[14];
                String Content_Type = arg0[15];
                String Route = arg0[16];


                // Building Parameters


                HashMap<String, String> params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("AwbNo", AwbNo);
                params.put("Scan_Status", Scan_Status);
                params.put("Origin", Origin);
                params.put("Destination", Destination);
                params.put("ScanDate", Date);
                params.put("Weight", Weight);
                params.put("BatchNo", BatchNo);
                params.put("Pieces", Pieces);
                params.put("SealNo", Global.convertNullToEmptyString(SealNo));
                params.put("VehicleNo", Global.convertNullToEmptyString(VehicleNo));
                params.put("Tag", Global.convertNullToEmptyString(Tag));
                params.put("Express_Centre_Code", Global.convertNullToEmptyString(Express_Centre_Code));
                params.put("Content_Type", Content_Type);
                params.put("Route", Global.convertNullToEmptyString(Route));

                // Making a request to url and getting response
                jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + "rvcScanData_Insert", WebRequest.POST, params);


                if (jsonStr.contains("rCode : 0")){
                    // FLAG THE WAYBILL AS UPDATED
                    sql="UPDATE SCANS set TransferStatus='Y' where AWBNO='" + AwbNo + "' and SCAN_STATUS='" + Scan_Status + "'";
                    db.dynamicInsert(SyncData.this,sql);
                    //DELETE ALL UPDATE SCANS
                    sql = "DELETE FROM SCANS where TransferStatus='Y' and AWBNO='" + AwbNo.toString() + "' and SCAN_STATUS='" + Scan_Status + "'";
                    db.dynamicInsert(SyncData.this, sql);

                    AwbNo = "";
                    Scan_Status = "";
                    Origin = "";
                    Destination = "";
                    Date = "";
                    Weight = "";
                    BatchNo = "";
                    Pieces = "";
                    SealNo = "";
                    VehicleNo = "";
                    Tag = "";
                    Express_Centre_Code = "";
                    Content_Type = "";
                    Route = "";
                }
            }
            if(arg0[0] == "SIGNATURE"){

                String DeviceID = db.getDeviceID(SyncData.this);//arg0[1];
                String UserID = db.getUserID(SyncData.this);//arg0[2];
                String AwbNo = arg0[3];
                String Signature = arg0[4];
                String DateCreated = arg0[5];



                // Building Parameters


                HashMap<String, String> params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("UserID", UserID);
                params.put("AwbNo", AwbNo);
                params.put("Signature", Signature);
                params.put("DateCreated", DateCreated);

                //DeviceID=string&UserID=string&Longitude=string&Latitude=string
                // Making a request to url and getting response
                jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + "rvcSignatureData_Insert", WebRequest.POST, params);

                if (jsonStr.contains("rCode : 0")){
                    // FLAG THE WAYBILL AS UPDATED
                    sql="UPDATE signatures set Transferred='Y' where AWBNO='" + AwbNo + "'";
                    db.dynamicInsert(SyncData.this,sql);
                    //DELETE ALL UPDATE Scans
                    sql = "DELETE FROM signatures where Transferred='Y' and AWBNO='" + AwbNo.toString() + "'";
                    db.dynamicInsert(SyncData.this, sql);
                }
            }
            if(arg0[0] == "PICKUP"){


                String DeviceID = db.getDeviceID(SyncData.this);//arg0[1];
                String AcctNo = arg0[2];
                String AwbNo = arg0[3];
                String COMPANY_NAME = arg0[4];
                String ADDRESS = arg0[5];
                String Box_Crating = arg0[6];
                String Box_Crating_Value = arg0[7];
                String Content_Description = arg0[8];
                String Delivery_Town = arg0[9];
                String Delivery_Type = arg0[10];
                String Department = arg0[11];
                String Destination = arg0[12];
                String Express_Centre = arg0[13];
                String INSURANCE_VALUE = arg0[14];
                String Origin = arg0[15];
                String Packaging = arg0[16];
                String Pickup_Date = arg0[17];
                String Pieces = arg0[18];
                String Recipient_Gsm = arg0[19];
                String Senders_Gsm = arg0[20];
                String UserID = db.getUserID(SyncData.this);//arg0[21];
                String Weight = arg0[22];
                String senders_name = arg0[23];
                String SENDERS_EMAIL = arg0[24];
                String RECIPIENTS_EMAIL = arg0[25];
                String AmountPaid = arg0[26];
                String DeliveryTownID = arg0[27];
                String DECLARED_VALUE = arg0[28];
                String WaybillEmailAlert = arg0[29];
                String ScansEmailAlert = arg0[30];
                String PodEmailAlert = arg0[31];
                String Flyer_No =arg0[32];
                String Prepaid =arg0[33];




                // Building Parameters


                HashMap<String, String> params = new HashMap<>();
                params.put("DeviceID", DeviceID);
                params.put("AcctNo", AcctNo);
                params.put("AwbNo", AwbNo);
                params.put("COMPANY_NAME", COMPANY_NAME);
                params.put("ADDRESS", ADDRESS);
                params.put("Box_Crating", Box_Crating);
                params.put("Box_Crating_Value", Box_Crating_Value);
                params.put("Content_Description", Content_Description);
                params.put("Delivery_Town", Delivery_Town);
                params.put("Delivery_Type", Delivery_Type);
                params.put("Department", Department);
                params.put("Destination", Destination);
                params.put("Express_Centre", Express_Centre);
                params.put("INSURANCE_VALUE", INSURANCE_VALUE);
                params.put("Origin", Origin);
                params.put("Packaging", Packaging);
                params.put("Pickup_Date", Pickup_Date);
                params.put("Pieces", Pieces);
                params.put("Recipient_Gsm", Recipient_Gsm);
                params.put("Senders_Gsm", Senders_Gsm);
                params.put("UserID", UserID);
                params.put("Weight", Weight);
                params.put("senders_name", senders_name);
                params.put("SENDERS_EMAIL", SENDERS_EMAIL);
                params.put("RECIPIENTS_EMAIL", RECIPIENTS_EMAIL);
                params.put("AmountPaid", AmountPaid);
                params.put("DeliveryTownID", DeliveryTownID);
                params.put("DECLARED_VALUE", DECLARED_VALUE);
                params.put("WaybillEmailAlert", Global.convertNullToEmptyString(WaybillEmailAlert));
                params.put("ScansEmailAlert", Global.convertNullToEmptyString(ScansEmailAlert));
                params.put("PodEmailAlert", Global.convertNullToEmptyString(PodEmailAlert));
                params.put("Flyer_No", Flyer_No);
                params.put("Prepaid", Prepaid);

               // Log.d("HTTP parameters: ", "> " + params);
                // Making a request to url and getting response
                jsonStr = webreq.makeWebServiceCall(Global.globalURLLocal + "rvcPickUpDataNew_Insert", WebRequest.POST, params);


                if (jsonStr.contains("rCode : 0")){
                    // FLAG THE WAYBILL AS UPDATED
                    sql="UPDATE PICKUP_BILLING set CUSTOM_FIELD2='Y' where AWBNO='" + AwbNo + "'";
                    db.dynamicInsert(SyncData.this,sql);
                    //DELETE  PICKUP RECORD
                    sql = "DELETE FROM PICKUP_BILLING where CUSTOM_FIELD2='Y' and AWBNO='" + AwbNo.toString() + "'";
                    db.dynamicInsert(SyncData.this, sql);
                }
            }

            if (jsonStr.contains("rCode : 1")){
                // erro inserting record
                // do nothing try the next record.
            }
            if (jsonStr.contains("rCode : 2")){
                // FLAG THE WAYBILL AS UPDATED
                retMsg= "Your device is not activated on the central server. /n Please contact administrator.";
            }
            //Log.d("Response: ", "> " + jsonStr);


            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

    }
    // END OF SECTION SYNC DATA

    public int onStartCommand(Intent intent, int flags, int startId) {

       // return START_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "Login session has expired!!", Toast.LENGTH_LONG).show();
        super.onDestroy();
        //Toast.makeText(this, "Please enable GPS", Toast.LENGTH_LONG).show();
    }
}
