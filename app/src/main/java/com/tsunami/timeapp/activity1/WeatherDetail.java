package com.tsunami.timeapp.activity1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.adapter1.LifeViewAdapter;
import com.tsunami.timeapp.adapter1.TodayViewAdapter;
import com.tsunami.timeapp.adapter1.WeekViewAdapter;
import com.tsunami.timeapp.bean1.LifeBean;
import com.tsunami.timeapp.bean1.TodayBean;
import com.tsunami.timeapp.bean1.WeekBean;
import com.tsunami.timeapp.util.WeatherDataUtil;
import com.tsunami.timeapp.util.WeatherIconUtil;
import com.tsunami.timeapp.view1.MyListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ArrayList;

/**
 * @author shenxiaoming
 */
public class WeatherDetail extends AppCompatActivity {

    private static final String TAG = "WeatherDetail";

    private MyListView weekLv;
    private List<WeekBean> weekBeanList = new ArrayList<>();

    private MyListView lifeLv;
    private List<LifeBean> lifeBeanList = new ArrayList<>();
    private LifeViewAdapter lifeViewAdapter;
    private int currentPosition = -1;

    private JSONObject cmf;
    private JSONObject drsg;
    private JSONObject flu;
    private JSONObject sport;
    private JSONObject trav;
    private JSONObject uv;
    private JSONObject cw;

    private LinearLayout today_layout;
    private MyListView todayLv;
    private List<TodayBean> todayViewList = new ArrayList<>();
    private TextView today_sunrise;
    private TextView today_sunset;

    // 下拉刷新header布局控件
    private ScrollView scrollView;
    private RelativeLayout header_layout;
    private ImageView arrow_iv;
    private ProgressBar headProgress;
    private TextView lastUpdate_tv;
    private TextView tips_tv;

    private RotateAnimation tipsAnimation;
    private RotateAnimation reverseAnimation;

    public static final int STATE_IDLE = 0;
    public static final int STATE_RELEASE_TO_REFRESH = 1;
    public static final int STATE_PULL_TO_REFRESH = 2;
    public static final int STATE_REFRESHING = 3;
    public static final int STATE_LOADING = 4;
    protected int mCurrentState = STATE_IDLE;
    // 从release转到pull   箭头是否向上
    private boolean isArrowUp = false;

    // header高度
    private int headerHeight;
    // 最后一次下拉移动header的padding
    private int lastHeaderPadding;

    // 下拉点坐标
    protected int mLastY;
    // 滑动的y轴距离
    private int deltaY;

    // 下拉加载footer
    private RelativeLayout weather_footer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (TextUtils.isEmpty(FileStreamUtil.load(WeatherDetail.this))) {
//            onRefresh();
//        }

        setContentView(R.layout.weather_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.weather_detail_toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 2 添加的代码
            getSupportActionBar().setTitle("天气详情");
            /////////////////////////////////
        }

        // Finish on navigation icon click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // 初始化Views
        initView(this);

        // 加载未来一周天气
        loadWeek();
        setListViewHeightBasedOnChildren(weekLv);
        // 加载生活指数
        loadLife();
        setListViewHeightBasedOnChildren(lifeLv);
        // 加载今日详情
        loadToday();
        setListViewHeightBasedOnChildren(todayLv);
    }

    /**
     * 初始化views
     * @param context
     */
    private void initView(Context context) {
        weekLv = (MyListView) findViewById(R.id.week_list_view);
        lifeLv = (MyListView) findViewById(R.id.life_list_view);
        today_layout = (LinearLayout) findViewById(R.id.today_layout);
        todayLv = (MyListView) findViewById(R.id.today_detail);
        today_sunrise = (TextView) findViewById(R.id.today_sunrise);
        today_sunset = (TextView) findViewById(R.id.today_sunset);

        // 整个页面的总layout
        LinearLayout global_layout = (LinearLayout) findViewById(R.id.global_layout);
//        inflater = LayoutInflater.from(context);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        header_layout = (RelativeLayout) inflater.inflate(R.layout.head_layout, null);
        measureView(header_layout);
        headerHeight = header_layout.getMeasuredHeight();

        weather_footer = (RelativeLayout) findViewById(R.id.weather_footer);

        // 最后一次下拉移动header的padding
        lastHeaderPadding = (-1 * headerHeight);
        header_layout.setPadding(0, lastHeaderPadding, 0, 0);
        header_layout.invalidate();
        global_layout.addView(header_layout, 1);

        headProgress = (ProgressBar) findViewById(R.id.head_progressbar);
        arrow_iv = (ImageView) findViewById(R.id.head_arrow_iv);
        arrow_iv.setMinimumWidth(50);
        arrow_iv.setMinimumHeight(50);
        tips_tv = (TextView) findViewById(R.id.head_tips_tv);
        lastUpdate_tv = (TextView) findViewById(R.id.head_lastupdated_tv);

        // 箭头转动动画
        tipsAnimation = new RotateAnimation(0, -180,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        tipsAnimation.setInterpolator(new LinearInterpolator());
        tipsAnimation.setDuration(200);
        tipsAnimation.setFillAfter(true);// 动画结束后保持动画
        // 箭头反转动画
        reverseAnimation = new RotateAnimation(-180, 0,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f,
                RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        tipsAnimation.setInterpolator(new LinearInterpolator());
        tipsAnimation.setDuration(200);
        tipsAnimation.setFillAfter(true);// 动画结束后保持动画

        lastUpdate_tv.setText("最近更新：" + WeatherDataUtil.getUpdateTime());

        scrollView = (ScrollView) findViewById(R.id.weather_scrollview);
        // 为scrollview绑定事件
        scrollView.setOnTouchListener(listener);


    }

    // scrollView的滑动监听
    View.OnTouchListener listener =  new View.OnTouchListener() {
        // 标记动作：从顶部向下拉后又向上滑动
        boolean flag_scrollDownUp = false;
        // 标记动作：先上滑后再往下拉
        boolean flag_scrollUpDown = false;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            // 如果点击事件落在listview内
            int position = -1;
            // 从顶端下拉才响应
            if (scrollView.getScrollY() == 0
                    && !flag_scrollUpDown) {
                if (todayLv.getIsDown()) {
                    mLastY = (int)todayLv.getLastY();
                    position = 0;
                } else if (weekLv.getIsDown()) {
                    mLastY = (int)weekLv.getLastY();
                    position = 1;
                } else if (lifeLv.getIsDown()) {
                    mLastY = (int)lifeLv.getLastY();
                    position = 2;
                }
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // ACTION_DOWN：当按下位置在非listview区域才响应
                    mLastY = (int)event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    // 滑动的y轴距离
                    deltaY = (int)(event.getRawY() - mLastY);

                    // 判断滑动方向
                    if (deltaY > 0) {
                        // scrollview下拉的距离
                        if (scrollView.getScrollY() == 0){
                            if( lastHeaderPadding >= (-1 * headerHeight)
                                    && mCurrentState != STATE_REFRESHING) {
                                // 从顶端下拉，标记
                                flag_scrollDownUp = true;
                                // 设置滑动阻力
                                deltaY /= 2;

                                lastHeaderPadding = deltaY + (-1 * headerHeight);
                                header_layout.setPadding(0, lastHeaderPadding, 0, 0);
                                if (lastHeaderPadding > 0) {
                                    mCurrentState = STATE_RELEASE_TO_REFRESH;
                                    changeHeaderViewByState();
                                } else {
                                    mCurrentState = STATE_PULL_TO_REFRESH;
                                    changeHeaderViewByState();
                                }
                            }
                        } else {    // 非从顶端下拉，用当前移动到的坐标修改 mLastY值
                            mLastY = (int)event.getRawY();
//                            Log.e("mLastY", Integer.toString(mLastY));
                            // 标记
                            flag_scrollUpDown = true;
                        }
                    } else {
                        // 从顶部向下拉后又向上滑动
                        if (flag_scrollDownUp) {
                            lastHeaderPadding = deltaY + (-1 * headerHeight);
                            if (scrollView.getScrollY() > 0) {
                                header_layout.setPadding(0, lastHeaderPadding, 0, 0);
                            } else {
                                header_layout.setPadding(0, -1 * headerHeight, 0, 0);
                            }
                        }

                        // 判断是否已滑动到底部
                        View childView = scrollView.getChildAt(0);
                        if (childView.getHeight() <= scrollView.getScrollY() + scrollView.getHeight() ){
                            mCurrentState = STATE_LOADING;
                            changeHeaderViewByState();
//                            Log.e(TAG, "bottom");
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    // 从顶端下拉标记清除
                    flag_scrollDownUp = false;
                    // 先上滑再下拉标记清除
                    flag_scrollUpDown = false;

                    // listview内的点击事件清除
                    switch (position) {
                        case 0:
                            todayLv.setIsDown(false);
                            break;
                        case 1:
                            weekLv.setIsDown(false);
                            break;
                        case 2:
                            lifeLv.setIsDown(false);
                            break;
                        default:
                            break;
                    }
                    if (mCurrentState != STATE_REFRESHING) {
                        switch (mCurrentState) {
                            case STATE_IDLE:
                                break;
                            case STATE_PULL_TO_REFRESH:
                                mCurrentState = STATE_IDLE;
                                lastHeaderPadding = -1 * headerHeight;
                                header_layout.setPadding(0, lastHeaderPadding, 0, 0);
                                changeHeaderViewByState();
                                break;
                            case STATE_RELEASE_TO_REFRESH:
                                mCurrentState = STATE_REFRESHING;
                                changeHeaderViewByState();
                                onRefresh();
                                break;
                            case STATE_LOADING:
                                mCurrentState = STATE_IDLE;
                                changeHeaderViewByState();
                                break;
                        }
                    }
                    break;
            }

            // 如果Header是被完全隐藏的则让scrollview正常滑动让事件继续，否则的话就阻断事件
            if (lastHeaderPadding > (-1 * headerHeight)
                    && mCurrentState != STATE_REFRESHING) {
                return true;
            } else {
                return false;
            }
        }
    };

    /**
     * 异步刷新
     */
    public void onRefresh() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(2000);
                    WeatherDataUtil.connectWeather();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                headProgress.setVisibility(View.GONE);
                arrow_iv.setVisibility(View.GONE);
                tips_tv.setText("刷新完成！");
                tips_tv.setVisibility(View.VISIBLE);
                lastUpdate_tv.setVisibility(View.GONE);
                header_layout.setPadding(0, 0, 0, 0);

                // 刷新
                refreshToday();
                refreshWeek();
                refreshLife();

                header_layout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onRefreshComplete();
                    }
                }, 1000);
            }
        }.execute();
    }

    /**
     * 异步刷新完成
     */
    private void onRefreshComplete() {
        mCurrentState = STATE_IDLE;
        // 获取刷新时间
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String t = format.format(date);
        lastUpdate_tv.setText("最近更新：" + t);
        changeHeaderViewByState();
    }

    private void changeHeaderViewByState() {
        switch (mCurrentState) {
            case STATE_PULL_TO_REFRESH:
                headProgress.setVisibility(View.GONE);
                tips_tv.setText("下拉刷新");
                tips_tv.setVisibility(View.VISIBLE);
                arrow_iv.setVisibility(View.VISIBLE);
                if (isArrowUp) {
                    isArrowUp = false;
                    arrow_iv.startAnimation(reverseAnimation);
//                    Log.e(TAG, "true");
                }
                lastUpdate_tv.setVisibility(View.VISIBLE);
                break;
            case STATE_RELEASE_TO_REFRESH:
                arrow_iv.setVisibility(View.VISIBLE);
                headProgress.setVisibility(View.GONE);
                tips_tv.setVisibility(View.VISIBLE);
                lastUpdate_tv.setVisibility(View.GONE);
                if (!isArrowUp) {
                    isArrowUp = true;
                    arrow_iv.clearAnimation();
                    arrow_iv.startAnimation(tipsAnimation);
                }
                tips_tv.setText("松开刷新");
                break;
            case STATE_REFRESHING:
                lastHeaderPadding = 0;
                header_layout.setPadding(0, lastHeaderPadding, 0, 0);
                header_layout.invalidate();
                arrow_iv.clearAnimation();
                arrow_iv.setVisibility(View.INVISIBLE);
                headProgress.setVisibility(View.VISIBLE);
                tips_tv.setText("正在刷新...");
                lastUpdate_tv.setVisibility(View.GONE);
                break;
            case STATE_IDLE:
                headProgress.setVisibility(View.GONE);
                arrow_iv.setVisibility(View.GONE);
                tips_tv.setVisibility(View.GONE);
                lastUpdate_tv.setVisibility(View.GONE);
                lastHeaderPadding = -1 * headerHeight;
                header_layout.setPadding(0, lastHeaderPadding, 0, 0);
                header_layout.invalidate();

                weather_footer.setVisibility(View.GONE);
                break;
            case STATE_LOADING:
                weather_footer.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 由于onCreate里面拿不到header的高度需要手动计算
     * @param view
     */
    private void measureView(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null) {
            params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, params.width);
        int height = params.height;
        int childHeightSpec;
        if (height > 0) {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(
                    height, View.MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = View.MeasureSpec.makeMeasureSpec(0,
                    View.MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
    }

    // 解决ScrollView嵌套ListView只能显示一行
    private void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取listview对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        // listView.getDividerHeight()获取子项间分隔符占用的高度
        // params.height最后得到整个ListView完整显示需要的高度
        listView.setLayoutParams(params);
    }

    /**
     * 加载今日详情
     */
    private void loadToday() {
        TextView today_tv = new TextView(this);
        today_tv.setText(R.string.today_weather);
        today_tv.setTextSize(16);
        today_tv.setGravity(Gravity.CENTER_VERTICAL);
        today_tv.setPadding(60, 0, 0, 0);
        today_tv.setHeight(120);
        today_tv.setTextColor(Color.BLACK);
        today_layout.addView(today_tv, 0);
        TextView divider = new TextView(this);
        divider.setHeight(2);
        divider.setBackgroundColor(Color.YELLOW);
        today_layout.addView(divider, 1);

        refreshToday();
    }

    /**
     * 刷新今日详情
     */
    private void refreshToday() {
        TodayBean[] beans = new TodayBean[4];
        for (int i = 0; i < 4; i++) {
            beans[i] = new TodayBean();
        }
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.today_fl);
        beans[0].setIcon(bitmap);
        beans[0].setItem("体感");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.today_hum);
        beans[1].setIcon(bitmap);
        beans[1].setItem("湿度");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.today_pres);
        beans[2].setIcon(bitmap);
        beans[2].setItem("气压");
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.today_vis);
        beans[3].setIcon(bitmap);
        beans[3].setItem("能见度");
        try {
            String now_str = WeatherDataUtil.getNow();
            JSONObject now = new JSONObject(now_str);
            beans[0].setValue(now.getString("fl") + "℃");
            beans[1].setValue(now.getString("hum") + "%");
            beans[2].setValue(now.getString("pres") + "hpa");
            beans[3].setValue(now.getString("vis") + "km");

            String daily_forecast_str = WeatherDataUtil.getDailyForecast();
            JSONArray daily_forecast = new JSONArray(daily_forecast_str);
            JSONObject today= daily_forecast.getJSONObject(0);
            String sun_str = today.getString("astro");
            JSONObject sun = new JSONObject(sun_str);
            today_sunrise.setText(sun.getString("sr"));
            today_sunset.setText(sun.getString("ss"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 清空
        todayViewList.clear();
        for (int i = 0; i < 4; i++) {
            todayViewList.add(beans[i]);
        }
        todayLv.setAdapter(new TodayViewAdapter(this, todayViewList));
    }

    /**
     * 加载一周天气
     */
    private void loadWeek() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        TextView week_about = new TextView(this);
        week_about.setText(R.string.week_about);
        week_about.setTextSize(16);
        week_about.setTextColor(Color.BLACK);
        week_about.setGravity(Gravity.CENTER_VERTICAL);
        week_about.setHeight(120);
        week_about.setPadding(60, 0, 0, 0);
        linearLayout.addView(week_about);
        TextView divider = new TextView(this);
        divider.setHeight(2);
        divider.setBackgroundColor(Color.YELLOW);
        linearLayout.addView(divider);
        weekLv.addHeaderView(linearLayout);

        refreshWeek();
    }

    /**
     * 刷新一周天气
     */
    private void refreshWeek() {
        // 返回数据解析存入beans
        WeekBean[] beans = new WeekBean[7];
        for (int i = 0; i < 7; i++) {
            beans[i] = new WeekBean();
        }
//        long time = System.currentTimeMillis();
//        Date date = new Date(time);
//         "yyyy年MM月dd日 HH时mm分ss秒 EEEE"
//         "yyyy-MM-dd HH:mm:ss"
//         "HH:mm:ss"
//         "MM/dd"
//         "EEEE"   星期二
//         "E"      周二
//        SimpleDateFormat format = new SimpleDateFormat("E");
//		String t = format.format(date);
        String daily_forecast_str = WeatherDataUtil.getDailyForecast();
        try {
            for (int i = 0; i < 7; i++) {
                JSONArray daily_forecast = new JSONArray(daily_forecast_str);
                JSONObject day = daily_forecast.getJSONObject(i);
                // 日期
                String date = day.getString("date");
                date = date.substring(5, 10);
                beans[i].setDay(date);
                // 天气情况和图标
                String cond_str = day.getString("cond");
                JSONObject cond = new JSONObject(cond_str);
                String txt_d = cond.getString("txt_d");
                String txt_n = cond.getString("txt_n");
                if (txt_d.equals(txt_n)) {
                    beans[i].setCond(txt_d);
                } else {
                    beans[i].setCond(txt_d + "转" + txt_n);
                }
                String code = cond.getString("code_d");
                Bitmap icon = BitmapFactory.decodeResource(getResources(), WeatherIconUtil.getWeatherIcon(code));
                beans[i].setIcon(icon);
                // 温度
                String tmp_str = day.getString("tmp");
                JSONObject tmp = new JSONObject(tmp_str);
                String min = tmp.getString("min");
                String max = tmp.getString("max");
                String temp = min + "~" + max + "℃";
                beans[i].setTemp(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 清空
        weekBeanList.clear();
        for (int i = 0; i < 7; i++) {
            weekBeanList.add(beans[i]);
        }
        weekLv.setAdapter(new WeekViewAdapter(this, weekBeanList));
    }

    /**
     * 加载生活指数
     */
    private void loadLife() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView life_about = new TextView(this);
        life_about.setText(R.string.life_about);
        life_about.setTextColor(Color.BLACK);
        life_about.setTextSize(16);
        life_about.setGravity(Gravity.CENTER_VERTICAL);
        life_about.setHeight(120);
        life_about.setPadding(60, 0, 0, 0);
        layout.addView(life_about);
        TextView divider = new TextView(this);
        divider.setHeight(2);
        divider.setBackgroundColor(Color.YELLOW);
        layout.addView(divider);
        lifeLv.addHeaderView(layout);

        refreshLife();
    }

    /**
     * 刷新生活详情
     */
    private void refreshLife() {
        // 返回数据解析存入beans
        LifeBean[] beans = new LifeBean[7];
        for (int i = 0; i < 7; i++) {
            beans[i] = new LifeBean();
        }

        beans[0].setItem(getString(R.string.life_cmf));
        beans[1].setItem(getString(R.string.life_drsg));
        beans[2].setItem(getString(R.string.life_flu));
        beans[3].setItem(getString(R.string.life_sport));
        beans[4].setItem(getString(R.string.life_trav));
        beans[5].setItem(getString(R.string.life_uv));
        beans[6].setItem(getString(R.string.life_cw));
        String suggestion_str = WeatherDataUtil.getSuggestion();
        try {
            JSONObject suggestion = new JSONObject(suggestion_str);
            String comf_str = suggestion.getString("comf");
            cmf = new JSONObject(comf_str);
            beans[0].setCondition(cmf.getString("brf"));
            String drsg_str = suggestion.getString("drsg");
            drsg = new JSONObject(drsg_str);
            beans[1].setCondition(drsg.getString("brf"));
            String flu_str = suggestion.getString("flu");
            flu = new JSONObject(flu_str);
            beans[2].setCondition(flu.getString("brf"));
            String sport_str = suggestion.getString("sport");
            sport = new JSONObject(sport_str);
            beans[3].setCondition(sport.getString("brf"));
            String trav_str = suggestion.getString("trav");
            trav = new JSONObject(trav_str);
            beans[4].setCondition(trav.getString("brf"));
            String uv_str = suggestion.getString("uv");
            uv = new JSONObject(uv_str);
            beans[5].setCondition(uv.getString("brf"));
            String cw_str = suggestion.getString("cw");
            cw = new JSONObject(cw_str);
            beans[6].setCondition(cw.getString("brf"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_cmf);
        beans[0].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_drsg);
        beans[1].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_flu);
        beans[2].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_sport);
        beans[3].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_trav);
        beans[4].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_uv);
        beans[5].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_cw);
        beans[6].setIcon(bmp);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_item_down);
        // 清空
        lifeBeanList.clear();
        for (int i = 0; i < 7; i++) {  // 第一项为title
            beans[i].setUpDown(bmp);
            lifeBeanList.add(beans[i]);
        }
        lifeViewAdapter = new LifeViewAdapter(this, lifeBeanList);
        lifeLv.setAdapter(lifeViewAdapter);
        // 设置点击打开详情的监听
        lifeLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return; // 点击title时无需响应
                }
                position--; // 由于listview添加了title，position有向后移1

                if (position != currentPosition) {
                    if (currentPosition != -1) {
                        lifeBeanList.get(currentPosition).setMore("");
                        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_item_down);
                        lifeBeanList.get(currentPosition).setUpDown(bmp);
                        lifeBeanList.get(currentPosition).setClick(false);
                    }
                    // currentPosition只是position的备份，无需自减
                    currentPosition = position;
                }

                boolean pre = !lifeBeanList.get(currentPosition).getClick();
                Bitmap bmp;
                if (pre) {
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_item_up);
                    try {
                        switch (position) {
                            case 0:
                                lifeBeanList.get(position).setMore(cmf.getString("txt"));
                                break;
                            case 1:
                                lifeBeanList.get(position).setMore(drsg.getString("txt"));
                                break;
                            case 2:
                                lifeBeanList.get(position).setMore(flu.getString("txt"));
                                break;
                            case 3:
                                lifeBeanList.get(position).setMore(sport.getString("txt"));
                                break;
                            case 4:
                                lifeBeanList.get(position).setMore(trav.getString("txt"));
                                break;
                            case 5:
                                lifeBeanList.get(position).setMore(uv.getString("txt"));
                                break;
                            case 6:
                                lifeBeanList.get(position).setMore(cw.getString("txt"));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    bmp = BitmapFactory.decodeResource(getResources(), R.drawable.life_item_down);
                    lifeBeanList.get(currentPosition).setMore("");
                }
                lifeBeanList.get(currentPosition).setClick(pre);
                lifeBeanList.get(position).setUpDown(bmp);

                lifeViewAdapter.notifyDataSetChanged();
                setListViewHeightBasedOnChildren(lifeLv);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(1);
    }
}
