package com.tsunami.timeapp.activity1;


import android.util.Log;
import com.alamkanak.weekview.WeekViewEvent;
import com.tsunami.timeapp.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shenxiaoming
 */
public class FriendWeekActivity extends com.tsunami.timeapp.activity2.weekview.FriendBaseAvtivity{

//    private static final int TYPE_DAY_VIEW = 1;
//    private static final int TYPE_THREE_DAY_VIEW = 2;
//    private static final int TYPE_WEEK_VIEW = 3;
//    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
//    private WeekView mWeekView;




//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_friendweek);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.friendweek_toolbar);
//        setSupportActionBar(toolbar);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            // 2 添加的代码
//            getSupportActionBar().setTitle("好友日程表");
//        }
//
//        if (getIntent().getStringExtra("op").equals("loadTable")) {
////            loadFriendSchedule();
//        } else if (getIntent().getStringExtra("op").equals("interview")) {
//            loadCommonSchedule();
//        }
//
//        mWeekView = (WeekView) findViewById(R.id.weekView);
//
//        mWeekView.setMonthChangeListener(this);
//
//        // Set up a date time interpreter to interpret how the date and time will be formatted in
//        // the week view. This is optional.
//        setupDateTimeInterpreter(false);
//    }

//    /**
//     * 加载好友日程
//     */
//    private void loadFriendSchedule() {
//        ProgressDialog dialog = new ProgressDialog(FriendWeekActivity.this);
//        dialog.setMessage("正在加载...");
//        Log.e("load", "friendSchedule");
//        // 开启异步任务
//        new AsyncTask<Void, Void, Void>() {
//
//            @Override
//            protected void onPreExecute() {
//                dialog.show();
//            }
//            @Override
//            protected Void doInBackground(Void... params) {
//                String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
//                RequestParams rp = new RequestParams(url);
//                rp.addBodyParameter("op", "loadTable");
//                rp.addBodyParameter("client", getIntent().getStringExtra("client"));
//                x.http().post(rp, new Callback.CommonCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        Log.e("on scuess", result);
//                        try {
//                            JSONArray jsonArray = new JSONArray(result);
//                            for (int i = 0; i < jsonArray.length(); i++) {
//                                JSONArray array = jsonArray.getJSONArray(i);
//                                Log.e("onMonthChange",array.toString());
//                                for (int k = 0; k < array.length(); k++) {
//                                    jsonList.add(array.getJSONObject(k));
//                                    Log.e("onMonthChange",array.getJSONObject(k).toString());
//                                }
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    @Override
//                    public void onError(Throwable ex, boolean isOnCallback) {
//                        Toast.makeText(FriendWeekActivity.this, "加载异常", Toast.LENGTH_SHORT).show();
//                    }
//                    @Override
//                    public void onCancelled(CancelledException cex) {
//                    }
//                    @Override
//                    public void onFinished() {
//                       dialog.dismiss();
//                    }
//                });
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                onMonthChange(2016,8);
//            }
//        }.execute();
//    }

    /*--------------------------------------------------------------------------------------*/
    @Override
    public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
        // Populate the week view with some events.
        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();


        Calendar endTime;
        int counter = 1;
        if (getIntent().getStringExtra("op").equals("loadTable")) {

            List<JSONObject> jsonList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("result"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray array = jsonArray.getJSONArray(i);
                    Log.e("loadTabe return",array.toString());
                    for (int k = 0; k < array.length(); k++) {
                        jsonList.add(array.getJSONObject(k));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (JSONObject json : jsonList) {
                    String date = json.getString("date");
                    String workname = json.getString("workname");
                    String worktext = json.getString("worktext");
                    int starttime = json.getInt("starttime");
                    int endtime = json.getInt("endtime");
//                Log.e("json",date);

                    //--------------------------------------------------------------------
                    String year = date.substring(0,4);
                    String month = date.substring(4,6);
                    String day = date.substring(6,8);
                    int startHour = starttime / 60;
                    int startMinute = starttime % 60;
                    int endHour = endtime / 60;
                    int endMinute = endtime % 60;
//                Log.e("json_day",day);

                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, startHour);
                    startTime.set(Calendar.MINUTE, startMinute);
                    startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                    startTime.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                    startTime.set(Calendar.YEAR, Integer.parseInt(year));

                    endTime = (Calendar) startTime.clone();
                    endTime.set(Calendar.HOUR_OF_DAY, endHour);
                    endTime.set(Calendar.MINUTE, endMinute);

                    WeekViewEvent event = new WeekViewEvent(counter,workname + '\n' + worktext, startTime, endTime);

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

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (getIntent().getStringExtra("op").equals("interview")) {

            Map<String, JSONArray> jsonMap = new HashMap<>();
            long time = System.currentTimeMillis();
            Date date = new Date(time);

            Calendar calendar = Calendar.getInstance();
            String days[] = new String[7];
            for (int i = 0; i < 7; i++) {
                calendar.setTime(date);
                int d = calendar.get(Calendar.DATE);
                calendar.set(Calendar.DATE, d + i);
                String day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                day = day.substring(0, 4) + day.substring(5, 7) + day.substring(8, 10);
                days[i] = day;
            }

            try {
                JSONArray jsonArray = new JSONArray(getIntent().getStringExtra("result"));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONArray array = jsonArray.getJSONArray(i);
                    Log.e("interview return",array.toString());
                    jsonMap.put(days[i], array);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                for (int i = 0; i < jsonMap.size(); i++) {
                    JSONArray array = jsonMap.get(days[i]);
                    Log.e("Map i", array.toString());
                    for (int k = 0; k < array.length(); k++) {
                        JSONObject json = array.getJSONObject(k);
                        String date9 = days[i];
                        String begain = json.getString("begin");
                        String end = json.getString("end");



                        //--------------------------------------------------------------------
                        String year = date9.substring(0,4);
                        String month = date9.substring(4,6);
                        String day = date9.substring(6,8);
                        int startHour = Integer.parseInt(begain) / 60;
                        int startMinute = Integer.parseInt(begain) % 60;
                        int endHour = Integer.parseInt(end) / 60;
                        int endMinute = Integer.parseInt(end) % 60;
//                Log.e("json_day",day);

                        Calendar startTime = Calendar.getInstance();
                        startTime.set(Calendar.HOUR_OF_DAY, startHour);
                        startTime.set(Calendar.MINUTE, startMinute);
                        startTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
                        startTime.set(Calendar.MONTH, Integer.parseInt(month) - 1);
                        startTime.set(Calendar.YEAR, Integer.parseInt(year));

                        endTime = (Calendar) startTime.clone();
                        endTime.set(Calendar.HOUR_OF_DAY, endHour);
                        endTime.set(Calendar.MINUTE, endMinute);

                        WeekViewEvent event = new WeekViewEvent(counter," ", startTime, endTime);

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
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        return events;
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.friendweek_menu, menu);
//        menu.findItem(R.id.action_addSchedule).setVisible(false);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        setupDateTimeInterpreter(id == R.id.action_week_view);
//        switch (id){
//            case R.id.action_today:
//                mWeekView.goToToday();
//                return true;
//            case R.id.action_addSchedule:
////                startActivity(new Intent(BaseActivity.this, SamplerMy.class));
//                return true;
//            case R.id.action_day_view:
//                if (mWeekViewType != TYPE_DAY_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_DAY_VIEW;
//                    mWeekView.setNumberOfVisibleDays(1);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                }
//                return true;
//            case R.id.action_three_day_view:
//                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_THREE_DAY_VIEW;
//                    mWeekView.setNumberOfVisibleDays(3);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
//                }
//                return true;
//            case R.id.action_week_view:
//                if (mWeekViewType != TYPE_WEEK_VIEW) {
//                    item.setChecked(!item.isChecked());
//                    mWeekViewType = TYPE_WEEK_VIEW;
//                    mWeekView.setNumberOfVisibleDays(7);
//
//                    // Lets change some dimensions to best fit the view.
//                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
//                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
//                }
//                return true;
//            case android.R.id.home:
//                FriendWeekActivity.this.finish();
//                return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }
//
//    /**
//     * Set up a date time interpreter which will show short date values when in week view and long
//     * date values otherwise.
//     * @param shortDate True if the date values should be short.
//     */
//    private void setupDateTimeInterpreter(final boolean shortDate) {
//        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
//            @Override
//            public String interpretDate(Calendar date) {
//                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
//                String weekday = weekdayNameFormat.format(date.getTime());
//                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());
//
//                // All android api level do not have a standard way of getting the first letter of
//                // the week day name. Hence we get the first char programmatically.
//                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
//                if (shortDate)
//                    weekday = String.valueOf(weekday.charAt(0));
//                return weekday.toUpperCase() + format.format(date.getTime());
//            }
//
//            @Override
//            public String interpretTime(int hour) {
//                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
//            }
//        });
//    }
//
//    public WeekView getWeekView() {
//        return mWeekView;
//    }



}
