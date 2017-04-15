package com.tsunami.timeapp.Activity3;

/**
 * Created by 2010330579 on 2016/9/12.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tsunami.timeapp.R;

/**
 * @author zhenglifeng
 */
public class AppVersion extends AppCompatActivity {

    private TextView tv_version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_version);



    }


    public void back(View view) {
        AppVersion.this.finish();
    }

}

