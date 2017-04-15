package com.tsunami.timeapp.ui;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

import com.tsunami.timeapp.App;

import com.tsunami.timeapp.R;
import com.tsunami.timeapp.adpater.base.BaseRecyclerViewAdapter;
import com.tsunami.timeapp.adpater.NotesAdapter;
import com.tsunami.timeapp.injector.component.DaggerActivityComponent;
import com.tsunami.timeapp.injector.module.ActivityModule;
import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.mvp.presenters.impl.MainPresenter;
import com.tsunami.timeapp.mvp.views.impl.MainView;
import com.tsunami.timeapp.utils.DialogUtils;
import com.tsunami.timeapp.utils.SnackbarUtils;
import com.tsunami.timeapp.utils.ToolbarUtils;
import com.tsunami.timeapp.view.BetterFab;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.OnClick;

/**
 * @author wangshujie
 */
public class MainActivity extends BaseActivity implements MainView{
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.refresher) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recyclerView) RecyclerView recyclerView;
    //@Bind(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @Bind(R.id.left_drawer_listview) ListView mDrawerMenuListView;
    @Bind(R.id.left_drawer) View drawerRootView;
    @Bind(R.id.fab) BetterFab fab;
    @Bind(R.id.coordinator_layout) CoordinatorLayout coordinatorLayout;
    @Bind(R.id.progress_wheel) ProgressWheel progressWheel;
    @Inject MainPresenter mainPresenter;
    private ActionBarDrawerToggle mDrawerToggle;
    private NotesAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        launchWithNoAnim();
        super.onCreate(savedInstanceState);
        initializePresenter();
        mainPresenter.onCreate(savedInstanceState);
        if(getIntent().getIntExtra("pageNum",0)==0){

            toolbar.setTitle("个人笔记");
        }
        else{
            toolbar.setTitle("私密空间");
        }
        mainPresenter.onDrawerItemSelect(getIntent().getIntExtra("pageNum",0));

    }

    private void initializePresenter() {
        mainPresenter.attachView(this);
    }

    @Override
    protected void initializeDependencyInjector() {

        // 2 完成依赖注入
        App app = (App) getApplication();
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(app.getAppComponent())
                .build();
        mActivityComponent.inject(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mainPresenter.onSaveInstanceState(outState);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainPresenter.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainPresenter.onResume();
    }

    @Override
    protected void onPause() {
        mainPresenter.onPause();
        super.onPause();
    }

    @Override
    public void onStop() {
       // mainPresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        mainPresenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public void initToolbar(){
        if(getIntent().getIntExtra("pageNum",0)==0){

            toolbar.setTitle("个人笔记");
        }
        else{
            toolbar.setTitle("私密空间");
        }
        ToolbarUtils.initToolbar(toolbar, this);
    }

//    @Override
//    public void initDrawerView(List<String> list) {
//        SimpleListAdapter adapter = new DrawerListAdapter(this, list);
//        mDrawerMenuListView.setAdapter(adapter);
//        mDrawerMenuListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) ->
//                mainPresenter.onDrawerItemSelect(position));
//        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0){
//            @Override
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                invalidateOptionsMenu();
//                mainPresenter.onDrawerOpened();
//            }
//
//            @Override
//            public void onDrawerClosed(View drawerView) {
//                super.onDrawerClosed(drawerView);
//                invalidateOptionsMenu();
//                mainPresenter.onDrawerClosed();
//            }
//        };
//        mDrawerToggle.setDrawerIndicatorEnabled(true);
//        mDrawerLayout.setDrawerListener(mDrawerToggle);
//        mDrawerLayout.setScrimColor(getCompactColor(R.color.drawer_scrim_color));
//    }


    // 2 対笔记的布局进行初始化
    @Override
    public void initRecyclerView(List<SNote> notes){
        recyclerAdapter = new NotesAdapter(notes, this);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter.setOnInViewClickListener(R.id.notes_item_root,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, SNote values) {
                        super.OnClickListener(parentV, v, position, values);
                        mainPresenter.onRecyclerViewItemClick(position, values);
                    }
                });
        recyclerAdapter.setOnInViewClickListener(R.id.note_more,
                new BaseRecyclerViewAdapter.onInternalClickListenerImpl<SNote>() {
                    @Override
                    public void OnClickListener(View parentV, View v, Integer position, SNote values) {
                        super.OnClickListener(parentV, v, position, values);
                        mainPresenter.showPopMenu(v, position, values);
                    }
                });
        recyclerAdapter.setFirstOnly(false);
        recyclerAdapter.setDuration(300);
        recyclerView.setAdapter(recyclerAdapter);
        refreshLayout.setColorSchemeColors(getColorPrimary());
        refreshLayout.setOnRefreshListener(mainPresenter);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbar.setTitle(title);
    }

    @Override
    public void switchNoteTypePage(List<SNote> notes) {
        recyclerAdapter.setList(notes);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void addNote(SNote note) {
        recyclerAdapter.add(note);
    }

    @Override
    public void updateNote(SNote note) {
        recyclerAdapter.update(note);
    }

    @Override
    public void removeNote(SNote note) {
        recyclerAdapter.remove(note);
    }

    @Override
    public void scrollRecyclerViewToTop() {
        recyclerView.smoothScrollToPosition(0);
    }

//    @Override
//    public void closeDrawer() {
//        if (mDrawerLayout.isDrawerOpen(drawerRootView)) {
//            mDrawerLayout.closeDrawer(drawerRootView);
//        }
//    }

//    @Override
//    public void openOrCloseDrawer() {
//        if (mDrawerLayout.isDrawerOpen(drawerRootView)) {
//            mDrawerLayout.closeDrawer(drawerRootView);
//        } else {
//            mDrawerLayout.openDrawer(drawerRootView);
//        }
//    }

    @Override
    public void setDrawerItemChecked(int position) {
        mDrawerMenuListView.setItemChecked(position, true);
    }

//    @Override
//    public boolean isDrawerOpen() {
//        return mDrawerLayout.isDrawerOpen(drawerRootView);
//    }

    @Override
    public void setMenuGravity(int gravity) {
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) drawerRootView.getLayoutParams();
        params.gravity = gravity;
        drawerRootView.setLayoutParams(params);
    }

    @Override
    public void showFab(boolean visible) {

        //  fab.setForceHide(!visible); ZLF 20160910
        fab.setForceHide(false);
    }

    @Override
    public void showProgressWheel(boolean visible){
        progressWheel.setBarColor(getColorPrimary());
        if (visible){
            if (!progressWheel.isSpinning())
                progressWheel.spin();
        }else{
            progressWheel.postDelayed(() -> {
                if (progressWheel.isSpinning()) {
                    progressWheel.stopSpinning();
                }
            }, 300);
        }
    }

    @Override
    public void stopRefresh() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void startRefresh() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public boolean isRefreshing() {
        return refreshLayout.isRefreshing();
    }

    @Override
    public void enableSwipeRefreshLayout(boolean enable) {
        refreshLayout.setEnabled(enable);
    }

    @Override
    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        recyclerView.setLayoutManager(manager);
    }

    // 2 在每个CardView上面需要显示菜单，包含”编辑”和”回收”
    @Override
    public void showNormalPopupMenu(View view, SNote note) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater()
                .inflate(R.menu.menu_notes_more, popup.getMenu());
        popup.setOnMenuItemClickListener((item -> mainPresenter.onPopupMenuClick(item.getItemId(), note)));
        //ZLF 这里可以设置图标可显示
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.show();
    }

    @Override
    public void showTrashPopupMenu(View view, SNote note) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater()
                .inflate(R.menu.menu_notes_trash_more, popup.getMenu());
        popup.setOnMenuItemClickListener((item -> mainPresenter.onPopupMenuClick(item.getItemId(), note)));

        //ZLF 这里可以设置图标可显示
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper
                            .getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.show();
    }

    @Override
    public void moveTaskToBack() {
        super.moveTaskToBack(true);
    }

    @Override
    public void reCreate() {
        super.recreate();
    }

    @Override
    public void showSnackbar(int message) {
        SnackbarUtils.show(fab, message);
    }

    @Override
    public void showGoBindEverNoteSnackbar(int message, int action) {
        SnackbarUtils.showAction(fab, message
                , action, mainPresenter);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }
    // 2 视图初始化
    @Override
    protected int getLayoutView() {
        return R.layout.activity_notemain;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
  //      mDrawerToggle.syncState();
        if (toolbar != null){
           // toolbar.setNavigationOnClickListener((view) -> mainPresenter.OnNavigationOnClick());
        }
    }


    // 2 初始化SearchView
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        //searchItem.expandActionView(); // 默认展开搜索框
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        ComponentName componentName = getComponentName();

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(componentName));
        searchView.setQueryHint(getString(R.string.search_note));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                recyclerAdapter.getFilter().filter(s); // 2 文字改变就即时处理搜索
                return true;
            }
        });

        // 2 监听搜索框是否打开，用于隐藏FloatingActionBar和禁用下拉刷新
        MenuItemCompat.setOnActionExpandListener(searchItem, mainPresenter);
        return true;
    }

    // 2 处理菜单事件 第一个if用于判断是否点击打开抽屉开关按钮，
    // 第二个才传入MainPresenter进行菜单的处理，
    // 返回true当然就表示消耗此事件。
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        if(mDrawerToggle.onOptionsItemSelected(item)) {
//            return true;
//        }
//        if (mainPresenter.onOptionsItemSelected(item.getItemId())){
//            return true;
//        }

        /**
         * 2 添加的代码
         */
        mainPresenter.onOptionsItemSelected(item.getItemId());
        //////////////////////////////////////////////////////
        return super.onOptionsItemSelected(item);
    }

    // 2  处理实体按键事件 返回值注意的是先处理传入MainPresentor里面方案，
    // 有代码自左至右运行顺序，如果不满足则按父类方法处理，
    // 这样写简直精妙，避免了多重if判断。
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mainPresenter.onKeyDown(keyCode) || super.onKeyDown(keyCode, event);
    }

    @OnClick(R.id.fab)
    public void newNote(View view){
        mainPresenter.newNote();
    }


    // 2  删除对话框的显示 用lambda表达式的写法真的很好，
    // 不过笔者很好奇作者如何这么顺畅地写出lambda表达式，
    // 毕竟没有智能提示。
    @Override
    public void showDeleteForeverDialog(final SNote note){
        AlertDialog.Builder builder = DialogUtils.makeDialogBuilder(this);
        builder.setTitle(R.string.delete_tip);
        DialogInterface.OnClickListener listener = (DialogInterface dialog, int which) ->
            mainPresenter.onDeleteForeverDialogClick(note, which);
        builder.setPositiveButton(R.string.sure, listener);
        builder.setNegativeButton(R.string.cancel, listener);
        builder.show();
    }

    @Override
    public void finishView() {
        finish();
    }
}
