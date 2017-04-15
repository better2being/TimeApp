package com.tsunami.timeapp.activity2;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.tsunami.timeapp.Activity3.Alarm.AlarmActivity;
import com.tsunami.timeapp.Activity3.GesturePassword.GestureVerifyActivity;
import com.tsunami.timeapp.Activity3.Setting;
import com.tsunami.timeapp.Activity3.clock.XiuYiXiuActivity;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.CalenarFragment;
import com.tsunami.timeapp.activity1.AddFriendActivity;
import com.tsunami.timeapp.activity1.CircleFragment;
import com.tsunami.timeapp.activity1.PublishActivity;
import com.tsunami.timeapp.activity1.WeatherDetail;

import com.tsunami.timeapp.activity2.Picture.PictureBitMap;
import com.tsunami.timeapp.activity2.weekview.NoJsonServiceBasicActivity;
import com.tsunami.timeapp.circle.bean.CircleItem;
import com.tsunami.timeapp.circle.utils.DatasUtil;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Circle;
import com.tsunami.timeapp.model1.Friend;
import com.tsunami.timeapp.model1.User;
import com.tsunami.timeapp.ui.NewNoteActivity;
import com.tsunami.timeapp.util.FileStreamUtil;
import com.tsunami.timeapp.util.ScreenUtil;
import com.tsunami.timeapp.util.WeatherDataUtil;
import com.tsunami.timeapp.util.WeatherIconUtil;


import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wangshujie
 */

public class UserActivity extends AppCompatActivity {

    private static final String TAG = "UserActivity";

    private ViewPager viewPager;

    // 右下角点击标记  1
    private boolean mFlag = true;
    // 右下角动画Button  1
    private FloatingActionButton fab;
    private int[] mResId = new int[]{R.id.fab_friendcircle,
            R.id.fab_new_note, R.id.fab_weather, R.id.fab_weekview};
    private List<ImageView> mImageList = new ArrayList<>();

    // 天气layout
    private LinearLayout weather_layout;
    private LocationManager locationManager;

    private TextView weatherAqi;
    private TextView weatherCity;
    private TextView weatherCond;
    private TextView weatherTemp;
    private TextView weatherTmpNow;
    private TextView weatherUpdate;
    private ImageView weatherIcon;
    private ProgressBar weatherProgressBar;

    private boolean currentNetwork = false;
    private boolean currentLocation = false;

    private Uri imageUri;

    // 默认城市 北京
    private String mCity = "beijing";

    private NetworkChangeReceiver networkChangeReceiver;
    private LocationChangeReceiver locationChangeReceiver;

    // 天气获取完成的Toast
    private Toast updatingToast;
    // 天气关闭flag
    private boolean weaCloseFlag = false;


    private DrawerLayout mDrawerLayout;

    private CircleImageView useravatarView;

    private LinearLayout headerView;

    // Activity的引用
    private static UserActivity userActivity;

    public UserActivity() {
        userActivity = this;
    }

    /**
     * 获取当前Activity的引用
     * @return Activity.this
     */
    public static UserActivity getUserActivity() {
        return userActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);


        //注册EventBus
        EventBus.getDefault().register(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.user_activity_toolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);//侧边菜单视图

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);//侧边菜单项
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        headerView = (LinearLayout) navigationView.inflateHeaderView(R.layout.nav_header);


        useravatarView = (CircleImageView) headerView.findViewById(R.id.useravatar_view);

        File outputImage = new File(Environment.getExternalStorageDirectory(), "username.jpg");

        try {
            imageUri = Uri.fromFile(outputImage);
            Bitmap bitmap1 = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            if (bitmap1 != null) {


                //picture.setImageBitmap(bitmap);
                useravatarView.setImageBitmap(bitmap1);
            } else {

                //Bitmap bitmap = getLoacalBitmap("/sdcard/tempImage.jpg");
                // iv_avatar.setImageBitmap(bitmap);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        useravatarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserActivity.this,PersonActivity.class));

            }
        });

        // 本地db与服务器端同步  1
        synchronizeDB();


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 网络变化广播监听
        //     进入网络将发生变化，进而获取位置，解析城市，获取天气
        IntentFilter networkFilter = new IntentFilter();
        networkFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(networkChangeReceiver, networkFilter);
        // 位置服务变化广播监听
        IntentFilter locationFilter = new IntentFilter();
        locationFilter.addAction(LocationManager.PROVIDERS_CHANGED_ACTION);
        locationChangeReceiver = new LocationChangeReceiver();
        registerReceiver(locationChangeReceiver, locationFilter);

        // 初始化view  右下角fab  1
        initView();
    }

    public void onEventMainThread(PictureBitMap event) {

        Bitmap bitmap =event.getBitmap();
        if (bitmap != null) {


            //picture.setImageBitmap(bitmap);
            useravatarView.setImageBitmap(bitmap);
        } else {

            //Bitmap bitmap = getLoacalBitmap("/sdcard/tempImage.jpg");
            // iv_avatar.setImageBitmap(bitmap);
        }
    }

    /**
     * 本地db数据与服务器端同步    1
     */
    private void synchronizeDB() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // 同步Friends
                synchronizeFriends();
                return null;
            }
        }.execute();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // 同步Circles
                synchronizeCircles();
                return null;
            }
        }.execute();
    }
    /**
     * 本地Friends与服务器同步  1
     */
    private void synchronizeFriends() {
        Log.e("synchronize", "friends");
        // 创建网络访问的url地址栏
        String url = "http://192.168.1.119:8080/ServeNew/jsonCheck";
        RequestParams rp = new RequestParams(url);
        // 封装该rp对象的请求参数
        Log.e("sync username", User.getInstance().getUsername());
        rp.addBodyParameter("username", User.getInstance().getUsername());
        // xutils post提交
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("friends-json", result);
                List<Friend> friends = UserDB.getInstance(UserActivity.this).loadFriends();
                // 同步数据库
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONArray array = jsonArray.getJSONArray(0);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = new JSONObject(array.get(i).toString());
                        boolean exist = true;
                        for (Friend f : friends) {
                            if (f.getFriendName().equals(json.getString("name"))) {
                                exist = false;
                                break;
                            }
                        }
                        if (exist) {
                            UserDB.getInstance(UserActivity.this)
                                    .saveFriend(new Friend(json.getString("name")));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.e("syncfriend", "error");
            }
            @Override
            public void onCancelled(CancelledException e) {
            }
            @Override
            public void onFinished() {
            }
        });
    }
    /**
     * 本地Circles与服务器数据同步    1
     */
    private void synchronizeCircles() {
        Log.e("synchronize", "circles");
        // 创建网络访问的url地址栏
        String url = "http://192.168.1.119:8080/ServeNew/friendCheck";
        RequestParams rp = new RequestParams(url);
        // 封装该rp对象的请求参数
        rp.addBodyParameter("op", "getdynamic");
        rp.addBodyParameter("client", User.getInstance().getUsername());
        // xutils post提交
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("circles-json", result);
                List<Circle> circles = UserDB.getInstance(UserActivity.this).loadCircles();
                // 同步数据库
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    JSONArray array = jsonArray.getJSONArray(0);
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject json = new JSONObject(array.get(i).toString());
                        String time = json.getString("datetime");
                        time = time.substring(0, 19);
                        boolean exist = true;
//                        Log.e("ip username", json.getString("name"));
//                        Log.e("ip createtime", time);
                        for (Circle c : circles) {
//                            Log.e("db username", c.getAuthor());
//                            Log.e("db createtime", c.getCreateTime());

                            if (c.getCreateTime().equals(time)
                                    && c.getAuthor().equals(json.getString("name"))){
                                exist = false;
                                break;
                            }
                        }
                        if (exist) {
                            Circle circle = new Circle();
                            if (User.getInstance().getUsername().equals(json.getString("name"))) {
                                circle.setAuthor("自己");
                            } else {
                                circle.setAuthor(json.getString("name"));
                            }
                            circle.setContent(json.getString("dynamic"));
                            circle.setCreateTime(time);
                            UserDB.getInstance(UserActivity.this).saveCircle(circle);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onError(Throwable throwable, boolean b) {
                Log.e("synccircle", "error");
            }
            @Override
            public void onCancelled(Callback.CancelledException e) {

            }
            @Override
            public void onFinished() {
            }
        });
    }


    /**
     * 初始化view    1
     *  包含四个点击事件
     */
    private void initView() {
        // 右下角点击Button动画
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFlag) {
                    startAnim();
                } else {
                    closeAnim();
                }
            }
        });
        for (int i = 0; i < mResId.length; i++) {
            ImageView imageView = (ImageView) findViewById(mResId[i]);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.fab_friendcircle:
                            // 发动态
                            startActivityForResult(new Intent(UserActivity.this, PublishActivity.class), 1);
                        break;
                        case R.id.fab_new_note:
                            startActivity(new Intent(UserActivity.this, NewNoteActivity.class));
                            break;
                        case R.id.fab_weather:
                            if (weaCloseFlag) {
                                // 关闭天气
                                ObjectAnimator animator = ObjectAnimator.ofFloat(
                                        weather_layout,
                                        "translationY",
                                        660F, 0);
                                animator.setDuration(500);
                                animator.setInterpolator(new DecelerateInterpolator());
                                animator.start();
                                animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        weather_layout.setVisibility(View.GONE);

                                    }
                                });
                                weaCloseFlag = false;
                            } else {
                                // 显示天气
                                weather_layout.setVisibility(View.VISIBLE);
                                ObjectAnimator animator = ObjectAnimator.ofFloat(
                                        weather_layout,
                                        "translationY",
                                        660F);
                                animator.setDuration(500);
                                animator.setInterpolator(new DecelerateInterpolator());
                                animator.start();
                                animator.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                        if (!TextUtils.isEmpty(FileStreamUtil.load(UserActivity.this))) {
                                            // 从历史数据中读取
                                            WeatherDataUtil.setDataAll(FileStreamUtil.load(UserActivity.this));
                                            // 显示数据
                                            updateWeatherUI();
                                        } else {
                                            connectWeather();
                                        }
                                    }
                                });
                                weaCloseFlag = true;
                            }
                            break;
                        case R.id.fab_weekview:
                            startActivity(new Intent(UserActivity.this,NoJsonServiceBasicActivity.class));
                            break;
                    }
                }
            });
            mImageList.add(imageView);
        }

        // 天气
        weatherAqi = (TextView) findViewById(R.id.weather_aqi);
        weatherCity = (TextView) findViewById(R.id.weather_city);
        weatherCond = (TextView) findViewById(R.id.weather_cond);
        weatherTemp = (TextView) findViewById(R.id.weather_temp);
        weatherTmpNow = (TextView) findViewById(R.id.weather_tmp_now);
        weatherUpdate = (TextView) findViewById(R.id.weather_update);
        weatherIcon = (ImageView) findViewById(R.id.weather_icon);
        weatherProgressBar = (ProgressBar) findViewById(R.id.weather_progressBar);
        weather_layout = (LinearLayout) findViewById(R.id.weather_layout);

        // 自定义Toast
        LayoutInflater inflater = LayoutInflater.from(UserActivity.this);
        View view = inflater.inflate(R.layout.weather_toast, null);
        TextView tv = (TextView) view.findViewById(R.id.toast_tv);
        tv.setText("Updating...");
        updatingToast = new Toast(UserActivity.this);
        updatingToast.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
                125 * (int) ScreenUtil.getDeviceDensity(UserActivity.this));
        updatingToast.setDuration(Toast.LENGTH_LONG);
        updatingToast.setView(view);
    }

    /**
     * 进入获取更多的天气情况
     * @param view  点击按钮
     */
    public void weatherDetailBtn(View view) {

        // 判断是否已获取到天气数据
        //  且刷新完毕
        if (WeatherDataUtil.getDataAll() != null
                && weatherProgressBar.getVisibility() == View.GONE) {

            startActivityForResult(new Intent(UserActivity.this, WeatherDetail.class), 1);
        } else if (FileStreamUtil.load(UserActivity.this).isEmpty()) {
            if (!currentNetwork) {
                Toast.makeText(this, "请连接移动数据", Toast.LENGTH_SHORT).show();
            } else if (!currentLocation) {
                Toast.makeText(this, "请打开位置服务", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * UI中显示天气数据
     */
    private void updateWeatherUI() {
        Log.e(TAG, "updateWeatherUI");
        if (WeatherDataUtil.getDataAll() == null) {
            Log.e(TAG, "null");
        } else {
            // 设置 空气质量
            weatherAqi.setText(WeatherDataUtil.getAqi());
            // 城市基本情况
            weatherCity.setText(WeatherDataUtil.getCity());
            // 当前天气情况
            weatherTemp.setText(WeatherDataUtil.getTemp());
            if (!TextUtils.isEmpty(WeatherDataUtil.getCondCode())) {
                weatherIcon.setImageResource(WeatherIconUtil.getWeatherIcon(
                        WeatherDataUtil.getCondCode()));
            }
            weatherCond.setText(WeatherDataUtil.getCond());
            weatherTmpNow.setText(WeatherDataUtil.getTmp());
            weatherUpdate.setText(WeatherDataUtil.getUpdateTime());
            weather_layout.invalidate();
        }
    }

    /**
     * 获取位置信息
     */
    private void connectLocation() {
        // 获取network的位置提供器
        List<String> providerList = locationManager.getProviders(true);
        String provider;
        currentLocation = false;
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            currentLocation = true;
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            // 位置提供器关闭时，弹出Toast提示用户
           // Toast.makeText(UserActivity.this, "no location service", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                Log.e(TAG, "a location");
                // 位置信息中获取城市
                connectCity(location);
            } else {
                Log.e(TAG, "no location");
            }
            locationManager.requestLocationUpdates(provider, 1000 * 60, 200, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取城市名
     * @param location 位置信息
     */
    private void connectCity(Location location) {
        String city = "";
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(),
                    location.getLongitude(), 1);
            if (addressList != null) {
                // 获取所在市
                city += addressList.get(0).getLocality();
            }
            city = city.substring(0, city.indexOf("市"));
            if (!TextUtils.isEmpty(city)) {
                mCity = city;
            }
            WeatherDataUtil.setCity(mCity);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, mCity);
    }

    /**
     * 获取城市天气
     */
    private void connectWeather() {
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(1000);
                    // 获取天气
                    WeatherDataUtil.connectWeather();
                    Log.e(TAG, "connectWeather");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                // 更新UI界面
                updateWeatherUI();
                weatherProgressBar.setVisibility(View.GONE);
                updatingToast.cancel();
            }
        }.execute();
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onLocationChanged(Location location) {
            // 更新当前设备的位置信息
            connectCity(location);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            try {
                // 关闭程序时将监听器移除
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        unregisterReceiver(networkChangeReceiver);
        unregisterReceiver(locationChangeReceiver);

//        System.exit(0);

        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0: // 添加好友
                // 跳转到好友列表
                viewPager.setCurrentItem(0);
                CheeseListFragment fragment = (CheeseListFragment) getSupportFragmentManager().findFragmentByTag(
                        "android:switcher:" + R.id.viewpager + ":0");
                fragment.invalidate();
                break;
            case 1: // 发动态
                if (resultCode == RESULT_OK) {
                    // 跳转到好友动态
                    viewPager.setCurrentItem(2);
                    CircleFragment circleFragment = (CircleFragment) getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.viewpager + ":2");
                    CircleItem newItem = new CircleItem();
                    newItem.setContent(data.getStringExtra("content"));
                    newItem.setCreateTime(data.getStringExtra("createTime"));
                    String[] path = data.getStringArrayExtra("paths");
                    List<String> list = Arrays.asList(path);
                    newItem.setPhotos(list);
                    newItem.setUser(DatasUtil.curUser);
                    newItem.setId(DatasUtil.curUser.getId());
                    newItem.setFavorters(new ArrayList<>());
                    newItem.setComments(new ArrayList<>());
                    newItem.setType(CircleItem.TYPE_IMG);

                    Circle circle = new Circle();
                    circle.setAuthor(User.getInstance().getUsername());
                    circle.setContent(data.getStringExtra("content"));
                    circle.setHeadUrl(User.getInstance().getHeadUrl());
                    circle.setCreateTime(data.getStringExtra("createTime"));
                    UserDB.getInstance(UserActivity.this).saveCircle(circle);

                    if (circleFragment != null) {
                        if (circleFragment.getView() != null) {
                            circleFragment.addPublish(newItem, circle);
                        }
                    }
                } else if (resultCode == RESULT_CANCELED) {
                }
                break;
            case 3: // 看天气
                updateWeatherUI();
                weather_layout.setVisibility(View.GONE);
                break;
            case 5: // 好友操作
                if (resultCode == RESULT_OK) {
                    CheeseListFragment fragment_cheese = (CheeseListFragment) getSupportFragmentManager().findFragmentByTag(
                            "android:switcher:" + R.id.viewpager + ":0");
                    fragment_cheese.invalidate();
                }
            default:
                break;
        }
    }

    /**
     * 再点击右下角动画关闭   1
     */
    private void closeAnim() {
        ObjectAnimator animator0 = ObjectAnimator.ofFloat(fab,
                "alpha", 0.5F, 1F);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(mImageList.get(0),
                "translationY", -200F, 0);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(mImageList.get(1),
                "translationX", -200F, 0);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(mImageList.get(2),
                "translationY", -400F, 0);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(mImageList.get(3),
                "translationX", -400F, 0);
        ObjectAnimator animator = ObjectAnimator.ofFloat(
                weather_layout,
                "translationY",
                660F, 0);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        if (weaCloseFlag) {
            set.playTogether(animator0, animator1, animator2, animator3, animator4, animator);
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    weather_layout.setVisibility(View.GONE);
                    weaCloseFlag = false;
                }
            });
        } else {
            set.playTogether(animator0, animator1, animator2, animator3, animator4);
        }
        set.start();
        mFlag = true;
    }

    /**
     * 右下角点击动画  1
     */
    private void startAnim() {
        ObjectAnimator animator0 = ObjectAnimator.ofFloat(
                fab,
                "alpha",
                1F,
                0.5F);
        ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                mImageList.get(0),
                "translationY",
                -200F);
        ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                mImageList.get(1),
                "translationX",
                -200F);
        ObjectAnimator animator3 = ObjectAnimator.ofFloat(
                mImageList.get(2),
                "translationY",
                -400F);
        ObjectAnimator animator4 = ObjectAnimator.ofFloat(
                mImageList.get(3),
                "translationX",
                -400F);
        AnimatorSet set = new AnimatorSet();
        set.setDuration(500);
        set.setInterpolator(new DecelerateInterpolator());
        set.playTogether(
                animator0,
                animator1,
                animator2,
                animator3,
                animator4);
        set.start();
        mFlag = false;
    }

    // 监听网络变化广播 1
    class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "network onReceive");
            // 获取当前网络状态
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                currentNetwork = false;
                // 提醒当前无网络
                Toast.makeText(UserActivity.this, "当前无网络", Toast.LENGTH_SHORT).show();
            } else {
                currentNetwork = true;
                connectLocation();
                if (currentLocation) {
                    // 刷新
//                    updatingToast.show();
//                    weatherProgressBar.setVisibility(View.VISIBLE);
                    // 获取天气
                    connectWeather();
                }
            }
        }
    }

    // 监听位置服务变化广播   1
    class LocationChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "location onReceive");
            // 获取network的位置信息
            connectLocation();
            if (currentLocation) {
                if (currentNetwork) {
                    // 刷新
//                    updatingToast.show();
//                    weatherProgressBar.setVisibility(View.VISIBLE);
                    // 获取天气
                    connectWeather();
                }
            } else {
                // 位置提供器关闭时，弹出Toast提示用户
                Toast.makeText(UserActivity.this, "当前无位置服务", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }


    // 1 初始化SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        MenuItem searchItem = menu.findItem(R.id.action_friend_search);
        //searchItem.expandActionView(); // 默认展开搜索框
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint(getString(R.string.search_friend));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                Intent intent = new Intent(UserActivity.this, AddFriendActivity.class);
                intent.putExtra("queryname", s);
                startActivityForResult(intent, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
//                recyclerAdapter.getFilter().filter(s); // 2 文字改变就即时处理搜索
                return true;
            }
        });

        // 2 监听搜索框是否打开，用于隐藏FloatingActionBar和禁用下拉刷新
        //    MenuItemCompat.setOnActionExpandListener(searchItem, mainPresenter);

        return true;
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        switch (AppCompatDelegate.getDefaultNightMode()) {
//            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
//                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
//                break;
//            case AppCompatDelegate.MODE_NIGHT_AUTO:
//                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
//                break;
//            case AppCompatDelegate.MODE_NIGHT_YES:
//                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
//                break;
//            case AppCompatDelegate.MODE_NIGHT_NO:
//                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
//                break;
//        }
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
//            case R.id.menu_night_mode_system:
//                setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
//                break;
//            case R.id.menu_night_mode_day:
//                setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//                break;
//            case R.id.menu_night_mode_night:
//                setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//                break;
//            case R.id.menu_night_mode_auto:
//                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNightMode(int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        //getDelegate().setLocalNightMode(nightMode);
        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
            Toast.makeText(UserActivity.this,"sdhfshdfsa",Toast.LENGTH_SHORT).show();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new CheeseListFragment(), "好友列表");
        adapter.addFragment(new CalenarFragment(), "日程管理");
        adapter.addFragment(new CircleFragment(), "时间动态");
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(1);
    }



    // 2 侧滑菜单的子项的点击监听
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                       // mDrawerLayout.closeDrawers();

                        switch (menuItem.getItemId()) {
                            // 2 点击日程表按钮按钮
                            case R.id.nav_weekschedule:
                                startActivity(new Intent(UserActivity.this, NoJsonServiceBasicActivity.class));

                                break;
                            // 2 点击定时器按钮
                            case R.id.nav_secondtimer:
                               // mDrawerLayout.closeDrawers();
                                startActivity(new Intent(UserActivity.this, XiuYiXiuActivity.class));
                                break;
                            // 2 点击笔记列表按钮
                            case R.id.nav_notelist:
                               // mDrawerLayout.closeDrawers();
                                startActivity(new Intent(UserActivity.this, com.tsunami.timeapp.ui.MainActivity.class).putExtra("pageNum",0));
                                //mDrawerLayout.closeDrawers();
                                break;
                            // 2 点击回收站按钮
                            case R.id.nav_notetrash:
                                //mDrawerLayout.closeDrawers();
                                startActivity(new Intent(UserActivity.this, GestureVerifyActivity.class).putExtra("pageNum",1));
                                //startActivity(new Intent(UserActivity.this, com.tsunami.timeapp.ui.MainActivity.class).putExtra("pageNum",1));
                                break;


                            // 2 点击闹钟按钮
                            case R.id.nav_clock:
                              //  mDrawerLayout.closeDrawers();
                                startActivity(new Intent(UserActivity.this, AlarmActivity.class));
                                //mDrawerLayout.closeDrawers();
                                break;
                            // 2 点击设置按钮
                            case R.id.nav_moresettings:
                             //   mDrawerLayout.closeDrawers();
                               // startActivity(new Intent(UserActivity.this, SettingActivity.class));
                                startActivity(new Intent(UserActivity.this, Setting.class));
                                break;


                            default:
                                mDrawerLayout.closeDrawers();
                                break;
                        }
                        return true;
                    }
                });




    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }


    //方法：控件View的点击事件
    public void onClickAvatar(View v) {
        startActivity(new Intent(UserActivity.this,PersonActivity.class));

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            // 创建退出对话框
            AlertDialog isExit = new AlertDialog.Builder(this).create();
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("确定要退出吗");
            // 添加选择按钮并注册监听
            isExit.setButton(AlertDialog.BUTTON_POSITIVE,"确定",listener);
            isExit.setButton(AlertDialog.BUTTON_NEGATIVE,"取消", listener);
            // 显示对话框
            isExit.show();

        }

        return false;

    }
    /**监听对话框里面的button点击事件*/
    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener()
    {
        public void onClick(DialogInterface dialog, int which)
        {
            switch (which)
            {
                case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
                    //android.os.Process.killProcess(android.os.Process.myPid());
                    Intent intent=new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    UserActivity.this.startActivity(intent);
                    System.exit(0);

                    break;
                case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
                    break;
                default:
                    break;
            }
        }
    };
}


