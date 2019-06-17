package com.courierplus.mobile;


import java.util.Timer;
import java.util.TimerTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.Window;

//import com.redstar.rsemobile.BuildConfig;

public class MainActivity extends Activity {

    //@Override
    private Timer timer = new Timer();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
       setContentView(R.layout.activity_main);
        timer.schedule(new CountDown(), 2500);
    }
    private class CountDown extends TimerTask {
        public void run() {
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(i);
            finish();

        }
    }
}
