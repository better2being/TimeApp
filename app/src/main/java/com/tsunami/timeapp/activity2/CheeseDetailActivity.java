package com.tsunami.timeapp.activity2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity1.FriendWeekActivity;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Friend;
import com.tsunami.timeapp.model1.User;


import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * @author wangshujie
 */

public class CheeseDetailActivity extends AppCompatActivity {

    public static final String EXTRA_NAME = "cheese_name";
    private static final String TAG = "CheeseDetailActivity";

    private String friendName;
    private ProgressDialog dialog;

    private CircleImageView circleImageView;
    private ImageView friend_img1;
    private ImageView friend_img2;
    private ImageView friend_img3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        friendName = getIntent().getStringExtra(EXTRA_NAME);

        Toolbar toolbar = (Toolbar) findViewById(R.id.friend_detial_toolbar);
        circleImageView = (CircleImageView) findViewById(R.id.friend_detail_avatar);
        friend_img1 = (ImageView) findViewById(R.id.friend_img1);
        friend_img2 = (ImageView) findViewById(R.id.friend_img2);
        friend_img3= (ImageView) findViewById(R.id.friend_img3);


        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 2 添加的代码
            getSupportActionBar().setTitle("详细资料");
        }

        // 初始化view
        initView();

        // 返回键 navigation icon click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog = new ProgressDialog(CheeseDetailActivity.this);
        dialog.setMessage("正在删除");
        TextView friend_tv = (TextView) findViewById(R.id.detail_text);
        friend_tv.setText(friendName);

//        // 随机产生头像
//        Glide.with(CheeseDetailActivity.this)
//                .load(Cheeses.getRandomCheeseDrawable())
//                .fitCenter()
//                .into(circleImageView);
//        // 随机产生头像
//        Glide.with(CheeseDetailActivity.this)
//                .load(Cheeses.getRandomCheeseDrawable())
//                .fitCenter()
//                .into(friend_img1);
//        // 随机产生头像
//        Glide.with(CheeseDetailActivity.this)
//                .load(Cheeses.getRandomCheeseDrawable())
//                .fitCenter()
//                .into(friend_img2);
//        // 随机产生头像
//        Glide.with(CheeseDetailActivity.this)
//                .load(Cheeses.getRandomCheeseDrawable())
//                .fitCenter()
//                .into(friend_img3);
    }

    /**
     * 初始化view  1
     */
    private void initView() {
        // friend   性别、年龄、地区、个性签名
        Friend friend = UserDB.getInstance(CheeseDetailActivity.this).detailFriend(friendName);
        TextView sex_tv = (TextView) findViewById(R.id.friend_sex);
        sex_tv.setText(friend.getSex());
        TextView age_tv = (TextView) findViewById(R.id.friend_age);
        age_tv.setText(friend.getAge());
        TextView region_tv = (TextView) findViewById(R.id.friend_region);
        region_tv.setText(friend.getRegion());
        TextView signature_tv = (TextView) findViewById(R.id.friend_signature);
        signature_tv.setText(friend.getSignature());
        // 个人相册

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.friend_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.detail_delete:
                deleteFriend();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 删除该好友操作
     */
    private void deleteFriend() {
        // 开启异步任务
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                dialog.show();
            }
            @Override
            protected Void doInBackground(Void... params) {
                // 创建网络访问的url地址栏
                String url = "http://192.168.1.119:8080/ServeNew/friendCheck";
                RequestParams rp = new RequestParams(url);
                // 封装该rp对象的请求参数
                rp.addBodyParameter("op", "deletefriend");
                rp.addBodyParameter("client", User.getInstance().getUsername());
                rp.addBodyParameter("friend", friendName);
                // xutils post提交
                x.http().post(rp, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // dialog清除
                        dialog.dismiss();
                        if ("ok".equals(result)) {
                            // 存入数据库
                            UserDB.getInstance(CheeseDetailActivity.this).deleteFriend(friendName);
                            setResult(RESULT_OK);
                            Toast.makeText(CheeseDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CheeseDetailActivity.this, "删除失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(CheeseDetailActivity.this, "删除失败，请重试", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(CancelledException e) {
                    }
                    @Override
                    public void onFinished() {
                        dialog.dismiss();
                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();
    }

    /**
     * 与好友日程比对，找出共同空余时间
     */
    public void onFindCommonSchedule(View view) {
        ProgressDialog dialog = new ProgressDialog(CheeseDetailActivity.this);
        dialog.setMessage("正在加载...");

        // 开启异步任务
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                dialog.show();
            }
            @Override
            protected Void doInBackground(Void... params) {
                String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
                RequestParams rp = new RequestParams(url);
                rp.addBodyParameter("op", "interview");
                rp.addBodyParameter("client", User.getInstance().getUsername());
                Log.e("username",  User.getInstance().getUsername());
                rp.addBodyParameter("friend", friendName);
                long time = System.currentTimeMillis();
                Date date = new Date(time);

                Calendar calendar = Calendar.getInstance();
                for (int i = 0; i < 7; i++) {
                    calendar.setTime(date);
                    int d = calendar.get(Calendar.DATE);
                    calendar.set(Calendar.DATE, d + i);
                    String day = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
                    day = day.substring(0, 4) + day.substring(5, 7) + day.substring(8, 10);
                    Log.e("date", day);
                    // date1 ~ date7
                    rp.addBodyParameter("date" + (i + 1), day);
                }
                x.http().post(rp, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("on success", result);
                        Intent intent = new Intent(CheeseDetailActivity.this, FriendWeekActivity.class);
                        intent.putExtra("op", "interview");
                        intent.putExtra("result", result);
                        startActivity(intent);
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(CheeseDetailActivity.this, "加载异常", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        dialog.dismiss();
                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();
    }

    /**
     * 查看好友的日程
     */
    public void onLookFriendSchedule(View view) {

        ProgressDialog dialog = new ProgressDialog(CheeseDetailActivity.this);
        dialog.setMessage("正在加载...");
        Log.e("load", "friendSchedule");
        // 开启异步任务
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                dialog.show();
            }
            @Override
            protected Void doInBackground(Void... params) {
                String url="http://192.168.1.119:8080/ServeNew/arrangeCheck";
                RequestParams rp = new RequestParams(url);
                rp.addBodyParameter("op", "loadTable");
                rp.addBodyParameter("client", friendName);
                x.http().post(rp, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("on scuess", result);
                        Intent intent = new Intent(CheeseDetailActivity.this, FriendWeekActivity.class);
                        intent.putExtra("result",result);
                        intent.putExtra("op", "loadTable");
                        startActivity(intent);
                    }
                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(CheeseDetailActivity.this, "加载异常", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCancelled(CancelledException cex) {
                    }
                    @Override
                    public void onFinished() {
                        dialog.dismiss();
                    }
                });
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
            }
        }.execute();
    }

}
