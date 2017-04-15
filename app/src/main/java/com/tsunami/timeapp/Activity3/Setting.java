package com.tsunami.timeapp.Activity3;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.duanqu.qupai.dagger.PerActivity;
import com.tsunami.timeapp.Activity3.GesturePassword.GestureEditActivity;
import com.tsunami.timeapp.Activity3.ScanErWeiMa.myzxingtest.MyErWeiMaActivity;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.PersonActivity;

/**
 * @author zhenglifeng
 */
public class Setting extends AppCompatActivity implements View.OnClickListener {

    private ImageView setting_back;
//    private CheckBox checkBox;
//    private RelativeLayout setting_stytle_change;
//    private RelativeLayout setting_personal_center;
    private RelativeLayout setting_gesturepassword;
    private RelativeLayout setting_erweima;
    private RelativeLayout setting_about;
    private RelativeLayout setting_pay_for_me;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_layout);

        setting_back = (ImageView) findViewById(R.id.setting_back);
        setting_back.setOnClickListener(this);

//        setting_stytle_change = (RelativeLayout) findViewById(R.id.setting_stytle_change);
//        setting_stytle_change.setOnClickListener(this);
//        setting_personal_center = (RelativeLayout) findViewById(R.id.setting_personal_center);
//        setting_personal_center.setOnClickListener(this);
        setting_gesturepassword = (RelativeLayout) findViewById(R.id.setting_gesturepassword);
        setting_gesturepassword.setOnClickListener(this);
        setting_erweima = (RelativeLayout) findViewById(R.id.setting_erweima);
        setting_erweima.setOnClickListener(this);
        setting_about = (RelativeLayout) findViewById(R.id.setting_about);
        setting_about.setOnClickListener(this);
        setting_pay_for_me = (RelativeLayout) findViewById(R.id.setting_pay_for_me);
        setting_pay_for_me.setOnClickListener(this);
        //checkBox = (CheckBox)findViewById(R.id.setting_right_hand_mode_checkBox);

    }
    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.setting_back:
                finish();
                break;
//            case R.id.setting_right_hand_mode_checkBox:
//
//                break;
//            case R.id.setting_stytle_change:
//
//                break;
//            case R.id.setting_personal_center:
//                startActivity(new Intent(Setting.this,PersonActivity.class));
//                break;
            case R.id.setting_gesturepassword:
                startActivity(new Intent(Setting.this,GestureEditActivity.class));
                break;

            case R.id.setting_erweima:
                startActivity(new Intent(Setting.this,MyErWeiMaActivity.class));
                break;

            case R.id.setting_about:
                Intent intent = new Intent(Setting.this,Setting_about.class);
                startActivity(intent);
                break;

            case R.id.setting_pay_for_me:

                break;

        }
    }
}
