package com.tsunami.timeapp.mvp.presenters.impl;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;


import com.tsunami.timeapp.Activity3.Alarm.preferences.AlarmPreferencesActivity;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.UserActivity;
import com.tsunami.timeapp.injector.ContextLifeCycle;
import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.mvp.presenters.Presenter;
import com.tsunami.timeapp.mvp.views.View;
import com.tsunami.timeapp.mvp.views.impl.MainView;
import com.tsunami.timeapp.ui.AboutActivity;
import com.tsunami.timeapp.ui.EditButtonActivity;
import com.tsunami.timeapp.ui.NoteActivity;
import com.tsunami.timeapp.ui.SettingActivity;
import com.tsunami.timeapp.utils.EverNoteUtils;
import com.tsunami.timeapp.utils.NotesLog;
import com.tsunami.timeapp.utils.ObservableUtils;
import com.tsunami.timeapp.utils.PreferenceUtils;

import net.tsz.afinal.FinalDb;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author shenxiaoming
 */
public class TrashPresenter implements Presenter, android.view.View.OnClickListener, SwipeRefreshLayout.OnRefreshListener,
        PopupMenu.OnMenuItemClickListener, MenuItemCompat.OnActionExpandListener {
    private MainView view;
    private final Context mContext;
    private FinalDb mFinalDb;
    private EverNoteUtils mEverNoteUtils;
    private ObservableUtils mObservableUtils;
    private PreferenceUtils mPreferenceUtils;
    private List<String> drawerList;
    //private SNote.NoteType mCurrentNoteTypePage = SNote.NoteType.getDefault();
    /**
     *  2 添加的代码
     */
    private SNote.NoteType mCurrentNoteTypePage = SNote.NoteType.TRASH;
    ///////////////////////////////////////////////////////////////////////
    private boolean isCardItemLayout = true;
    private boolean isRightHandMode = false;
    private final String  CURRENT_NOTE_TYPE_KEY = "CURRENT_NOTE_TYPE_KEY";
    @Inject
    public TrashPresenter(@ContextLifeCycle ("Activity")Context context, FinalDb finalDb, PreferenceUtils preferenceUtils,
                         ObservableUtils mObservableUtils, EverNoteUtils everNoteUtils) {
        this.mContext = context;
        this.mFinalDb = finalDb;
        this.mPreferenceUtils = preferenceUtils;
        this.mEverNoteUtils = everNoteUtils;
        this.mObservableUtils = mObservableUtils;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 2 再onCreate中恢复信息
        if (savedInstanceState != null){
            int value = savedInstanceState.getInt(CURRENT_NOTE_TYPE_KEY);
            mCurrentNoteTypePage = SNote.NoteType.mapValueToStatus(value);

            /**
             * 2 添加的代码
             */
            //mCurrentNoteTypePage = SNote.NoteType.mapValueToStatus(1);
            //switchNoteTypePage(mCurrentNoteTypePage);
            //view.setDrawerItemChecked(1);
            view.showFab(false);
            view.enableSwipeRefreshLayout(false);
            ///////////////////////////////////////////////////////////////////
        }


        /**
         * 2 添加的代码
         */
        //mCurrentNoteTypePage = SNote.NoteType.mapValueToStatus(1);
        //switchNoteTypePage(mCurrentNoteTypePage);
        //view.setDrawerItemChecked(1);
        view.showFab(false);
        view.enableSwipeRefreshLayout(false);
        ///////////////////////////////////////////////////////////////////


        // 2  初始化视图
        // 调用所有继承自MainView接口的Activity的初始化Toolbar方法以初始化
        view.initToolbar();


        // initDrawer();
        // 2 设置抽屉的方向
        // initMenuGravity();
        // 2 设置RecyclerView线性或网格排列
        initItemLayoutManager();
        initRecyclerView();
        // 2 使用到了 EventBus 库 在onCreate方法中进行注册:
        EventBus.getDefault().register(this);
    }

    // 2 信息的保存: 笔者猜测是用于保存当前界面，保存在"Normal"模式还是"回收站"模式
    // 想要在Activity崩溃的时候，其实主要是屏幕发生旋转时保存信息，
    // 以便重启Activity后能够恢复信息，需要怎么做呢？很简单，
    // 重写 onSaveInstanceState(Bundle outState) 方法，
    // 在内部实现信息的保存，并在onCreate方法中对信息进行恢复即可
    public void onSaveInstanceState(Bundle outState){
        outState.putInt(CURRENT_NOTE_TYPE_KEY, mCurrentNoteTypePage.getValue());
    }

    @Override
    public void onStart() {
    }


    // 2 主要是对已加载配置文件并应用设置，这也告诉我们，
    // 通常加载配置文件放在依赖注入处，也就是onCreate中，而应用设置放在onResume中。
    @Override
    public void onResume() {
        if (isRightHandMode != mPreferenceUtils.getBooleanParam(mContext
                .getString(R.string.right_hand_mode_key))){
            isRightHandMode = !isRightHandMode;
            if (isRightHandMode){
                view.setMenuGravity(Gravity.END);
            }else{
                view.setMenuGravity(Gravity.START);
            }
        }
        if (isCardItemLayout != mPreferenceUtils.getBooleanParam(mContext
                .getString(R.string.card_note_item_layout_key), true)){
            // 2 最终通过recyclerView.setLayoutManager(manager)设置显示方式
            switchItemLayoutManager(!isCardItemLayout);
        }
    }

    @Override
    public void onPause() {
    }

//    @Override
//    public void onStop() {
//        view.closeDrawer();
//    }

    @Override
    public void onDestroy() {
        // 2 在onDestroy方法中进行反注册:
        EventBus.getDefault().unregister(this);
    }

    // 2 如果按的不是返回键，则返回false交给系统处理，
    // 如果按的是返回键且抽屉未关，则关闭抽屉，
    // 否则调用 super.moveTaskToBack(true) 手动隐藏Activity，
    // 保持栈结构，将任务移到后台，不会调用onDestroy方法。
    public boolean onKeyDown(int keyCode){
        if (keyCode == KeyEvent.KEYCODE_BACK){

            view.finishView();
            return true;
        }
        return false;
    }

    @Override
    public void attachView(View view) {
        this.view = (MainView)view;
    }

    // 2 回收站点击menu子项
    public boolean onOptionsItemSelected(int id){
        switch (id){
          //  case R.id.setting: startSettingActivity();return true;
           /* case R.id.sync:
                if (view.isRefreshing()){
                    return true;
                }
                view.startRefresh();
                onRefresh();
                return true;
            case R.id.about: startAboutActivity();return true;*/

            // 2 添加的代码
            case android.R.id.home:

                view.finishView();
                // 2 添加的代码
                return true;
        }
        return false;
    }

    @Override
    public void onClick(android.view.View v) {
        startSettingActivity();
    }

    @Override
    public void onRefresh() {
        sync(EverNoteUtils.SyncType.ALL, false);
    }


    // 2 初始化抽屉 这里值得留意的是在strings.xml中定义数组: <array name="drawer_content">
    private void initDrawer(){
        drawerList = Arrays.asList(mContext.getResources()
                .getStringArray(R.array.drawer_content));
        // view.initDrawerView(drawerList);
        view.setDrawerItemChecked(mCurrentNoteTypePage.getValue());
        view.setToolbarTitle(drawerList.get(mCurrentNoteTypePage.getValue()));
    }

    // 2 设置抽屉的方向，由MVP模式，具体的实现放在MainActivity中
    private void initMenuGravity(){
        boolean end = mPreferenceUtils.getBooleanParam(mContext.getString(R.string.right_hand_mode_key));
        if (end){
            view.setMenuGravity(Gravity.END);
        }else {
            view.setMenuGravity(Gravity.START);
        }
        isRightHandMode = end;
    }


    // 2 点击drawer项目的响应
    public void onDrawerItemSelect(int position){
        mCurrentNoteTypePage = SNote.NoteType.mapValueToStatus(position);
        switchNoteTypePage(mCurrentNoteTypePage);
        view.setDrawerItemChecked(position);
        switch (mCurrentNoteTypePage){
            case TRASH:
                view.showFab(false);view.enableSwipeRefreshLayout(false);break;
            default:
                view.showFab(true);view.enableSwipeRefreshLayout(true);break;
        }
    }

    private boolean onMenuItemActionExpand(){
        view.enableSwipeRefreshLayout(false);
        view.showFab(false);
        return true;
    }

    private boolean onMenuItemActionCollapse(){
        if (mCurrentNoteTypePage != SNote.NoteType.TRASH){
            view.enableSwipeRefreshLayout(true);
            view.showFab(true);
        }
        return true;
    }

    @Override
    public boolean onMenuItemActionExpand(MenuItem item) {
        return onMenuItemActionExpand();
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem item) {
        return onMenuItemActionCollapse();
    }

//    public void OnNavigationOnClick(){
//        view.openOrCloseDrawer();
//    }

    private void refreshNoteTypePage(){
        switchNoteTypePage(mCurrentNoteTypePage);
    }

    public void switchNoteTypePage(SNote.NoteType type){
        view.showProgressWheel(true);
        //TODO 分页，避免数据过多时加载太慢
        mObservableUtils.getLocalNotesByType(mFinalDb, type.getValue())
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((notes) -> {
                    view.switchNoteTypePage(notes);
                    //  view.closeDrawer();
                    view.showProgressWheel(false);
                }, (e) -> {
                    e.printStackTrace();
                    view.showProgressWheel(false);
                });
    }





    public void onDrawerOpened(){
        view.setToolbarTitle("私密空间");
    }

    public void onDrawerClosed(){
        view.setToolbarTitle(drawerList.get(mCurrentNoteTypePage.getValue()));
    }

    public void initItemLayoutManager(){
        boolean card = mPreferenceUtils.getBooleanParam(mContext.getString(R.string.card_note_item_layout_key), true);
        switchItemLayoutManager(card);
    }

    private void switchItemLayoutManager(boolean card){
        if (card){
            view.setLayoutManager(new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL));
        }else {
            view.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        }
        isCardItemLayout = card;
    }

    // 2 加载笔记
    public void initRecyclerView(){
        view.showProgressWheel(true);
        mObservableUtils.getLocalNotesByType(mFinalDb, mCurrentNoteTypePage.getValue())

                // 2 指定线程 此处的代码用于指定观察者和被观察者所在的线程，
                // 由于加载笔记属于重大任务，所以指定在computation计算线程，
                // 另外subscribeOn可指定的参数如图:
//        在RxJava 中，Scheduler ——调度器，相当于线程控制器，
//        RxJava 通过它来指定每一段代码应该运行在什么样的线程。
//        RxJava 已经内置了几个 Scheduler ，它们已经适合大多数的使用场景:
//        Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
//        Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
//        Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler。
//        行为模式和 newThread() 差不多，区别在于 io() 的内部实现是是用一个无数量上限的线程池，可以重用空闲的线程，
//        因此多数情况下 io() 比 newThread() 更有效率。不要把计算工作放在 io() 中，可以避免创建不必要的线程。
//        Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算
//        ，即不会被 I/O 等操作限制性能的操作，例如图形的计算。这个 Scheduler 使用的固定的线程池，大小为 CPU 核数。
//        不要把 I/O 操作放在 computation() 中，否则 I/O 操作的等待时间会浪费 CPU。
//        另外， Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。

                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((notes) -> {
                    view.initRecyclerView(notes);
                    view.showProgressWheel(false);
                }, (e) -> {
                    e.printStackTrace();
                    view.showProgressWheel(false);
                });
    }


    // 2 启动其它Activity
    private void startNoteActivity(int type, SNote value){
        Intent intent = new Intent(mContext, NoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(NotePresenter.OPERATE_NOTE_TYPE_KEY, type);

        // 2 发送消息
        EventBus.getDefault().postSticky(value);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    public void startSettingActivity(){
        Intent intent = new Intent(mContext, SettingActivity.class);
        mContext.startActivity(intent);
    }

    public void startAboutActivity(){
        Intent intent = new Intent(mContext, AboutActivity.class);
        mContext.startActivity(intent);
    }

    /**
     * 2 添加的代码
     */
    public void startEditButtonActivity(){
        Intent intent = new Intent(mContext,EditButtonActivity.class);
        mContext.startActivity(intent);
    }

    public void startUserActivity(){
        Intent intent = new Intent(mContext,UserActivity.class);
        mContext.startActivity(intent);
    }
    //////////////////////////////////////////////////////////


    public void onRecyclerViewItemClick(int position, SNote value){
        if (mCurrentNoteTypePage == SNote.NoteType.TRASH){
            return;
        }
        startNoteActivity(NotePresenter.VIEW_NOTE_MODE, value);
    }

    public void showPopMenu(android.view.View v, int position, SNote value){
        if (mCurrentNoteTypePage == SNote.NoteType.TRASH){
            view.showTrashPopupMenu(v, value);
        }else {
            view.showNormalPopupMenu(v, value);
        }
    }

    private void moveToTrash(SNote note){
        if (note == null)
            return;
        note.setType(SNote.NoteType.TRASH);
        note.setStatus(SNote.Status.NEED_REMOVE);
        mFinalDb.update(note);
        view.removeNote(note);
        pushNote(note);
    }

    private void recoverNote(SNote note){
        if (note == null)
            return;
        note.setType(SNote.NoteType.NORMAL);
        note.setStatus(SNote.Status.NEED_PUSH);
        mFinalDb.update(note);
        view.removeNote(note);
        pushNote(note);
    }

    public void onDeleteForeverDialogClick(SNote note, int which){
        if (which == Dialog.BUTTON_POSITIVE){
            mFinalDb.delete(note);
            view.removeNote(note);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        return false;
    }

    public boolean onPopupMenuClick(int id, SNote note){
        switch (id){
            case R.id.edit:
                startNoteActivity(NotePresenter.EDIT_NOTE_MODE, note);
                break;
            case R.id.add_alarm:
                startAlarmPreferenceActivity();
                break;
//            case R.id.add_date:
//
//                break;

          /*  case R.id.note_myself_delete:
                view.showDeleteForeverDialog(note);
                break;*/
            case R.id.recover:
                recoverNote(note);
                break;
            default:
                break;
        }
        return true;
    }

    private void startAlarmPreferenceActivity() {
        Intent intent = new Intent(mContext, AlarmPreferencesActivity.class);
        mContext.startActivity(intent);
    }

    public void newNote(){
        SNote note = new SNote();
        note.setType(mCurrentNoteTypePage);
        startNoteActivity(NotePresenter.CREATE_NOTE_MODE, note);
    }

    private void pushNote(SNote note){
        mObservableUtils.pushNote(mEverNoteUtils, note)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result -> {
                    if (!result)
                        NotesLog.e("push note fail");
                }), (e) ->{
                    e.printStackTrace();
                    NotesLog.e("push note fail");
                });
    }

    // 2 同步笔记
    private void sync(EverNoteUtils.SyncType type, boolean silence){
        //mEverNoteUtils.sync();
        mObservableUtils.sync(mEverNoteUtils, type)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result -> {
                    if (!silence)
                        // 2 在接受消息的Activity中重写事件接收方法， 消息接收——同步笔记
                        // 如onEventMainThread:onEvent:如果使用onEvent作为订阅函数，那么该事件在哪个线程发布出来的，
//                        onEvent就会在这个线程中运行，也就是说发布事件和接收事件线程在同一个线程。
//                    使用这个方法时，在onEvent方法中不能执行耗时操作，如果执行耗时操作容易导致事件分发延迟。
//                    onEventMainThread:如果使用onEventMainThread作为订阅函数，那么不论事件是在哪个线程中发布出来的，
//                    onEventMainThread都会在UI线程中执行，接收事件就会在UI线程中运行，这个在Android中是非常有用的，
//                    因为在Android中只能在UI线程中跟新UI，所以在onEvnetMainThread方法中是不能执行耗时操作的。
//                    onEventBackground:如果使用onEventBackgrond作为订阅函数，那么如果事件是在UI线程中发布出来的，
//                    那么onEventBackground就会在子线程中运行，如果事件本来就是子线程中发布出来的，那么onEventBackground函数直接在该子线程中执行。
//                    onEventAsync：使用这个函数作为订阅函数，那么无论事件在哪个线程发布，都会创建新的子线程在执行onEventAsync.
                        onEventMainThread(result);
                }));
    }

    // 2 消息接收——同步笔记
    public void onEventMainThread(EverNoteUtils.SyncResult result){
        if (result != EverNoteUtils.SyncResult.START)
            view.stopRefresh();
        switch (result){
            case ERROR_NOT_LOGIN: view.showGoBindEverNoteSnackbar(R.string.unbind_ever_note_tip, R.string.go_bind);break;
            case ERROR_EXPUNGE: view.showSnackbar(R.string.expunge_error);break;
            case ERROR_DELETE: view.showSnackbar(R.string.delete_error);break;
            case ERROR_FREQUENT_API: view.showSnackbar(R.string.frequent_api_tip);break;
            case ERROR_AUTH_EXPIRED: view.showSnackbar(R.string.error_auth_expired_tip);break;
            case ERROR_PERMISSION_DENIED: view.showSnackbar(R.string.error_permission_deny);break;
            case ERROR_QUOTA_EXCEEDED: view.showSnackbar(R.string.error_permission_deny);break;
            case ERROR_OTHER: view.showSnackbar(R.string.sync_fail);break;
            case START: break;
            case SUCCESS_SILENCE: break;
            case SUCCESS:view.showSnackbar(R.string.sync_success);refreshNoteTypePage();break;
        }
    }

    public void onEventMainThread(NotifyEvent event){
        switch (event.getType()){
            case NotifyEvent.REFRESH_LIST:
                view.startRefresh();
                onRefresh();
                break;
            case NotifyEvent.CREATE_NOTE:
                if (event.getData() instanceof SNote){
                    SNote note = (SNote)event.getData();
                    view.addNote(note);
                    view.scrollRecyclerViewToTop();
                    pushNote(note);
                }
                break;
            case NotifyEvent.UPDATE_NOTE:
                if (event.getData() instanceof SNote){
                    SNote note = (SNote)event.getData();
                    view.updateNote(note);
                    // 2 可以使RecyclerView平滑地滚动到指定位置
                    view.scrollRecyclerViewToTop();
                    pushNote(note);
                }
                break;
            case NotifyEvent.CHANGE_THEME:

                // 2 重建Activity
                view.reCreate();
                break;
        }
    }

    public static class NotifyEvent<T>{
        public static final int REFRESH_LIST = 0;
        public static final int CREATE_NOTE = 1;
        public static final int UPDATE_NOTE = 2;
        public static final int CHANGE_THEME = 3;
        public static final int CHANGE_ITEM_LAYOUT = 4;
        public static final int CHANGE_MENU_GRAVITY = 5;
        private int type;
        private T data;
        @IntDef({REFRESH_LIST, CREATE_NOTE, UPDATE_NOTE, CHANGE_THEME,
                CHANGE_ITEM_LAYOUT, CHANGE_MENU_GRAVITY})
        public @interface Type {
        }

        public @Type int getType() {
            return type;
        }

        public void setType(@Type int type) {
            this.type = type;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}

