
package com.tsunami.timeapp.Activity3.Alarm.telephony;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * @author zhenglifeng
 */
public class PhoneStateChangedBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(getClass().getSimpleName(), "onReceive()");
		
	}

}
