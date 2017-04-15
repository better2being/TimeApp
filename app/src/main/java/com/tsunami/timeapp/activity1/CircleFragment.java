package com.tsunami.timeapp.activity1;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.malinskiy.superrecyclerview.OnMoreListener;
import com.malinskiy.superrecyclerview.SuperRecyclerView;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.db1.adapter.CircleAdapter;
import com.tsunami.timeapp.circle.bean.CircleItem;
import com.tsunami.timeapp.circle.bean.CommentConfig;
import com.tsunami.timeapp.circle.bean.CommentItem;
import com.tsunami.timeapp.circle.bean.FavortItem;
import com.tsunami.timeapp.circle.mvp.contract.CircleContract;
import com.tsunami.timeapp.circle.mvp.presenter.CirclePresenter;
import com.tsunami.timeapp.circle.utils.CommonUtils;
import com.tsunami.timeapp.circle.widgets.CommentListView;
import com.tsunami.timeapp.circle.widgets.DivItemDecoration;
import com.tsunami.timeapp.circle.widgets.dialog.UpLoadDialog;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Circle;
import com.tsunami.timeapp.model1.User;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * @author shenxiaoming
 */
public class CircleFragment extends Fragment implements CircleContract.View{

    protected static final String TAG = "CircleFragment";
    private CircleAdapter circleAdapter;
    private LinearLayout edittextbody;
    private EditText editText;
    private ImageView sendIv;

    private int screenHeight;
    private int editTextBodyHeight;
    private int currentKeyboardH;
    private int selectCircleItemH;
    private int selectCommentItemOffset;

    private CirclePresenter presenter;
    private CommentConfig commentConfig;
    private SuperRecyclerView recyclerView;
    private RelativeLayout bodyLayout;
    private LinearLayoutManager layoutManager;

    private final static int TYPE_PULLREFRESH = 1;
    private final static int TYPE_UPLOADREFRESH = 2;
    private UpLoadDialog uploadDialog;

    // UserActivity中取出的控件
    private AppBarLayout appBarLayout;
    private FloatingActionButton fab;
    private ImageView fab_a;
    private ImageView fab_b;
    private ImageView fab_c;
    private ImageView fab_d;

    private ImageView userPhoto;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(
                R.layout.circle_layout, container, false);

        appBarLayout = (AppBarLayout)getActivity().findViewById(R.id.appbar);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab_a = (ImageView) getActivity().findViewById(R.id.fab_friendcircle);
        fab_b = (ImageView) getActivity().findViewById(R.id.fab_new_note);
        fab_c = (ImageView) getActivity().findViewById(R.id.fab_weather);
        fab_d = (ImageView) getActivity().findViewById(R.id.fab_weekview);



//        presenter = new CirclePresenter(this);
        initView(linearLayout);

//        presenter.loadData(TYPE_PULLREFRESH);
        return linearLayout;
    }

//    @Override
//    public void onDestroy() {
//        if(presenter !=null){
//            presenter.recycle();
//        }
//        super.onDestroy();
//    }

    @SuppressLint({ "ClickableViewAccessibility", "InlinedApi" })
    private void initView(LinearLayout linearLayout) {

//        initTitle(linearLayout);
        initUploadDialog(linearLayout);

        recyclerView = (SuperRecyclerView) linearLayout.findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DivItemDecoration(2, true));
        recyclerView.getMoreProgressView().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (edittextbody.getVisibility() == View.VISIBLE) {
                    updateEditTextBodyVisible(View.GONE, null);
                    return true;
                }
                return false;
            }
        });
        // 下拉刷新
        recyclerView.setRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                        presenter.loadData(TYPE_PULLREFRESH);
                        // 加载数据库的动态
                        Iterator<Circle> iterator = UserDB.getInstance(getActivity()).loadCircles().iterator();
                        List<Circle> list = new ArrayList<>();
                        while (iterator.hasNext()) {
                            list.add(0, iterator.next());
//                            Log.e("while", iterator.next().getContent());
                        }
                        circleAdapter.setDatas(list);
                        recyclerView.setRefreshing(false);
                    }
                }, 2000);
            }
        });
        // 滑动
        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Glide.with(getActivity()).resumeRequests();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState != RecyclerView.SCROLL_STATE_IDLE){
                    Glide.with(getActivity()).pauseRequests();
                }
            }
        });

        circleAdapter = new CircleAdapter(getActivity());
//        circleAdapter.setCirclePresenter(presenter);


        // 加载数据库的动态
        Iterator<Circle> iterator = UserDB.getInstance(getActivity()).loadCircles().iterator();
        List<Circle> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(0, iterator.next());
//            Log.e("while", iterator.next().getContent());
        }
        circleAdapter.setDatas(list);
        recyclerView.setAdapter(circleAdapter);

        edittextbody = (LinearLayout) linearLayout.findViewById(R.id.editTextBodyLl);
        editText = (EditText) linearLayout.findViewById(R.id.circleEt);
        sendIv = (ImageView) linearLayout.findViewById(R.id.sendIv);
        sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (presenter != null) {
                    //发布评论
                    String content =  editText.getText().toString().trim();
                    if(TextUtils.isEmpty(content)){
                        Toast.makeText(getActivity(), "评论内容不能为空...", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    presenter.addComment(content, commentConfig);
                }
                updateEditTextBodyVisible(View.GONE, null);
            }
        });

        setViewTreeObserver(linearLayout);
    }

    private void initUploadDialog(LinearLayout linearLayout) {
        uploadDialog = new UpLoadDialog(getActivity());
    }

    private void setViewTreeObserver(LinearLayout linearLayout) {
        bodyLayout = (RelativeLayout) linearLayout.findViewById(R.id.bodyLayout);
        final ViewTreeObserver swipeRefreshLayoutVTO = bodyLayout.getViewTreeObserver();
        swipeRefreshLayoutVTO.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                bodyLayout.getWindowVisibleDisplayFrame(r);
                int statusBarH =  getStatusBarHeight();//状态栏高度
                int screenH = bodyLayout.getRootView().getHeight();
                if(r.top != statusBarH ){
                    //在这个demo中r.top代表的是状态栏高度，在沉浸式状态栏时r.top＝0，通过getStatusBarHeight获取状态栏高度
                    r.top = statusBarH;
                }
                int keyboardH = screenH - (r.bottom - r.top);
//                Log.e(TAG, "screenH＝ "+ screenH +" &keyboardH = " + keyboardH + " &r.bottom=" + r.bottom + " &top=" + r.top + " &statusBarH=" + statusBarH);

                if(keyboardH == currentKeyboardH){//有变化时才处理，否则会陷入死循环
                    return;
                }

                currentKeyboardH = keyboardH;
                screenHeight = screenH;//应用屏幕的高度
                editTextBodyHeight = edittextbody.getHeight();

                if(keyboardH<150){//说明是隐藏键盘的情况
                    updateEditTextBodyVisible(View.GONE, null);
                    return;
                }
                //偏移listview
                if(layoutManager!=null && commentConfig != null){
                    layoutManager.scrollToPositionWithOffset(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE, getListviewOffset(commentConfig));
                }
            }
        });
    }

    /**
     * 获取状态栏高度
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    /**
     * 添加动态
     * @param circleItem
     */
    public void addPublish(CircleItem circleItem, Circle circle) {
        // 上传服务器，成功后保存到本地数据库
        upLoadCircle(circle);
    }

    /**
     * 动态上传服务器
     */
    private void upLoadCircle(Circle circle) {
        ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("正在发布");
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
                rp.addBodyParameter("op", "insertdynamic");
                rp.addBodyParameter("name", User.getInstance().getUsername());
                rp.addBodyParameter("dynamic", circle.getContent());
                rp.addBodyParameter("datetime", circle.getCreateTime());
                // xutils post提交
                x.http().post(rp, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // dialog清除
                        dialog.dismiss();
                        if ("ok".equals(result)) {
//                            UserDB.getInstance(getActivity()).saveCircle(circle);
                            Toast.makeText(getContext(), "发布成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "发布失败，请重试", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(getContext(), "发布失败", Toast.LENGTH_LONG).show();
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
            protected void onPostExecute(Void result) {
                circleAdapter.getDatas().add(0, circle);
                circleAdapter.notifyDataSetChanged();
            }
        }.execute();
    }



    /*----------------------------------------------------------------------------------------*/
    // 删除动态
    @Override
    public void update2DeleteCircle(String circleId) {
        List<CircleItem> circleItems = circleAdapter.getDatas();
        for(int i=0; i<circleItems.size(); i++){
            if(circleId.equals(circleItems.get(i).getId())){
                circleItems.remove(i);
                circleAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemRemoved(i+1);
                return;
            }
        }
    }

    // 点赞
    @Override
    public void update2AddFavorite(int circlePosition, FavortItem addItem) {
        if(addItem != null){
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            item.getFavorters().add(addItem);
            circleAdapter.notifyDataSetChanged();
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
    }

    // 取消点赞
    @Override
    public void update2DeleteFavort(int circlePosition, String favortId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<FavortItem> items = item.getFavorters();
        for(int i=0; i<items.size(); i++){
            if(favortId.equals(items.get(i).getId())){
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            }
        }
    }

    // 添加评论
    @Override
    public void update2AddComment(int circlePosition, CommentItem addItem) {
        if(addItem != null){
            CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
            item.getComments().add(addItem);
            circleAdapter.notifyDataSetChanged();
            //circleAdapter.notifyItemChanged(circlePosition+1);
        }
        //清空评论文本
        editText.setText("");
    }

    // 删除评论
    @Override
    public void update2DeleteComment(int circlePosition, String commentId) {
        CircleItem item = (CircleItem) circleAdapter.getDatas().get(circlePosition);
        List<CommentItem> items = item.getComments();
        for(int i=0; i<items.size(); i++){
            if(commentId.equals(items.get(i).getId())){
                items.remove(i);
                circleAdapter.notifyDataSetChanged();
                //circleAdapter.notifyItemChanged(circlePosition+1);
                return;
            }
        }
    }

    /**
     * 显示评论窗
      */
    @Override
    public void updateEditTextBodyVisible(int visibility, CommentConfig commentConfig) {
        this.commentConfig = commentConfig;
        edittextbody.setVisibility(visibility);
        // 收起appBarLayout
        appBarLayout.setExpanded(false);

        measureCircleItemHighAndCommentItemOffset(commentConfig);

        if(View.VISIBLE==visibility){
            editText.requestFocus();
            //弹出键盘
            CommonUtils.showSoftInput( editText.getContext(),  editText);
            // 右下角fab隐藏
            fab.setVisibility(View.GONE);
            fab_a.setVisibility(View.GONE);
            fab_b.setVisibility(View.GONE);
            fab_c.setVisibility(View.GONE);
            fab_d.setVisibility(View.GONE);
        }else if(View.GONE==visibility){
            //隐藏键盘
            CommonUtils.hideSoftInput( editText.getContext(),  editText);
            // 右下角fab显示
            fab.setVisibility(View.VISIBLE);
            fab_a.setVisibility(View.VISIBLE);
            fab_b.setVisibility(View.VISIBLE);
            fab_c.setVisibility(View.VISIBLE);
            fab_d.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void update2loadData(int loadType, List<CircleItem> datas) {
        if (loadType == TYPE_PULLREFRESH){
            circleAdapter.setDatas(datas);
        }else if(loadType == TYPE_UPLOADREFRESH){
            circleAdapter.getDatas().addAll(datas);
        }
        circleAdapter.notifyDataSetChanged();

        if(circleAdapter.getDatas().size()<45 + CircleAdapter.HEADVIEW_SIZE){
            recyclerView.setupMoreListener(new OnMoreListener() {
                @Override
                public void onMoreAsked(int overallItemsCount, int itemsBeforeMore, int maxLastVisiblePosition) {

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            presenter.loadData(TYPE_UPLOADREFRESH);
                        }
                    }, 2000);

                }
            }, 1);
        }else{
            recyclerView.removeMoreListener();
            recyclerView.hideMoreProgress();
        }
    }

    /**
     * 测量偏移量
     * @param commentConfig
     * @return
     */
    private int getListviewOffset(CommentConfig commentConfig) {
        if(commentConfig == null)
            return 0;
        //这里如果你的listview上面还有其它占高度的控件，则需要减去该控件高度，listview的headview除外。
        //int listviewOffset = mScreenHeight - mSelectCircleItemH - mCurrentKeyboardH - mEditTextBodyHeight;
        int listviewOffset = screenHeight - selectCircleItemH - currentKeyboardH - editTextBodyHeight - appBarLayout.getHeight();// - titleBar.getHeight();
        if(commentConfig.commentType == CommentConfig.Type.REPLY){
            //回复评论的情况
            listviewOffset = listviewOffset + selectCommentItemOffset;
        }
        Log.e(TAG, "listviewOffset : " + listviewOffset);
        return listviewOffset;
    }

    /**
     * 弹出评论窗时的偏移量
     * @param commentConfig
     */
    private void measureCircleItemHighAndCommentItemOffset(CommentConfig commentConfig){
        if(commentConfig == null)
            return;

        int firstPosition = layoutManager.findFirstVisibleItemPosition();
        //只能返回当前可见区域（列表可滚动）的子项
        View selectCircleItem = layoutManager.getChildAt(commentConfig.circlePosition + CircleAdapter.HEADVIEW_SIZE - firstPosition);

        if(selectCircleItem != null){
            selectCircleItemH = selectCircleItem.getHeight();
        }

        if(commentConfig.commentType == CommentConfig.Type.REPLY){
            //回复评论的情况
            CommentListView commentLv = (CommentListView) selectCircleItem.findViewById(R.id.commentList);
            if(commentLv!=null){
                //找到要回复的评论view,计算出该view距离所属动态底部的距离
                View selectCommentItem = commentLv.getChildAt(commentConfig.commentPosition);
                if(selectCommentItem != null){
                    //选择的commentItem距选择的CircleItem底部的距离
                    selectCommentItemOffset = 0;
                    View parentView = selectCommentItem;
                    do {
                        int subItemBottom = parentView.getBottom();
                        parentView = (View) parentView.getParent();
                        if(parentView != null){
                            selectCommentItemOffset += (parentView.getHeight() - subItemBottom);
                        }
                    } while (parentView != null && parentView != selectCircleItem);
                }
            }
        }
    }

    @Override
    public void showLoading(String msg) {
    }

    @Override
    public void hideLoading() {
    }

    @Override
    public void showError(String errorMsg) {
    }
}
