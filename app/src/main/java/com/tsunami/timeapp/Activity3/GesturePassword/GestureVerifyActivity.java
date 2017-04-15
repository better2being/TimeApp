package com.tsunami.timeapp.Activity3.GesturePassword;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsunami.timeapp.Activity3.GesturePassword.widget.GestureContentView;
import com.tsunami.timeapp.Activity3.GesturePassword.widget.GestureDrawline.GestureCallBack;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity1.NewLoginActivity;


/**
 * @author zhenglifeng
 */
public class GestureVerifyActivity extends AppCompatActivity implements View.OnClickListener {
	/** 手机号码*/
	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
	/** 意图 */
	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
	//private RelativeLayout mTopLayout;
	/*private TextView mTextTitle;
	private TextView mTextCancel;*/
	/*private ImageView mImgUserLogo;*/
	/*private TextView mTextPhoneNumber;*/
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	/*private TextView mTextForget;
	private TextView mTextOther;*/
	private String mParamPhoneNumber;
	private long mExitTime = 0;
	private int mParamIntentCode;
	// 2 验证密码
	private String password = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_verify);
		//在这里验证密码是否被保存 ZLF
		SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);

		password = pref.getString("password","");
		if(password.equals("")){
			//若密码未被保存，直接跳到登陆页面
			if(getIntent().getIntExtra("pageNum",0)==0){
				Intent intent = new Intent(GestureVerifyActivity.this,NewLoginActivity.class);
				startActivity(intent);
				this.finish();
			}
			else if(getIntent().getIntExtra("pageNum",0)==1){
				startActivity(new Intent(GestureVerifyActivity.this, com.tsunami.timeapp.ui.MainActivity.class).putExtra("pageNum",1));
                this.finish();
			}
		}

		ObtainExtraData();
		setUpViews();
		setUpListeners();
	}

	private void ObtainExtraData() {
		mParamPhoneNumber = getIntent().getStringExtra(PARAM_PHONE_NUMBER);
		mParamIntentCode = getIntent().getIntExtra(PARAM_INTENT_CODE, 0);
	}

	private void setUpViews() {
		//mTopLayout = (RelativeLayout) findViewById(R.id.top_layout);
		/*mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextCancel = (TextView) findViewById(R.id.text_cancel);*/
		/*mImgUserLogo = (ImageView) findViewById(R.id.user_logo);
		mTextPhoneNumber = (TextView) findViewById(R.id.text_phone_number);*/
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		/*mTextForget = (TextView) findViewById(R.id.text_forget_gesture);
		mTextOther = (TextView) findViewById(R.id.text_other_account);*/

		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, true, password,
				new GestureCallBack() {

					@Override
					public void onGestureCodeInput(String inputCode) {

					}

					@Override
					public void checkedSuccess() {
						mGestureContentView.clearDrawlineState(0L);
						//Toast.makeText(GestureVerifyActivity.this, "密码正确", Toast.LENGTH_SHORT).show();
						GestureVerifyActivity.this.finish();
						//在这里可以设定跳转向哪个页面 ZLF
						if(getIntent().getIntExtra("pageNum",0)==0){
							Intent intent = new Intent(GestureVerifyActivity.this,NewLoginActivity.class);
							startActivity(intent);
						}
						else if(getIntent().getIntExtra("pageNum",0)==1){
							startActivity(new Intent(GestureVerifyActivity.this, com.tsunami.timeapp.ui.MainActivity.class).putExtra("pageNum",1));

						}


						//或者重写finish
					}

					@Override
					public void checkedFail() {
						mGestureContentView.clearDrawlineState(1300L);
						mTextTip.setVisibility(View.VISIBLE);
						mTextTip.setText(Html
								.fromHtml("<font color='#c70c1e'>密码错误</font>"));
						// 左右移动动画
						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureVerifyActivity.this, R.anim.shake);
						mTextTip.startAnimation(shakeAnimation);
					}
				});
		// 设置手势解锁显示到哪个布局里面 ZLF
		mGestureContentView.setParentView(mGestureContainer);
	}

	private void setUpListeners() {
		//mTextCancel.setOnClickListener(this);
		/*mTextForget.setOnClickListener(this);
		mTextOther.setOnClickListener(this);*/
	}

	private String getProtectedMobile(String phoneNumber) {
		if (TextUtils.isEmpty(phoneNumber) || phoneNumber.length() < 11) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		builder.append(phoneNumber.subSequence(0,3));
		builder.append("****");
		builder.append(phoneNumber.subSequence(7,11));
		return builder.toString();
	}

	@Override
	public void onClick(View v) {
		/*switch (v.getId()) {
			case R.id.text_cancel:
				this.finish();
				break;
			default:
				break;
		}*/
	}

}