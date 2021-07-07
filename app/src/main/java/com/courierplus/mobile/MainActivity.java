package com.courierplus.mobile;


import java.util.Timer;
import java.util.TimerTask;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;

import db.DataDB;


public class MainActivity extends Activity {
    DataDB db;
    //@Override
    private Timer timer = new Timer();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.activity_main);
        // get Instance  of Database Adapter
        db=new DataDB();
       // timer.schedule(new CountDown(), 2500);
        timer.schedule(new CountDown(), 2500);

    }
    private class CountDown extends TimerTask {
        public void run() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                Global.globalUserCount = db.getUserCount(MainActivity.this).toString();
                //Log.d("User count : ", Global.globalUserCount.toString());
                if(Global.globalUserCount.equals("0")){
                    Intent i = new Intent(MainActivity.this, deviceIdActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }

            }else {
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }

        }
    }
}
