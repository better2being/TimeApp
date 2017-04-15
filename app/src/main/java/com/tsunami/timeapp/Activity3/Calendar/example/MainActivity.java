package com.tsunami.timeapp.Activity3.Calendar.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.tsunami.timeapp.Activity3.Alarm.AlarmActivity;
import com.tsunami.timeapp.R;
import  com.tsunami.timeapp.Activity3.Calendar.CollapseCalendarView;
import  com.tsunami.timeapp.Activity3.Calendar.manager.CalendarManager;

import org.joda.time.LocalDate;
/**
 * @author zhenglifeng
 */
public class MainActivity extends Activity {

    private CollapseCalendarView mCalendarView;
    private Button addAlarm=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activtiy_calendar);

        CalendarManager manager = new CalendarManager(LocalDate.now(), CalendarManager.State.MONTH, LocalDate.now(), LocalDate.now().plusYears(1));

        mCalendarView = (CollapseCalendarView) findViewById(R.id.calendar);
        mCalendarView.init(manager);


        addAlarm = (Button) findViewById(R.id.addAlarm);
        addAlarm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent intent = new Intent(MainActivity.this, AlarmActivity.class);
                startActivity(intent);
                return false;
            }
        });
    }
}
