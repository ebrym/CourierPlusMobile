package com.courierplus.mobile.PickUp;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.R;


public class recipientDetails extends Fragment {
    EditText txtRecipientName,txtRecipientAddress,txtRecipientPhoneNo,txtRecipientEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_recipient_details, container, false);

        txtRecipientName = (EditText) rootView.findViewById(R.id.txtRecipientName);
        txtRecipientAddress = (EditText) rootView.findViewById(R.id.txtRecipientAddress);
        txtRecipientPhoneNo = (EditText) rootView.findViewById(R.id.txtRecipientPhoneNo);
        txtRecipientEmail = (EditText) rootView.findViewById(R.id.txtRecipientEmail);

        txtRecipientName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupRecipientName = txtRecipientName.getText().toString();
//                if(s.length() != 0){
//                    Global.globalPickupRecipientName = txtRecipientName.getText().toString();
//                }
            }
        });
        txtRecipientAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupRecipientAddress = txtRecipientAddress.getText().toString();
//                if(s.length() != 0){
//                    Global.globalPickupRecipientAddress = txtRecipientAddress.getText().toString();
//                }
            }
        });
        txtRecipientPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupRecipientPhone = txtRecipientPhoneNo.getText().toString();
//                if(s.length() != 0){
//                    Global.globalPickupRecipientPhone = txtRecipientPhoneNo.getText().toString();
//                }
            }
        });
        txtRecipientEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupRecipientEmail = txtRecipientEmail.getText().toString();
//                if(s.length() != 0){
//                    Global.globalPickupRecipientEmail = txtRecipientEmail.getText().toString();
//                }
            }
        });

        return rootView;
    }
}
