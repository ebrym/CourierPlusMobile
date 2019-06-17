package com.courierplus.mobile.PickUp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.R;

import java.util.List;

import db.DataDB;


public class DestinationDetails extends Fragment {
    private Spinner spnPickUpOrigin, spnPickUpDestination,spnPickUpOnforwarding,spnPickUpDeliveryType;
    private EditText txtPickupAmount,txtPickupFlyerNo;
    DataDB db;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_destination, container, false);

        spnPickUpOrigin = (Spinner) rootView.findViewById(R.id.spnPickUpOrigin);
        spnPickUpDestination = (Spinner) rootView.findViewById(R.id.spnPickUpDestination);
        spnPickUpOnforwarding = (Spinner) rootView.findViewById(R.id.spnPickUpOnforwarding);
        spnPickUpDeliveryType = (Spinner) rootView.findViewById(R.id.spnPickUpDeliveryType);
        txtPickupAmount = (EditText) rootView.findViewById(R.id.txtPickupAmount);
        txtPickupFlyerNo  = (EditText) rootView.findViewById(R.id.txtPickupFlyerNo);
        loadspnPickUpOrigin();
        loadspnPickUpDeliveryType();

        spnPickUpOrigin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Origin = String.valueOf(parent.getItemAtPosition(position).toString());
                if(!Origin.contentEquals(""))
                {
                    Global.globalPickupOrigin= Origin.toString();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spnPickUpDestination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String des = String.valueOf(parent.getItemAtPosition(position).toString());
                if(!des.contentEquals(""))
                {
                    Global.globalPickupDestination= des.toString();
                    loadspnPickUpOnForwarding(des);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spnPickUpOnforwarding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Onforwarding = String.valueOf(parent.getItemAtPosition(position).toString());
                Global.globalPickupOnforwarding = Onforwarding.toString();
//                if(!Onforwarding.contentEquals(""))
//                {
//                    Global.globalPickupOnforwarding = Onforwarding.toString();
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spnPickUpDeliveryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String Onforwarding = String.valueOf(parent.getItemAtPosition(position).toString());
                Global.globalPickupDeliveryType = Onforwarding.toString();

//                if(!Onforwarding.contentEquals(""))
//                {
//                    Global.globalPickupDeliveryType = Onforwarding.toString();
//                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });


        txtPickupAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupAmount = txtPickupAmount.getText().toString();
//                if(s.length() != 0){
//                    Global.globalPickupAmount = txtPickupAmount.getText().toString();
//                }
            }
        });
        txtPickupFlyerNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupFlyerNo = txtPickupFlyerNo.getText().toString();
//                if(s.length() != 0){
//                    Global.globalPickupFlyerNo = txtPickupFlyerNo.getText().toString();
//                }
            }
        });

        return rootView;
    }
    private void loadspnPickUpDeliveryType() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getDeliveryType(getActivity());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPickUpDeliveryType.setAdapter(dataAdapter);
    }
    private void loadspnPickUpOrigin() {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getStation(getActivity());
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPickUpOrigin.setAdapter(dataAdapter);
        spnPickUpDestination.setAdapter(dataAdapter);
    }
    private void loadspnPickUpOnForwarding(String des) {
        // database handler
        //DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        db = new DataDB();
        // Spinner Drop down elements
        List<String> stationCodes = db.getOnForwarding(getActivity(),String.valueOf(des));
        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, stationCodes);

        // Drop down layout style - list view with radio button
        dataAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spnPickUpOnforwarding.setAdapter(dataAdapter);
    }



}
