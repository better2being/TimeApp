package com.tsunami.timeapp.activity1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.Cheeses;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Friend;
import com.tsunami.timeapp.model1.User;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.ArrayList;

/**
 * @author shenxiaoming
 */
public class AddFriendActivity extends BaseActivity {

    public static final String TAG = "AddFriendActivity";

    private RecyclerView recyclerView;
    private AddFriendRvAdapter adapter;

    private List<String> queryList;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addfriend_activity);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.addfriendtoolbar);
//        setSupportActionBar(toolbar);
//
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//            // 2 添加的代码
//            getSupportActionBar().setTitle("搜索好友");
//            /////////////////////////////////
//        }

        recyclerView = (RecyclerView) findViewById(R.id.add_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("搜索中");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                        Toast.makeText(AddFriendActivity.this, "搜索取消", Toast.LENGTH_SHORT).show();
                        AddFriendActivity.this.finish();
                    }
                });

        // 查询好友
        queryList = new ArrayList<>();
        queryFriend();
        adapter = new AddFriendRvAdapter(AddFriendActivity.this, queryList);
        recyclerView.setAdapter(adapter);
    }

    private void queryFriend() {
        // 开启异步任务
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                progressDialog.show();
            }

            @Override
            protected Void doInBackground(Void... params) {
                // 创建网络访问的url地址栏
                String url = "http://192.168.1.119:8080/ServeNew/friendCheck";
                RequestParams rp = new RequestParams(url);
                // 封装该rp对象的请求参数
                rp.addBodyParameter("op", "findfriend");
                rp.addBodyParameter("keyword", getIntent().getStringExtra("queryname"));
                // xutils post提交
                x.http().post(rp, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e("findfriend", "success");
                        // 登录dialog清除
                        progressDialog.dismiss();
                        if (TextUtils.isEmpty(result)) {
                            Toast.makeText(AddFriendActivity.this, "搜索的用户不存在", Toast.LENGTH_LONG).show();
                            return;
                        }
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            JSONArray array = jsonArray.getJSONArray(0);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject json = new JSONObject(array.get(i).toString());
                                queryList.add(json.getString("friendname"));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(AddFriendActivity.this, "搜索失败，请重试", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                    @Override
                    public void onCancelled(CancelledException e) {

                    }
                    @Override
                    public void onFinished() {
                        progressDialog.dismiss();
                        Log.e(TAG, "dialogsdismiss");
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                adapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * 返回键
     */
    @Override
    public void onBackPressed() {
        AddFriendActivity.this.finish();
        super.onBackPressed();
    }
    public void onIsBack(View view) {
        onBackPressed();
    }


    public static class AddFriendRvAdapter
            extends RecyclerView.Adapter<AddFriendRvAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mList;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;
            public final Button mButton;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.add_avatar);
                mTextView = (TextView) view.findViewById(R.id.add_text);
                mButton = (Button) view.findViewById(R.id.add_btn);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public String getValueAt(int position) {
            return mList.get(position);
        }

        public AddFriendRvAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mList = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.addfriend_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mList.get(position);
            holder.mTextView.setText(mList.get(position));

            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressDialog dialog = new ProgressDialog(v.getContext());
                    dialog.setMessage("正在添加");
                    dialog.show();
                    // 创建网络访问的url地址栏
                    String url = "http://192.168.1.119:8080/ServeNew/friendCheck";
                    RequestParams rp = new RequestParams(url);
                    // 封装该rp对象的请求参数
                    rp.addBodyParameter("op", "addfriend");
                    rp.addBodyParameter("username", User.getInstance().getUsername());
                    rp.addBodyParameter("friendname", holder.mTextView.getText().toString());
                    // xutils post提交
                    x.http().post(rp, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            // dialog清除
                            dialog.dismiss();
                            if ("ok".equals(result)) {
                                // 存入数据库
                                UserDB.getInstance(v.getContext()).saveFriend(
                                        new Friend(holder.mTextView.getText().toString()));
                                Toast.makeText(v.getContext(), "添加成功", Toast.LENGTH_LONG).show();
                            } else if ("repetition".equals(result)){
                                Toast.makeText(v.getContext(), "该用户已在你的好友列表", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(v.getContext(), "添加失败，请重试", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onError(Throwable throwable, boolean b) {
                            Toast.makeText(v.getContext(), "添加失败", Toast.LENGTH_LONG).show();
                        }
                        @Override
                        public void onCancelled(CancelledException e) {

                        }
                        @Override
                        public void onFinished() {
                            dialog.dismiss();
                            Log.e(TAG, "dialogsdismiss");
                        }
                    });
                }
            });

            Glide.with(holder.mImageView.getContext())
                    .load(Cheeses.getRandomCheeseDrawable())
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }


    public void back(View view) {
        AddFriendActivity.this.finish();
    }


}
