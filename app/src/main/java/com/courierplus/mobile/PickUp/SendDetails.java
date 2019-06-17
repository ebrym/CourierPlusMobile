package com.courierplus.mobile.PickUp;

/**
 * Created by iabdullahi on 6/17/2016.
 */
 import android.os.Bundle;
        import android.support.v4.app.Fragment;
 import android.text.Editable;
 import android.text.TextWatcher;
 import android.util.Log;
 import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
 import android.widget.EditText;

 import com.courierplus.mobile.Global;
 import com.courierplus.mobile.R;

public class SendDetails extends Fragment {
    EditText txtSendersName,txtDepartment,txtSenderPhoneNo,txtSenderEmail;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_sender_details, container, false);
        txtSendersName = (EditText) rootView.findViewById(R.id.txtSendersName);
        txtDepartment = (EditText) rootView.findViewById(R.id.txtDepartment);
        txtSenderPhoneNo = (EditText) rootView.findViewById(R.id.txtSenderPhoneNo);
        txtSenderEmail = (EditText) rootView.findViewById(R.id.txtSenderEmail);

        txtSendersName.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupSenderName = txtSendersName.getText().toString();

            }
        });
        txtDepartment.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupSenderDepartment = txtDepartment.getText().toString();

            }
        });
        txtSenderPhoneNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupSenderPhone = txtSenderPhoneNo.getText().toString();

            }
        });
        txtSenderEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {}
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                Global.globalPickupSenderEmail = txtSenderEmail.getText().toString();

            }
        });


        return rootView;


    }
    public void onRefresh() {
        Log.d("Ondetach : Account no", Global.globalPickupAccountNo  );
        // setAccountNo();
    }
}