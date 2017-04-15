package com.tsunami.timeapp.Activity3.ScanErWeiMa.myzxingtest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.tsunami.timeapp.R;
/**
 * @author zhenglifeng
 */
public class MyErWeiMaActivity extends AppCompatActivity{

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myerweima);
    }

    //方法：控件View的点击事件
    public void onClickClockScan(View v) {
        Intent intent = new Intent(MyErWeiMaActivity.this, ScanErWeiMaActivity.class);
        startActivity(intent);

    }

    public void onClickMyErWeiMaBack(View v) {
        MyErWeiMaActivity.this.finish();
    }
}
