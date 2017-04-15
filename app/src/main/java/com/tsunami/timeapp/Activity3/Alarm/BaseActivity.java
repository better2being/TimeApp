
package com.tsunami.timeapp.Activity3.Alarm;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.Toast;

import java.lang.reflect.Field;

import com.tsunami.timeapp.Activity3.Alarm.preferences.AlarmPreferencesActivity;
import com.tsunami.timeapp.Activity3.Alarm.service.AlarmServiceBroadcastReciever;
import com.tsunami.timeapp.R;
/**
 * @author zhenglifeng
 */
public abstract class BaseActivity  extends AppCompatActivity implements android.view.View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
	        ViewConfiguration config = ViewConfiguration.get(this);	        
	        Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
	        if(menuKeyField != null) {
	            menuKeyField.setAccessible(true);
	            menuKeyField.setBoolean(config, false);
	        }
	    } catch (Exception ex) {
	        // Ignore
	    }
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {		
		// Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu, menu);
	    return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String url = null;
		Intent intent = null;
		switch (item.getItemId()) {

		case R.id.menu_item_new:
			Intent newAlarmIntent = new Intent(this, AlarmPreferencesActivity.class);
			startActivity(newAlarmIntent);
			break;

		// 2 在应用商店为该应用评分
		case R.id.menu_item_rate:
			url = "market://details?id=" + "WPS";
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the market", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_item_website:
			url = "http://www.neilson.co.za";
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the website", Toast.LENGTH_LONG).show();
			}
			break;
		case R.id.menu_item_report:
			
			url = "https://github.com/SheldonNeilson/Android-Alarm-Clock/issues";
			intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse(url));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException e) {
				Toast.makeText(this, "Couldn't launch the bug reporting website", Toast.LENGTH_LONG).show();
			}
			

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	protected void callMathAlarmScheduleService() {
		Intent mathAlarmServiceIntent = new Intent(this, AlarmServiceBroadcastReciever.class);
		sendBroadcast(mathAlarmServiceIntent, null);
	}
}
