package com.courierplus.mobile;


/**
 * Created by iabdullahi on 5/23/2016.
 *//////////////////////////

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Global {
    //public static String globalURLLocalTest = "http://192.100.64.228/MobileAdmin/service.asmx/";
    //public static String globalURLLocal = "http://52.59.230.245/mobileadmin/service.asmx/";
    public static String globalURLLocal = "http://35.157.66.43/mobileadmin/service.asmx/";
   //public static String globalURLLocal = "http://192.100.64.10:1264/service.asmx/";
   // public static String globalURL = "https://www.redstarplc.com:8444/mobileservice/service.asmx/";





    static String ErrorMessageToReturn = "";

    public static String globalAWBno = "";

    public static String globalOrigin = "";
    public static String globalDestination = "";
    public static String globalPodDeliveryStation = "";
    public static String globalUserStation = "";
    public static String globalScanStatus = "";
    public static String globalWeight = "";
    public static String globalPieces = "";
    public static String globalTagNo = "";
    public static String globalSealNo = "";
    public static String globalVehicleNo = "";
    public static String globalContentType = "";
    public static String globalRoute = "";
    public static String globalBatchNo = "";
    public static String globalLongitude = "";
    public static String globalLatitude = "";
    public static String globalDeviceIMEI = "";
    public static String globalDeviceType = "";
    public static String globalUserName = "";




    // global variables for pod
    public static String globalPodAwbno = "";
    public static String globalPodOrigin = "";
    public static String globalPodStatus = "";
    public static String globalPodExitDate = "";
    public static String globalPodFirstDate = "";
    public static String globalPodBy = "";
    public static String globalPodRemarks = "";
    public static String globalPodSignature = "";

    public static String globalUserCount = "";

    // global variables for chemonics pod
    public static String globalCHMPodAwbno = "";
    public static String globalCHMPodBy = "";
    public static String globalCHMPodSignature = "";
    public static String globalCHMPodDate = "";
    public static String globalCHMServiceType = "";

// Global variables for pickup
    public static String globalPickupAccountNo = "";
    public static String globalPickupAwbno = "";
    public static String globalPickupOrigin = "";
    public static String globalPickupDestination = "";
    public static String globalPickupSenderName = "";
    public static String globalPickupSenderDepartment = "";
    public static String globalPickupSenderAddress = "";
    public static String globalPickupSenderPhone = "";
    public static String globalPickupSenderEmail = "";
    public static String globalPickupRecipientName = "";
    public static String globalPickupRecipientAddress = "";
    public static String globalPickupRecipientPhone = "";
    public static String globalPickupRecipientEmail = "";

    public static String globalPickupWeight = "";
    public static String globalPickupPieces = "";
    public static String globalPickupDescription = "";
    public static String globalPickupDeclaredValue = "";
    public static String globalPickupInsurance = "";
    public static String globalPickupCratingValue = "";

    public static String globalPickupPackaging = "";
    public static String globalPickupBoxCrating = "";
    public static String globalPickupExpressCenter = "";

    public static String globalPickupDeliveryType = "";
    public static String globalPickupOnforwarding = "";
    public static String globalPickupDeliverTownID = "";
    public static String globalPickupAmount = "";
    public static String globalPickupFlyerNo = "";

    public static String globalPickupAlert = "";
    public static String globalPODAlert = "";
    public static String globalScanAlert = "";
    public static String globalPrepaidAlert = "N";
    public static String globalDataListOpertionType = "";
    public static String globalDataListOpertionCode = "";

    public static String ResponseCode = "";
    public static String ResponseMessage = "";


    public static boolean globalLocationEnabled = false;
    public static String globalMultiplePOD = "";
    public static String globalHeader2 = "";
    public static String globalHeader3 = "";
    public static String globalReportDetails = "";
    public static Bitmap globalMultipleSignature = null;

    public static boolean globalSyncPOD = false;
    public static boolean globalSyncCHMPOD = false;
    public static String globalSyncCHMServiceType = "";
    public static boolean globalSyncPickUp = false;
    public static boolean globalSyncScans = false;
    public static boolean globalSyncSignature = false;
    // for waybill validation.
    public static boolean globalWaybillValidation = false;
    public static int globalWaybillLength = 0;


    public static String convertNullToEmptyString(String value) {
         return value == null ? "" : value;
    }
    public static String convertEmptyStringToNull(String value){
        return value == "" ? null : value;

    }

    public static boolean isWaybillValid(String text){
        // firs test.
        // text for the length of the text.
        globalWaybillLength = text.length();
        if (globalWaybillLength >= 8 && text.matches("^([a-zA-Z0-9]+$)")){

                globalWaybillValidation = true;
         }

        globalWaybillLength = 0 ;
        return globalWaybillValidation;
    }

    public static AlertDialog.Builder AssetDialog(String message, Context thecontext) {

        AlertDialog.Builder builder = new AlertDialog.Builder(thecontext);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });

        return builder;
    }


    public static String[] split(String strString, String strDelimiter) {
        int iOccurrences = 0;
        int iIndexOfInnerString = 0;
        int iIndexOfDelimiter = 0;
        int iCounter = 0;

        // Check for null input strings.
        if (strString == null) {
            throw new NullPointerException("Input string cannot be null.");
        }
        // Check for null or empty delimiter strings.
        if (strDelimiter.length() <= 0 || strDelimiter == null) {
            throw new NullPointerException("Delimeter cannot be null or empty.");
        }

        // If strString begins with delimiter then remove it in order to comply
        // with the desired format.
        if (strString.startsWith(strDelimiter)) {
            strString = strString.substring(strDelimiter.length());
        }

        // If strString does not end with the delimiter then add it to the
        // string in order to comply with the desired format.
        if (!strString.endsWith(strDelimiter)) {
            strString += strDelimiter;
        }

        // Count occurrences of the delimiter in the string.
        // Occurrences should be the same amount of inner strings.
        while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
                iIndexOfInnerString)) != -1) {
            iOccurrences += 1;
            iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();
        }

        // Declare the array with the correct size.
        String[] strArray = new String[iOccurrences];

        // Reset the indices.
        iIndexOfInnerString = 0;
        iIndexOfDelimiter = 0;

        // Walk across the string again and this time add the strings to the
        // array.
        while ((iIndexOfDelimiter = strString.indexOf(strDelimiter,
                iIndexOfInnerString)) != -1) {

            // Add string to array.
            strArray[iCounter] = strString.substring(iIndexOfInnerString,
                    iIndexOfDelimiter);

            // Increment the index to the next character after the next
            // delimiter.
            iIndexOfInnerString = iIndexOfDelimiter + strDelimiter.length();

            // Inc the counter.
            iCounter += 1;
        }
        return strArray;
    }
}
