package com.tsunami.timeapp.Activity3.GesturePassword;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tsunami.timeapp.Activity3.GesturePassword.widget.GestureContentView;
import com.tsunami.timeapp.Activity3.GesturePassword.widget.GestureDrawline.GestureCallBack;
import com.tsunami.timeapp.Activity3.GesturePassword.widget.LockIndicator;
import com.tsunami.timeapp.R;


/**
 * @author zhenglifeng
 */
public class GestureEditActivity extends AppCompatActivity implements OnClickListener {
	/** 手机号码*/
	public static final String PARAM_PHONE_NUMBER = "PARAM_PHONE_NUMBER";
	/** 意图 */
	public static final String PARAM_INTENT_CODE = "PARAM_INTENT_CODE";
	/** 首次提示绘制手势密码，可以选择跳过 */
	public static final String PARAM_IS_FIRST_ADVICE = "PARAM_IS_FIRST_ADVICE";
	/*private TextView mTextTitle;
	private TextView mTextCancel;*/
	private LockIndicator mLockIndicator;
	private TextView mTextTip;
	private FrameLayout mGestureContainer;
	private GestureContentView mGestureContentView;
	private TextView mTextReset;
	private String mParamSetUpcode = null;
	private String mParamPhoneNumber;
	private boolean mIsFirstInput = true;
	private String mFirstPassword = null;
	private String mConfirmPassword = null;
	private int mParamIntentCode;

	private boolean alreadySetPassword = false;
	private String password = "";
	private SharedPreferences pref;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gesture_edit);
		setUpViews();
		setUpListeners();


		pref = getSharedPreferences("data",MODE_PRIVATE);
		alreadySetPassword = pref.getBoolean("setPassword",false);
		if(alreadySetPassword){
			//设置过密码
			mTextTip.setText(Html.fromHtml("<font color='#673AB7'>请先输入已设置的密码</font>"));
			password = pref.getString("password","");
		}

	}

	private void setUpViews() {
		/*mTextTitle = (TextView) findViewById(R.id.text_title);
		mTextCancel = (TextView) findViewById(R.id.text_cancel);*/
		//mTextReset = (TextView) findViewById(R.id.text_reset);
		//mTextReset.setClickable(false);
		//mLockIndicator = (LockIndicator) findViewById(R.id.lock_indicator);
		mTextTip = (TextView) findViewById(R.id.text_tip);
		mGestureContainer = (FrameLayout) findViewById(R.id.gesture_container);
		// 初始化一个显示各个点的viewGroup
		mGestureContentView = new GestureContentView(this, false, "", new GestureCallBack() {
			@Override
			public void onGestureCodeInput(String inputCode) {
				//是否已经设置过密码 ZLF
				if(alreadySetPassword){
					if(inputCode.equals(pref.getString("password",""))){
						//如果输入正确
						mGestureContentView.clearDrawlineState(0L);
						mTextTip.setText(Html.fromHtml("<font color='#673AB7'>绘制密码图案</font>"));
						//将密码设为空 同时是否设置过密码设为否
						SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
						editor.putString("password","");
						editor.putBoolean("setPassword", false);
						editor.apply();
						alreadySetPassword = false;
					}
					else{
						//如果输入错误
						mGestureContentView.clearDrawlineState(0L);
						mTextTip.setText(Html
								.fromHtml("<font color='#c70c1e'>密码错误</font>"));
						// 左右移动动画
						Animation shakeAnimation = AnimationUtils.loadAnimation(GestureEditActivity.this, R.anim.shake);
						mTextTip.startAnimation(shakeAnimation);
					}

					if (!isInputPassValidate(inputCode)) {
						mTextTip.setText(Html.fromHtml("<font color='#673AB7'>最少连接4个点, 请重新输入</font>"));
						mGestureContentView.clearDrawlineState(0L);
						return;
					}

				}
				else{
					//没设置过密码 或需要重新设置密码
					if (!isInputPassValidate(inputCode)) {
						mTextTip.setText(Html.fromHtml("<font color='#673AB7'>最少连接4个点, 请重新输入</font>"));
						mGestureContentView.clearDrawlineState(0L);
						return;
					}
					if (mIsFirstInput) {
						mFirstPassword = inputCode;
						updateCodeList(inputCode);
						mGestureContentView.clearDrawlineState(0L);
					/*mTextReset.setClickable(true);
					mTextReset.setText(getString(R.string.reset_gesture_code));*/
						mTextTip.setText(Html.fromHtml("<font color='#673AB7'>请确认手势密码</font>"));
						mIsFirstInput = false;
					} else {
						//ZLF 设置密码
						if (inputCode.equals(mFirstPassword)) {
							//在这里将密码保存到SharedPreferences
							SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
							editor.putString("password", mFirstPassword);
							editor.putBoolean("setPassword", true);
							editor.apply();

							Toast.makeText(GestureEditActivity.this, "设置成功", Toast.LENGTH_SHORT).show();
							mGestureContentView.clearDrawlineState(0L);


							GestureEditActivity.this.finish();
						} else {
							mTextTip.setText(Html.fromHtml("<font color='##673AB7'>与上一次绘制不一致，请重新绘制</font>"));
							//重新绘制时需要用户再次两次输入确认密码，刷新是否第一次输入的变量 ZLF
							mIsFirstInput=true;

							// 左右移动动画
							Animation shakeAnimation = AnimationUtils.loadAnimation(GestureEditActivity.this, R.anim.shake);
							mTextTip.startAnimation(shakeAnimation);
							// 保持绘制的线，1.5秒后清除
							mGestureContentView.clearDrawlineState(1300L);
						}
					}
				}


				//将这段代码放进if判断中 ZLF
				//mIsFirstInput = false;
			}

			@Override
			public void checkedSuccess() {

			}

			@Override
			public void checkedFail() {

			}
		});
		// 设置手势解锁显示到哪个布局里面
		mGestureContentView.setParentView(mGestureContainer);
		updateCodeList("");
	}

	private void setUpListeners() {
// 2 		mTextCancel.setOnClickListener(this);
		//mTextReset.setOnClickListener(this);
	}

	private void updateCodeList(String inputCode) {
		// 更新选择的图案
		//mLockIndicator.setPath(inputCode);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			/*case R.id.text_cancel:
				this.finish();
				break;*/
			/*case R.id.text_reset:
				mIsFirstInput = true;
				updateCodeList("");
				mTextTip.setText(getString(R.string.set_gesture_pattern));
				break;*/
			default:
				break;
		}
	}

	private boolean isInputPassValidate(String inputPassword) {
		if (TextUtils.isEmpty(inputPassword) || inputPassword.length() < 4) {
			return false;
		}
		return true;
	}

	public void onClickGestureEditBack(View v) {
		GestureEditActivity.this.finish();
	}

}