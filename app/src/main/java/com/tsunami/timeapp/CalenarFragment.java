package com.tsunami.timeapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kelin.calendarlistview.library.CalendarHelper;
import com.kelin.calendarlistview.library.CalendarListView;
import com.tsunami.timeapp.retrofit.RetrofitProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.TreeMap;

import rx.Notification;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author shenxiaoming
 */
public class CalenarFragment extends Fragment {

    public static final SimpleDateFormat DAY_FORMAT = new SimpleDateFormat("yyyyMMdd");
    public static final SimpleDateFormat YEAR_MONTH_FORMAT = new SimpleDateFormat("yyyy年MM月");

    private CalendarListView calendarListView;
    private DayNewsListAdapter dayNewsListAdapter;
    private CalendarItemAdapter calendarItemAdapter;

    private JsonService jsonService = new JsonService();
    private boolean flag = false;
    //key:date "yyyy-mm-dd" format.
    private TreeMap<String, List<NewsService.News.StoriesBean>> listTreeMap = new TreeMap<>();

    private Handler handler = new Handler();

    private RelativeLayout linearLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        try {

        linearLayout = (RelativeLayout) inflater.inflate(
                R.layout.calen_layout, container, false);
        if (linearLayout == null) {
            Log.e("linearLayout", "null");
        } else {
            Log.e("linearLayout", linearLayout.toString());
        }

//            RelativeLayout relativeLayout = new RelativeLayout(getActivity());


//        ActionBar actionBar = getActivity().getSupportActionBar();
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayHomeAsUpEnabled(true);

        return linearLayout;

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("return", "null");
            return null;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onStart() {
        super.onStart();
        calendarListView = (CalendarListView) linearLayout.findViewById(R.id.calen_lv);
        dayNewsListAdapter = new DayNewsListAdapter(getActivity());
        calendarItemAdapter = new CalendarItemAdapter(getActivity());
        if (calendarListView == null) {
            Log.e("calendarListView", "null");
        } else {
            Log.e("calendarListView", "good");
        }

        calendarListView.setCalendarListViewAdapter(calendarItemAdapter, dayNewsListAdapter);

        // set start time,just for test.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        loadNewsList(DAY_FORMAT.format(calendar.getTime()));
//        actionBar.setTitle(YEAR_MONTH_FORMAT.format(calendar.getTime()));

        // deal with refresh and load more event.
        calendarListView.setOnListPullListener(new CalendarListView.onListPullListener() {
            @Override
            public void onRefresh() {
                String date = listTreeMap.firstKey();
                Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
                calendar.add(Calendar.MONTH, -1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                loadNewsList(DAY_FORMAT.format(calendar.getTime()));
            }

            @Override
            public void onLoadMore() {
                String date = listTreeMap.lastKey();
                Calendar calendar = CalendarHelper.getCalendarByYearMonthDay(date);
                calendar.add(Calendar.MONTH, 1);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                loadNewsList(DAY_FORMAT.format(calendar.getTime()));
            }
        });

        //
        calendarListView.setOnMonthChangedListener(new CalendarListView.OnMonthChangedListener() {
            @Override
            public void onMonthChanged(String yearMonth) {
                Calendar calendar = CalendarHelper.getCalendarByYearMonth(yearMonth);
//                actionBar.setTitle(YEAR_MONTH_FORMAT.format(calendar.getTime()));
                loadCalendarData(yearMonth);
                //Toast.makeText(getActivity(), YEAR_MONTH_FORMAT.format(calendar.getTime()), Toast.LENGTH_SHORT).show();
            }
        });

        calendarListView.setOnCalendarViewItemClickListener(new CalendarListView.OnCalendarViewItemClickListener() {
            @Override
            public void onDateSelected(View View, String selectedDate, int listSection, SelectedDateRegion selectedDateRegion) {

            }
        });
    }

    //this code is just for generate test date for ListView!
    private void loadNewsList(String date) {
        Calendar calendar = getCalendarByYearMonthDay(date);
        String key = CalendarHelper.YEAR_MONTH_FORMAT.format(calendar.getTime());

        // just not care about how data to create.

        Random random = new Random();
        final List<String> set = new ArrayList<>();/*
        while (set.size() < 5) {
            int i = random.nextInt(29);
            System.out.println(i+":"+date+" :key:"+key);
            if (i > 0) {
                if (!set.contains(key + "-" + i)) {
                    if (i < 10) {
                        set.add(key + "-0" + i);
                    } else {
                        set.add(key + "-" + i);
                    }
                }
            }
        }*/
        set.add("key"+"27");
        //for (int i=0;)


        System.out.println("ketok");
        // JsonService js = new JsonService();
        String str = jsonService.dayTable("20160827", "testtable");
        jsonService.dayTable("20160828", "testtable");
        System.out.println(str + "date:" + date);


        Observable<Notification<NewsService.News>> newsListOb =
                RetrofitProvider.getInstance().create(NewsService.class)
                        .getNewsList(date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                       // .compose(bindToLifecycle())
                        .materialize().share();
        //System.out.println(str);
//        final boolean flag=true;
        flag = true;
        newsListOb.filter(Notification::isOnNext)
                .map(n -> n.getValue())
                .filter(m -> !m.getStories().isEmpty())
                .flatMap(m -> Observable.from(m.getStories()))
                .doOnNext(i -> {
                    /*
                    int index = random.nextInt(5);
                    String day = set.get(index);
                    //System.out.println("day:"+day+" k"+);
                    if (listTreeMap.get(day) != null) {
                        List<NewsService.News.StoriesBean> list = listTreeMap.get(day);
                        list.add(i);
                    } else {
                        List<NewsService.News.StoriesBean> list = new ArrayList<NewsService.News.StoriesBean>();
                        list.add(i);
                        i.setTitle("测试");
                        listTreeMap.put(day, list);
                    }*/
                    if (flag) {
                        System.out.println("运行");
                        String day = "2016-08-29";
                        if (listTreeMap.get(day) != null) {
                            //System.out.print());
                            List<NewsService.News.StoriesBean> list = listTreeMap.get(day);
                            NewsService.News.StoriesBean p = new NewsService.News.StoriesBean();
                            p.setTitle("test23");
                            list.add(p);
                        } else {
                            List<NewsService.News.StoriesBean> list = new ArrayList<NewsService.News.StoriesBean>();
                            list.add(i);
                            i.setTitle("测试标题");
                            listTreeMap.put(day, list);
                        }
                        JsonService.dayWork dt = jsonService.list.get("20160827");
                        //System.out.println("运行2:"+(dt.work.size()));
                        if (dt != null) {
                            day="2016-08-27";
                            System.out.println("运行2:"+(dt.work.size()));
                            for (int j = 0; j < dt.work.size(); j++) {
                                String title= dt.work.get(j).workname;
                                String time = dt.work.get(j).worktime;
                                System.out.println("for j:" + title);
                                if (listTreeMap.get(day) != null) {
                                    //System.out.print());
                                    List<NewsService.News.StoriesBean> list1 = listTreeMap.get(day);
                                    if (j==0) list1.clear();
                                    NewsService.News.StoriesBean p = new NewsService.News.StoriesBean();
                                    p.setTitle(title);
                                    p.setTime(time);
                                    list1.add(p);
                                } else {
                                    List<NewsService.News.StoriesBean> list1 = new ArrayList<NewsService.News.StoriesBean>();
                                    NewsService.News.StoriesBean p = new NewsService.News.StoriesBean();
                                    p.setTitle(title);
                                    p.setTime(time);
                                    list1.add(p);
                                    listTreeMap.put(day, list1);
                                }
                                flag = false;
                            }
                            System.out.println("运行2");
                            //jsonService.list.get("20160827").work.clear();
                        }else  System.out.println("运行3");

                        dt = jsonService.list.get("20160828");
                        //System.out.println("运行2:"+(dt.work.size()));
                        if (dt != null) {
                            day="2016-08-28";
                            System.out.println("运行2:"+(dt.work.size()));
                            for (int j = 0; j < dt.work.size(); j++) {
                                String title= dt.work.get(j).workname;
                                String time = dt.work.get(j).worktime;
                                System.out.println("for j:" + title);
                                if (listTreeMap.get(day) != null) {
                                    //System.out.print());
                                    List<NewsService.News.StoriesBean> list1 = listTreeMap.get(day);
                                    if (j==0) list1.clear();
                                    NewsService.News.StoriesBean p = new NewsService.News.StoriesBean();
                                    p.setTitle(title);
                                    p.setTime(time);
                                    list1.add(p);
                                } else {
                                    List<NewsService.News.StoriesBean> list1 = new ArrayList<NewsService.News.StoriesBean>();
                                    NewsService.News.StoriesBean p = new NewsService.News.StoriesBean();
                                    p.setTitle(title);
                                    p.setTime(time);
                                    list1.add(p);
                                    listTreeMap.put(day, list1);
                                }
                                flag = false;
                            }
                            System.out.println("运行2");
                            //jsonService.list.get("20160827").work.clear();
                        }else  System.out.println("运行3");

                    }


                })
                .toList()
                .subscribe((l) -> {
                    dayNewsListAdapter.setDateDataMap(listTreeMap);
                    dayNewsListAdapter.notifyDataSetChanged();
                    calendarItemAdapter.notifyDataSetChanged();
                })
        ;
    }

    // date (yyyy-MM),load data for Calendar View by date,load one month data one times.
    // generate test data for CalendarView,imitate to be a Network Requests. update "calendarItemAdapter.getDayModelList()"
    //and notifyDataSetChanged will update CalendarView.
    private void loadCalendarData(String date) {
        new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Random random = new Random();
                            if (date.equals(calendarListView.getCurrentSelectedDate().substring(0, 7))) {
                                for (String d : listTreeMap.keySet()) {
                                    if (date.equals(d.substring(0, 7))) {
                                        CustomCalendarItemModel customCalendarItemModel = calendarItemAdapter.getDayModelList().get(d);
                                        if (customCalendarItemModel != null) {
                                            customCalendarItemModel.setNewsCount(listTreeMap.get(d).size());
                                            customCalendarItemModel.setFav(random.nextBoolean());
                                        }

                                    }
                                }
                                calendarItemAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }.start();

    }

    public static Calendar getCalendarByYearMonthDay(String yearMonthDay) {
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTimeInMillis(DAY_FORMAT.parse(yearMonthDay).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
