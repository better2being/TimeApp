package com.tsunami.timeapp.activity2.weekview;

import android.content.Intent;

import com.alamkanak.weekview.WeekViewEvent;
import com.tsunami.timeapp.JsonService;
import com.tsunami.timeapp.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author wangshujie
 */
public class BasicActivity extends BaseActivity {

    private static JsonService jsonService = new JsonService();

    Intent intent = getIntent();


//    static {
//        System.out.println(jsonService.dayTable("20160827", "testtable"));
//    }

    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        //System.out.println(jsonService.list.get("20160827").work.get(0).workname);
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();
        if (true) {
            List<JsonService.work> dW = jsonService.list.get("20160827").work;
            for (int i = 0; i < dW.size(); i++) {
                System.out.println(dW.get(i).workname);
                WeekViewEvent event = addEvent(i + 1, dW.get(i).workname+'\n'+dW.get(i).worktime, dW.get(i).starttime, dW.get(i).endtime);
                events.add(event);
            }
            jsonService.flag = false;
        }

        return events;
    }
    public Calendar setTime(int time) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DAY_OF_MONTH, 3);
        cal.set(Calendar.YEAR, 2016);
        cal.set(Calendar.HOUR_OF_DAY, time / 60);
        cal.set(Calendar.MINUTE, time % 60);
        return cal;
    }

    public WeekViewEvent addEvent(int id, String workname, int starttime, int endtime) {
        Calendar start = setTime(starttime), end = setTime(endtime);
        //Random random = new Random();
        //int x = random.nextInt(40)+40;
        //end.add(Calendar.MINUTE, x);
        WeekViewEvent event = new WeekViewEvent(id, workname, start, end);
        int setColor=0;
        switch(color){
            case 1:
                setColor=R.color.event_color_01;
                break;
            case 2:
                setColor=R.color.event_color_02;
                break;
            case 3:
                setColor=R.color.event_color_03;
                break;
            case 4:
                setColor=R.color.event_color_04;
                break;
        }
        color++;
        if(color>4)color=1;
        event.setColor(getResources().getColor(setColor));
        return event;
    }

    private int color=1;

}
