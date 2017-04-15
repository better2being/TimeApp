
package com.tsunami.timeapp.Activity3.ScanErWeiMa.myzxingtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


import com.tsunami.timeapp.Activity3.ScanErWeiMa.zxing.DecodeThread;
import com.tsunami.timeapp.Activity3.ScanErWeiMa.zxing.camera.CameraManager;
import com.tsunami.timeapp.Activity3.ScanErWeiMa.zxing.view.ViewfinderResultPointCallback;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.tsunami.timeapp.R;

import java.util.Collection;
/**
 * @author zhenglifeng
 */
public final class CaptureActivityHandler extends Handler {
	private static final String TAG = CaptureActivityHandler.class.getSimpleName();
	private final ScanErWeiMaActivity activity;
	private final DecodeThread decodeThread;// 解码线程
	private State state;
	private final CameraManager cameraManager;// 相机管理者

	// 枚举当前的状态类型
	private enum State {
		PREVIEW, // 预览
		SUCCESS, // 成功
		DONE// 完成
	}

	// 实例化时所需参数，activity，
	CaptureActivityHandler(ScanErWeiMaActivity activity, Collection<BarcodeFormat> decodeFormats, String characterSet, CameraManager cameraManager) {
		this.activity = activity;
		decodeThread = new DecodeThread(activity, decodeFormats, characterSet, new ViewfinderResultPointCallback(activity.getViewfinderView()));
		decodeThread.start();
		state = State.SUCCESS;
		this.cameraManager = cameraManager;
		cameraManager.startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {
			case R.id.restart_preview:// 重新开始
				Log.d(TAG, "Got restart preview message");
				restartPreviewAndDecode();
				break;
			case R.id.decode_succeeded:// 成功
				Log.d(TAG, "Got decode succeeded message");
				state = State.SUCCESS;
				Bundle bundle = message.getData();
				Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
				activity.handleDecode((Result) message.obj, barcode);
				break;
			case R.id.decode_failed:// 失败
				state = State.PREVIEW;
				cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
				break;
			case R.id.return_scan_result:
				Log.d(TAG, "Got return scan result message");
				activity.setResult(Activity.RESULT_OK, (Intent) message.obj);
				activity.finish();
				break;
		}
	}

	// 退出同步
	public void quitSynchronously() {
		state = State.DONE;
		cameraManager.stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try {
			decodeThread.join(500L);
		} catch (InterruptedException e) {
		}
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			activity.drawViewfinder();
		}
	}
}
