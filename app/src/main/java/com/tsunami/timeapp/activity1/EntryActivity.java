package com.tsunami.timeapp.activity1;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.WindowManager;


import com.tsunami.timeapp.Activity3.GesturePassword.GestureVerifyActivity;
import com.tsunami.timeapp.R;

import java.util.Timer;
import java.util.TimerTask;

/**
 * @author shenxiaoming
 */
public class EntryActivity extends BaseActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.entry_activity);


        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {

                startActivity(new Intent(EntryActivity.this, GestureVerifyActivity.class));
                EntryActivity.this.finish();
            }
        };
        timer.schedule(task, 1500); // 3秒后跳转
    }



}
