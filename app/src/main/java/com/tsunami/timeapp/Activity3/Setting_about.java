package com.tsunami.timeapp.Activity3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.tsunami.timeapp.R;


/**
 * @author zhenglifeng
 */
public class Setting_about extends AppCompatActivity {

    private TextView tv_version;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_about);

        tv_version = (TextView) findViewById(R.id.tv_version);
        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting_about.this,AppVersion.class));
            }
        });

    }


    public void back(View view) {
        Setting_about.this.finish();
    }

}

