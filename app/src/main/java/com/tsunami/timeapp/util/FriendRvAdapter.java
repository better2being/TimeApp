package com.tsunami.timeapp.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.CheeseDetailActivity;
import com.tsunami.timeapp.activity2.Cheeses;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;


import java.io.IOException;
import java.util.List;

import Decoder.BASE64Decoder;

/**
 * @author wangshujie
 */
public class FriendRvAdapter extends
        RecyclerView.Adapter<FriendRvAdapter.ViewHolder>  implements SectionIndexer {

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getItemCount(); i++) {
            String sortStr = mList.get(i).getSortLetters();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return mList.get(position).getSortLetters().charAt(0);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public String mBoundString;

        public final View mView;
        public final ImageView mImageView;
        public final TextView mTextView;
        public final TextView tvLetter;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mImageView = (ImageView) view.findViewById(R.id.avatar);
            mTextView = (TextView) view.findViewById(android.R.id.text1);
            tvLetter = (TextView) view.findViewById(R.id.tv_catagory);
        }
    }

    private final TypedValue mTypedValue = new TypedValue();
    private int mBackground;
    private List<FriendSortModel> mList;

    public FriendSortModel getValueAt(int position) {
        return mList.get(position);
    }

    public FriendRvAdapter(Context context, List<FriendSortModel> items) {
        context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
        mBackground = mTypedValue.resourceId;
        mList = items;
    }

    private Context mContext;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int position) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        view.setBackgroundResource(mBackground);

        mContext = parent.getContext();

        return new ViewHolder(view);
    }

    public Bitmap bitmap=null;
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.mBoundString = mList.get(position).getName();
        viewHolder.mTextView.setText(mList.get(position).getName());

        final FriendSortModel mContent = mList.get(position);
        int section = getSectionForPosition(position);
        if (position == getPositionForSection(section)) {
            viewHolder.tvLetter.setVisibility(View.VISIBLE);
            viewHolder.tvLetter.setText(mContent.getSortLetters());
        } else {
            viewHolder.tvLetter.setVisibility(View.GONE);
        }

        viewHolder.mTextView.setText(this.mList.get(position).getName());

        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, CheeseDetailActivity.class);
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, viewHolder.mTextView.getText().toString());
                ((AppCompatActivity)context).startActivityForResult(intent, 5);
            }
        });

//        // 随机产生头像
//        Glide.with(viewHolder.mImageView.getContext())
//                .load(Cheeses.getRandomCheeseDrawable())
//                .fitCenter()
//                .into(viewHolder.mImageView);

//        // 异步任务  下载头像
//        new AsyncTask<Void, Void, Void>() {
//            String client = viewHolder.mTextView.toString();
//            @Override
//            protected void onPreExecute() {
//
//            }
//            @Override
//            protected Void doInBackground(Void... params) {
//                String url="http://192.168.1.119:8080/ServeNew/downlCheck";
//                RequestParams rp = new RequestParams(url);
//                rp.addBodyParameter("client", client);
//                Log.e("friend ", client);
//                x.http().post(rp, new Callback.CommonCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//                        // System.out.println("result+"+result);
//
//                        //System.out.println(result);
//                        System.out.print(result);
//                        bitmap=getBitmapFromByte(result);
//                        if (!TextUtils.isEmpty(viewHolder.mTextView.getText().toString())) {
//
//                            viewHolder.mImageView.setImageBitmap(bitmap);
//                        }
//
//                    }
//
//                    @Override
//                    public void onError(Throwable ex, boolean isOnCallback) {
//                        Log.e("error", "下载头像异常 ");
//                        //bitmap=getBitmapFromByte(str);
//                    }
//
//                    @Override
//                    public void onCancelled(Callback.CancelledException cex) {
//                    }
//                    @Override
//                    public void onFinished() {
//                        System.out.println("end");
//                        //  getBitmapFromByte(str);
//                    }
//                });
//
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//
//            }
//        }.execute();
     }

    public Bitmap getBitmapFromByte(String str){
        if(str==null){
            System.out.println("out");
            return null;
        }
        Bitmap bitmap1=null;

        System.out.println("allright");
        //opts.inSampleSize=
        byte[] bytes = decode(str);
        System.out.println("try");
        //byte[] bytes = hex2byte( str );
        System.out.println(bytes.length);
        bitmap1= BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        //bitmap=BitmapFactory.decodeByteArray(bytes, 0, bytes.length,opts);
        return bitmap1;
    }
    public static byte[] decode(String str) {
        byte[] bt = null;
        try {
            BASE64Decoder decoder = new BASE64Decoder();
            bt = decoder.decodeBuffer(str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bt;
    }


    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setDatas(List<FriendSortModel> list) {
        mList.clear();
        for (FriendSortModel item : list) {
            mList.add(item);
        }
    }
}
