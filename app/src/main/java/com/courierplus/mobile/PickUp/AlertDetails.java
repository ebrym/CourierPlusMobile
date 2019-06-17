package com.courierplus.mobile.PickUp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.R;

public class AlertDetails extends Fragment {
 private CheckBox chkPickupAlert,chkScanAlert,chkPODAlert,chkPrepaid;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View  rootView = inflater.inflate(R.layout.fragment_alert_details, container, false);

        chkPickupAlert = (CheckBox) rootView.findViewById(R.id.chkPickupAlert);
        chkScanAlert = (CheckBox) rootView.findViewById(R.id.chkScanAlert);
        chkPODAlert = (CheckBox) rootView.findViewById(R.id.chkPODAlert);
        chkPrepaid = (CheckBox) rootView.findViewById(R.id.chkPrepaidAlert);

        chkPickupAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
           @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                    if (isChecked)
                        Global.globalPickupAlert = "Yes";
                    else
                        Global.globalPickupAlert = "No";
               }
            }
        );
        chkScanAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
          @Override
          public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
              if (isChecked)
                  Global.globalScanAlert = "Yes";
              else
                  Global.globalScanAlert = "No";
          }
        }
        );
        chkPODAlert.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (isChecked)
                    Global.globalPODAlert = "Yes";
                else
                    Global.globalPODAlert = "No";
            }
        }
        );
        chkPrepaid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                      @Override
                                                      public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                          if (isChecked)
                                                              Global.globalPrepaidAlert = "Y";
                                                          else
                                                              Global.globalPrepaidAlert = "N";
                                                      }
                                                  }
        );
        return rootView;
    }



}
