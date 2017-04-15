
package com.tsunami.timeapp.Activity3.Alarm.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
/**
 * @author zhenglifeng
 */
public class AlarmServiceBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//Log.d("AlarmServiceBroadcastReciever", "onReceive()");
		Intent serviceIntent = new Intent(context, AlarmService.class);
		context.startService(serviceIntent);
	}

}
