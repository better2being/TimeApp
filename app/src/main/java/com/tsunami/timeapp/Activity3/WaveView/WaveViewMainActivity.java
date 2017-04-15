package com.tsunami.timeapp.Activity3.WaveView;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.tsunami.timeapp.R;
/**
 * @author zhenglifeng
 */
public class WaveViewMainActivity extends AppCompatActivity {

    private static final String TAG = "Test";
    private long totalTime = 0;
    private long milliseconds = 0;
    private float level = 0;


    private WaveHelper mWaveHelper;

    private int mBorderColor = Color.parseColor("#673AB7");
    private int mBorderWidth = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_waveviewmain);

        Log.d(TAG,"onCreate");


        final WaveView waveView = (WaveView) findViewById(R.id.wave);
        waveView.setBorder(mBorderWidth, mBorderColor);
        //获取时间数据
        milliseconds = getIntent().getLongExtra("milliseconds",0);
        totalTime = getIntent().getLongExtra("totalTime",0);
        waveView.startCountDownTimer(milliseconds);
//        Toast.makeText(getApplicationContext(), "111111111111111111",
//                Toast.LENGTH_LONG).show();
        level = (float)(totalTime - milliseconds) / totalTime ;
        //参数分别为 水波上升所需的总时间，上升开始的高度，上升停止的高度 ZLF
        mWaveHelper = new WaveHelper(waveView,milliseconds,level,1f);

    }

    @Override
    protected void onPause() {
        super.onPause();
        mWaveHelper.cancel();
//        Toast.makeText(getApplicationContext(), "2222222222222222221",
//                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWaveHelper.start();
//        Toast.makeText(getApplicationContext(), "3333333333333333331",
//                Toast.LENGTH_LONG).show();
        Log.d(TAG, "onResume");
    }

}
