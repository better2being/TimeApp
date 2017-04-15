
package com.tsunami.timeapp.Activity3.Alarm.alert;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import com.tsunami.timeapp.Activity3.Alarm.Alarm;
import com.tsunami.timeapp.R;
/**
 * @author zhenglifeng
 */
public class AlarmAlertActivity extends Activity implements OnClickListener {

//	private Alarm alarm;
//	private MediaPlayer mediaPlayer;
//
//	private StringBuilder answerBuilder = new StringBuilder();
//
//	private MathProblem mathProblem;
//
//	// 2 振动器
//	private Vibrator vibrator;
//
//	private boolean alarmActive;
//
//	// 2 闹钟响应问题界面
//	private TextView problemView;
//	private TextView answerView;
//
//	//2 闹钟响应问题的答案
//	private String answerString;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		final Window window = getWindow();
//		// 2 设置闹钟在锁屏下显示，忽略安全密码，屏幕亮屏
//		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
//				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
//		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
//				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//
//		// 2 闹钟响应表格界面
//		setContentView(R.layout.alarm_alert);
//
//		Bundle bundle = this.getIntent().getExtras();
//		alarm = (Alarm) bundle.getSerializable("alarm");
//
//		this.setTitle(alarm.getAlarmName());
//
//
//		switch (alarm.getDifficulty()) {
//			case EASY:
//				mathProblem = new MathProblem(3);
//				break;
//			case MEDIUM:
//				mathProblem = new MathProblem(4);
//				break;
//			case HARD:
//				mathProblem = new MathProblem(5);
//				break;
//		}
//
//		answerString = String.valueOf(mathProblem.getAnswer());
//		if (answerString.endsWith(".0")) {
//			answerString = answerString.substring(0, answerString.length() - 2);
//		}
//
//
//		problemView = (TextView) findViewById(R.id.textView1);
//		problemView.setText(mathProblem.toString());
//
//		answerView = (TextView) findViewById(R.id.textView2);
//		answerView.setText("= ?");
//
//		// 2 闹钟响应问题界面的所有按钮 1 2 3 4 5 6 7 8 9 0 。。。
//		((Button) findViewById(R.id.Button0)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button1)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button2)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button3)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button4)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button5)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button6)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button7)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button8)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button9)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button_clear)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button_decimal)).setOnClickListener(this);
//		((Button) findViewById(R.id.Button_minus)).setOnClickListener(this);
//
//		// 2 获取电话服务管理器
//		TelephonyManager telephonyManager = (TelephonyManager) this
//				.getSystemService(Context.TELEPHONY_SERVICE);
//
//		// 2 注册我们要监听的电话状态改变事件
//		PhoneStateListener phoneStateListener = new PhoneStateListener() {
//			@Override
//			public void onCallStateChanged(int state, String incomingNumber) {
//				switch (state) {
//					// 2 电话正在接通
//					case TelephonyManager.CALL_STATE_RINGING:
//						Log.d(getClass().getSimpleName(), "Incoming call: "
//								+ incomingNumber);
//						try {
//							mediaPlayer.pause(); // 2 停止闹钟铃声
//						} catch (IllegalStateException e) {
//
//						}
//						break;
//					// 2 电话空闲状态
//					case TelephonyManager.CALL_STATE_IDLE:
//						Log.d(getClass().getSimpleName(), "Call State Idle");
//						try {
//							// 2 开始闹钟铃声
//							mediaPlayer.start();
//						} catch (IllegalStateException e) {
//
//						}
//						break;
//				}
//				super.onCallStateChanged(state, incomingNumber);
//			}
//		};
//
//		telephonyManager.listen(phoneStateListener,
//				PhoneStateListener.LISTEN_CALL_STATE);
//
//		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();
//
//		startAlarm();
//
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		alarmActive = true;
//	}
//
//	private void startAlarm() {
//
//		if (alarm.getAlarmTonePath() != "") {
//			mediaPlayer = new MediaPlayer();
//			if (alarm.getVibrate()) {
//				// 2 手机振动
//				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//				long[] pattern = { 1000, 200, 200, 200 };
//				vibrator.vibrate(pattern, 0);
//			}
//			try {
//				// 2 手机铃声
//				mediaPlayer.setVolume(1.0f, 1.0f);
//				mediaPlayer.setDataSource(this,
//						Uri.parse(alarm.getAlarmTonePath()));
//				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
//				mediaPlayer.setLooping(true);
//				mediaPlayer.prepare();
//				mediaPlayer.start();
//
//			} catch (Exception e) {
//				mediaPlayer.release();
//				alarmActive = false;
//			}
//		}
//
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.app.Activity#onBackPressed()
//	 */
//	@Override
//	public void onBackPressed() {
//		if (!alarmActive)
//			super.onBackPressed();
//	}
//
//	/*
//	 * (non-Javadoc)
//	 *
//	 * @see android.app.Activity#onPause()
//	 */
//	@Override
//	protected void onPause() {
//		super.onPause();
//		StaticWakeLock.lockOff(this);
//	}
//
//	@Override
//	protected void onDestroy() {
//		try {
//			if (vibrator != null)
//				vibrator.cancel();
//		} catch (Exception e) {
//
//		}
//		try {
//			mediaPlayer.stop();
//		} catch (Exception e) {
//
//		}
//		try {
//			mediaPlayer.release();
//		} catch (Exception e) {
//
//		}
//		super.onDestroy();
//	}
//
//	@Override
//	public void onClick(View v) {
//		if (!alarmActive)
//			return;
//		String button = (String) v.getTag();
//		v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
//		if (button.equalsIgnoreCase("clear")) {
//			if (answerBuilder.length() > 0) {
//				answerBuilder.setLength(answerBuilder.length() - 1);
//				answerView.setText(answerBuilder.toString());
//			}
//		} else if (button.equalsIgnoreCase(".")) {
//			if (!answerBuilder.toString().contains(button)) {
//				if (answerBuilder.length() == 0)
//					answerBuilder.append(0);
//				answerBuilder.append(button);
//				answerView.setText(answerBuilder.toString());
//			}
//		} else if (button.equalsIgnoreCase("-")) {
//			if (answerBuilder.length() == 0) {
//				answerBuilder.append(button);
//				answerView.setText(answerBuilder.toString());
//			}
//		} else {
//			answerBuilder.append(button);
//			answerView.setText(answerBuilder.toString());
//			if (isAnswerCorrect()) {
//				alarmActive = false;
//				if (vibrator != null)
//					vibrator.cancel();
//				try {
//					mediaPlayer.stop();
//				} catch (IllegalStateException ise) {
//
//				}
//				try {
//					mediaPlayer.release();
//				} catch (Exception e) {
//
//				}
//				this.finish();
//			}
//		}
//		if (answerView.getText().length() >= answerString.length()
//				&& !isAnswerCorrect()) {
//			answerView.setTextColor(Color.RED);
//		} else {
//			answerView.setTextColor(Color.BLACK);
//		}
//	}
//
//	public boolean isAnswerCorrect() {
//		boolean correct = false;
//		try {
//			correct = mathProblem.getAnswer() == Float.parseFloat(answerBuilder
//					.toString());
//		} catch (NumberFormatException e) {
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return correct;
//	}

	private Alarm alarm;
	private MediaPlayer mediaPlayer;

	// 2 振动器
	private Vibrator vibrator;

	private boolean alarmActive;

	private Button btn_lock_screen_alert;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final Window window = getWindow();
		// 2 设置闹钟在锁屏下显示，忽略安全密码，屏幕亮屏
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
				| WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
				| WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		// 2 闹钟响应表格界面
		setContentView(R.layout.alarm_lock_screen_alert);

		btn_lock_screen_alert = (Button) findViewById(R.id.btn_lock_screen_alert);

		btn_lock_screen_alert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alarmActive = false;
				if (vibrator != null)
				vibrator.cancel();
			try {
					mediaPlayer.stop();
				} catch (IllegalStateException ise) {

				}
				try {
					mediaPlayer.release();
				} catch (Exception e) {

				}
				AlarmAlertActivity.this.finish();
			}
		});

		Bundle bundle = this.getIntent().getExtras();
		alarm = (Alarm) bundle.getSerializable("alarm");

		this.setTitle(alarm.getAlarmName());


		// 2 获取电话服务管理器
		TelephonyManager telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);

		// 2 注册我们要监听的电话状态改变事件
		PhoneStateListener phoneStateListener = new PhoneStateListener() {
			@Override
			public void onCallStateChanged(int state, String incomingNumber) {
				switch (state) {
					// 2 电话正在接通
					case TelephonyManager.CALL_STATE_RINGING:
						Log.d(getClass().getSimpleName(), "Incoming call: "
								+ incomingNumber);
						try {
							mediaPlayer.pause(); // 2 停止闹钟铃声
						} catch (IllegalStateException e) {

						}
						break;
					// 2 电话空闲状态
					case TelephonyManager.CALL_STATE_IDLE:
						Log.d(getClass().getSimpleName(), "Call State Idle");
						try {
							// 2 开始闹钟铃声
							mediaPlayer.start();
						} catch (IllegalStateException e) {

						}
						break;
				}
				super.onCallStateChanged(state, incomingNumber);
			}
		};

		telephonyManager.listen(phoneStateListener,
				PhoneStateListener.LISTEN_CALL_STATE);

		// Toast.makeText(this, answerString, Toast.LENGTH_LONG).show();

		startAlarm();

	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmActive = true;
	}

	private void startAlarm() {

		if (alarm.getAlarmTonePath() != "") {
			mediaPlayer = new MediaPlayer();
			if (alarm.getVibrate()) {
				// 2 手机振动
				vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
				long[] pattern = { 1000, 200, 200, 200 };
				vibrator.vibrate(pattern, 0);
			}
			try {
				// 2 手机铃声
				mediaPlayer.setVolume(1.0f, 1.0f);
				mediaPlayer.setDataSource(this,
						Uri.parse(alarm.getAlarmTonePath()));
				mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mediaPlayer.setLooping(true);
				mediaPlayer.prepare();
				mediaPlayer.start();

			} catch (Exception e) {
				mediaPlayer.release();
				alarmActive = false;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (!alarmActive)
			super.onBackPressed();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
		StaticWakeLock.lockOff(this);
	}

	@Override
	protected void onDestroy() {
		try {
			if (vibrator != null)
				vibrator.cancel();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.stop();
		} catch (Exception e) {

		}
		try {
			mediaPlayer.release();
		} catch (Exception e) {

		}
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {



	}

//	public boolean isAnswerCorrect() {
//		boolean correct = false;
//		try {
//			correct = mathProblem.getAnswer() == Float.parseFloat(answerBuilder
//					.toString());
//		} catch (NumberFormatException e) {
//			return false;
//		} catch (Exception e) {
//			e.printStackTrace();
//			return false;
//		}
//		return correct;
//	}

}
