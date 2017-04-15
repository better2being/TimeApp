package com.tsunami.timeapp.Activity3.clock;


import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tsunami.timeapp.Activity3.WaveView.WaveViewMainActivity;
import com.tsunami.timeapp.R;



/**
 * @author zhenglifeng
 */
public class XiuYiXiuActivity extends AppCompatActivity {

    private boolean changeStytle = false;
    private WhewView wv;
    private RoundImageView my_photo;
    private ImageView ivSecondTimerLaunchPicker;
    private static final int Nou = 2;
    private long totalTime = 0;
    private long milliseconds = 0;
    private int hour = 0;
    private int minute = 0;
    private boolean isSetCountDownTimer = false ;
    private boolean isFirstSetTime = true;

//    // 声明一个SoundPool
//    private SoundPool sp;
//    // 定义一个整型用load（）；来设置suondIDf
//    private int music;

    private Handler xiuyixiuhandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == Nou) {
                // 每隔10s响一次
                xiuyixiuhandler.sendEmptyMessageDelayed(Nou, 5000);
//                sp.play(music, 1, 1, 0, 0, 1);
            }
        }
    };

    final Handler handler = new Handler(){
        public void handleMessage(Message msg){         // handle message
            switch (msg.what) {
                case 1:
                    milliseconds-=100;
                    if(milliseconds > 0) {
                        Message message = handler.obtainMessage(1);
                        handler.sendMessageDelayed(message, 100);      // send message
                    }
                    else{
                        milliseconds = 0;
                        Vibrator vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
                        long [] pattern = {1000,1000,1000,1000};   // 停止 开启 停止 开启
                        vibrator.vibrate(pattern,-1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
                        Toast.makeText(getApplicationContext(), "倒计时结束！",
                                Toast.LENGTH_SHORT).show();
                    }
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countertimerstart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.secondtimertoolbar);
        setSupportActionBar(toolbar);
        //如果结束定时 布尔值设为假 ZLF
        if(!isFirstSetTime) {
            isSetCountDownTimer = !(getIntent().getBooleanExtra("stopCountDownTimer", false));
        }

//        final ActionBar ab = getSupportActionBar();
//        ab.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        ab.setDisplayHomeAsUpEnabled(true);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 2 添加的代码
            getSupportActionBar().setTitle("定时器");
            /////////////////////////////////
        }

        // 2 点击时间选择器按钮的响应 颜色设置
        ivSecondTimerLaunchPicker = (ImageView) findViewById(R.id.ivSecondTimerLaunchPicker);
        ivSecondTimerLaunchPicker.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ivSecondTimerLaunchPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.removeMessages(1);

                //弹出时间选择对话框 默认倒计时1小时
                new TimePickerDialog(XiuYiXiuActivity.this, new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        setTitle(String.format("%02d:%02d", hourOfDay, minute));
                        XiuYiXiuActivity.this.hour = hourOfDay;
                        XiuYiXiuActivity.this.minute = minute;
                        //修正查询倒计时时间的设定
                        milliseconds = ( hour * 60 * 60 + XiuYiXiuActivity.this.minute * 60 ) * 1000;
                        totalTime = milliseconds ;
                    }

                }, 1,0, true).show();
            }
        });


//        //弹出时间选择对话框 默认倒计时1小时
//        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
//
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                setTitle(String.format("%02d:%02d", hourOfDay, minute));
//                XiuYiXiuActivity.this.hour = hourOfDay;
//                XiuYiXiuActivity.this.minute = minute;
//                //修正查询倒计时时间的设定
//                milliseconds = ( hour * 60 * 60 + XiuYiXiuActivity.this.minute * 60 ) * 1000;
//                totalTime = milliseconds ;
//            }
//
//        }, 1,0, true).show();

        initView();
    }

    private void initView() {

//        // 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//        sp = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        // 把你的声音素材放到res/raw里，第2个参数即为资源文件，第3个为音乐的优先级
//        music = sp.load(this, R.raw.hongbao_gq, 1);

        wv = (WhewView) findViewById(R.id.wv);
        wv.start();
        my_photo = (RoundImageView) findViewById(R.id.my_photo);
        my_photo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });
        my_photo.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                if(wv.isStarting()){
                    //如果动画正在运行就停止，否则就继续执行
//                    wv.stop();
//                    //结束进程
//                    handler.removeMessages(Nou);
                    if(!changeStytle){
                        if(milliseconds == 0){
                            Toast.makeText(getApplicationContext(), "请设定倒计时时间",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else if(!isSetCountDownTimer){
                            Message message = handler.obtainMessage(1);     // Message
                            handler.sendMessageDelayed(message, 100);
                            isSetCountDownTimer = true ;//已设置过时间
                            isFirstSetTime = false ;

                            Intent intent = new Intent(XiuYiXiuActivity.this,MainActivity.class);
                            intent.putExtra("milliseconds",milliseconds);
                            startActivity(intent);
                        }
                        else{
                            Intent intent = new Intent(XiuYiXiuActivity.this,MainActivity.class);
                            intent.putExtra("milliseconds",milliseconds);
                            startActivity(intent);
                        }
                    }
                    else{
                        if (milliseconds == 0 || totalTime == 0) {
                            Toast.makeText(getApplicationContext(), "请设定倒计时时间",
                                    Toast.LENGTH_SHORT).show();
                        } else if (!isSetCountDownTimer) {
                            Message message = handler.obtainMessage(1);     // Message
                            handler.sendMessageDelayed(message, 100);
                            isSetCountDownTimer = true;//已设置过时间
                            isFirstSetTime = false;

                            Intent intent = new Intent(XiuYiXiuActivity.this, WaveViewMainActivity.class);
                            intent.putExtra("milliseconds", milliseconds);
                            intent.putExtra("totalTime",totalTime);
                            startActivity(intent);
                        } else {
                            Intent intent = new Intent(XiuYiXiuActivity.this, WaveViewMainActivity.class);
                            intent.putExtra("milliseconds", milliseconds);
                            intent.putExtra("totalTime",totalTime);
                            startActivity(intent);
                        }
                    }



                }else{
                    // 执行动画
                    wv.start();
                    xiuyixiuhandler.sendEmptyMessage(Nou);
                    Toast.makeText(XiuYiXiuActivity.this,"再点击一次进入计时器界面",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
       // handler.removeMessages(Nou);ZLF
        xiuyixiuhandler.removeMessages(Nou);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_second_timer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                finish();
                return true;

            case R.id.secondtimer_change_view:
                changeStytle = !changeStytle;
//                View toastRoot = getLayoutInflater().inflate(R.layout.my_toast, null);
//                Toast toast=new Toast(getApplicationContext());
//                toast.setView(toastRoot);
//                TextView tv=(TextView)toastRoot.findViewById(R.id.TextViewInfo);
//                tv.setText("说明：这是一个自定义边框和底色的提示框。");
//                toast.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
