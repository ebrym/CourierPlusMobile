package com.courierplus.mobile.PickUp;

import android.app.TabActivity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.courierplus.mobile.Global;
import com.courierplus.mobile.PickupActivity;
import com.courierplus.mobile.R;

/**
 * Created by iabdullahi on 6/20/2016.
 */
public class AccountDetails extends Fragment {
    EditText txtAccountNumber;


   public interface accountValidation {
       public void validateAccount(String accountNo);
   }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_pickup, container, false);

        txtAccountNumber = (EditText) rootView.findViewById(R.id.txtAccountNumber);

        txtAccountNumber.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if(s.length() != 0 && s.length() == 10){
                    Global.globalPickupAccountNo = txtAccountNumber.getText().toString();
                }
            }
        });


        return rootView;
    }






    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

}
