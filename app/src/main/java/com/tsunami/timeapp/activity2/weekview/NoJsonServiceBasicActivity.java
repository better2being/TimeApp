package com.tsunami.timeapp.activity2.weekview;


import android.content.Intent;
import android.util.Log;

import com.alamkanak.weekview.WeekViewEvent;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Schedule;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author wangshujie
 */
public class NoJsonServiceBasicActivity extends BaseActivity {



    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();


        int startYear,startMonth,startDayOfMonth,startHour,startMinute;
        int endYear,endMonth,endDayOfMonth,endHour,endMinute;
        int noteID;
        String noteLabel,noteContent,noteTypedTime;

        Intent intent = getIntent();
        startYear = intent.getIntExtra("startYear", -1);
        startMonth = intent.getIntExtra("startMonth", 9);
        startDayOfMonth = intent.getIntExtra("startDayOfMonth", 5);
        startHour = intent.getIntExtra("startHour", 6);
        startMinute = intent.getIntExtra("startMinute", 6);



        endDayOfMonth = startDayOfMonth;
        endHour = intent.getIntExtra("endHour", 8);
        endMinute = intent.getIntExtra("endMinute", 5);

        noteID = intent.getIntExtra("noteID",0);
        noteLabel = intent.getStringExtra("noteLabel");
        noteContent = intent.getStringExtra("noteContent");
        noteTypedTime = intent.getStringExtra("noteTypedTime");
        String samplerNoteLabelAndContent = intent.getStringExtra("samplerNoteLabelAndContent");


//        // 2 添加的代码
//        if(startYear != -1) {
//            Calendar startTime = Calendar.getInstance();
//            startTime.set(Calendar.HOUR_OF_DAY, startHour);
//            startTime.set(Calendar.MINUTE, startMinute);
//            startTime.set(Calendar.DAY_OF_MONTH, startDayOfMonth);
//            startTime.set(Calendar.MONTH, newMonth - 1);
//            startTime.set(Calendar.YEAR, newYear);
//            Calendar endTime = (Calendar) startTime.clone();
//            endTime.set(Calendar.HOUR_OF_DAY, endHour);
//            endTime.set(Calendar.MINUTE, endMinute);
//            endTime.set(Calendar.DAY_OF_MONTH, endDayOfMonth);
//            endTime.set(Calendar.MONTH, newMonth - 1);
//
//            WeekViewEvent event = new WeekViewEvent(2,samplerNoteLabelAndContent, startTime, endTime);
//
//            event.setColor(getResources().getColor(R.color.event_color_01));
//
//            events.add(event);
//        }


        List<Schedule> schedules = UserDB.getInstance(NoJsonServiceBasicActivity.this).getSchedules();

        Calendar endTime;
        int counter = 1;
        for (Schedule s : schedules) {
            Log.e("weekview1", s.toString());
            String year = s.getDate().substring(0,4);
            String month = s.getDate().substring(4,6);
            String day = s.getDate().substring(6,8);
            startHour = s.getStarttime() / 60;
            startMinute = s.getStarttime() % 60;
            endHour = s.getEndtime() / 60;
            endMinute = s.getEndtime() % 60;

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, startHour);
            startTime.set(Calendar.MINUTE, startMinute);
            startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
            startTime.set(Calendar.MONTH, Integer.parseInt(month) - 1);
            startTime.set(Calendar.YEAR, Integer.parseInt(year));


            endTime = (Calendar) startTime.clone();
            endTime.set(Calendar.HOUR_OF_DAY, endHour);
            endTime.set(Calendar.MINUTE, endMinute);

            WeekViewEvent event = new WeekViewEvent(counter,s.getWorkname() + '\n' + s.getWorktext(), startTime, endTime);

            switch (counter % 4) {
                case 0:
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    break;
                case 1:
                    event.setColor(getResources().getColor(R.color.event_color_02));
                    break;
                case 2:
                    event.setColor(getResources().getColor(R.color.event_color_03));
                    break;
                case 3:
                    event.setColor(getResources().getColor(R.color.event_color_04));
                    break;

            }

            events.add(event);
            counter++;
        }


        return events;
    }

}