
package com.tsunami.timeapp.activity2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Friend;
import com.tsunami.timeapp.util.CharacterParser;
import com.tsunami.timeapp.util.FriendRvAdapter;
import com.tsunami.timeapp.util.FriendSortModel;
import com.tsunami.timeapp.util.PinyinComparator;
import com.tsunami.timeapp.view1.SideBar;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.util.Collections;
/**
 * @author wangshujie
 */
public class CheeseListFragment extends Fragment {

    public static final String TAG = "CheeseListFragment";

    private CharacterParser characterParser;

    private List<FriendSortModel> SourceDateList;

    private SideBar sideBar;
    private TextView dialog;

    private RecyclerView recyclerView;
    private FriendRvAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                FrameLayout  frameLayout = (FrameLayout) inflater.inflate(
                        R.layout.fragment_cheese_list, container, false);
//        setupRecyclerView(rv);

        recyclerView = (RecyclerView) frameLayout.findViewById(R.id.recyclerview);
        characterParser = new CharacterParser();
        sideBar = (SideBar) frameLayout.findViewById(R.id.sidebar);
        dialog = (TextView) frameLayout.findViewById(R.id.dialog);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        adapter = new FriendRvAdapter(getActivity(), getFriendNameList());
        recyclerView.setAdapter(adapter);

        // 右侧的sideBar       1
        sideBar.setTextView(dialog);
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    recyclerView.scrollToPosition(position + 1);
                }
            }
        });

        return frameLayout;
    }

    /**
     * 刷新好友列表
     */
    public void invalidate() {
        adapter.setDatas(getFriendNameList());
        adapter.notifyDataSetChanged();
    }

    /**
     * 获取好友List     1
     * @return
     */
    public List<FriendSortModel> getFriendNameList() {
        List<Friend> friend = UserDB.getInstance(getContext()).loadFriends();
        String[] friend_str = new String[friend.size()];
        // 获取friendName列表
        for (int i = 0; i < friend.size(); i++) {
            friend_str[i] = friend.get(i).getFriendName();
        }

        SourceDateList =  filledData(friend_str);
//        测试数据 省份
//        filledData(getResources().getStringArray(R.array.provinces));
        Collections.sort(SourceDateList, new PinyinComparator());

        return SourceDateList;
    }

    /**
     * 从文字中取出letter比较   1
     * @param date
     * @return
     */
    private List<FriendSortModel> filledData(String[] date) {
        List<FriendSortModel> mSortList = new ArrayList<>();
        ArrayList<String> indexString = new ArrayList<>();

        for (int i = 0; i < date.length; i++) {
            FriendSortModel sortModel = new FriendSortModel();
            sortModel.setName(date[i]);
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {

                //对重庆多音字做特殊处理
                if (pinyin.startsWith("zhongqing")) {
                    sortString = "C";
                    sortModel.setSortLetters("C");
                } else {
                    sortModel.setSortLetters(sortString.toUpperCase());
                }

                if (!indexString.contains(sortString)) {
                    indexString.add(sortString);
                }
            }

            mSortList.add(sortModel);
        }
        Collections.sort(indexString);
//        sideBar.setIndexText(indexString);
        return mSortList;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        recyclerView.setAdapter(new SimpleStringRecyclerViewAdapter(getActivity(),
                getRandomSublist(Cheeses.sCheeseStrings, 30)));
    }

    private List<String> getRandomSublist(String[] array, int amount) {
        ArrayList<String> list = new ArrayList<>(amount);
        Random random = new Random();
        while (list.size() < amount) {
            list.add(array[random.nextInt(array.length)]);
        }
        return list;
    }

    public static class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder> {

        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<String> mValues;

        public static class ViewHolder extends RecyclerView.ViewHolder {
            public String mBoundString;

            public final View mView;
            public final ImageView mImageView;
            public final TextView mTextView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mImageView = (ImageView) view.findViewById(R.id.avatar);
                mTextView = (TextView) view.findViewById(android.R.id.text1);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mTextView.getText();
            }
        }

        public String getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(Context context, List<String> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mBoundString = mValues.get(position);
            holder.mTextView.setText(mValues.get(position));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, CheeseDetailActivity.class);
                    intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.mBoundString);

                    context.startActivity(intent);
                }
            });

            Glide.with(holder.mImageView.getContext())
                    .load(Cheeses.getRandomCheeseDrawable())
                    .fitCenter()
                    .into(holder.mImageView);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }
}
