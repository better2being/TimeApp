package com.tsunami.timeapp.mvp.presenters.impl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.tsunami.timeapp.Activity3.Alarm.preferences.AlarmPreferencesActivity;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity1.WeatherDetail;
import com.tsunami.timeapp.activity2.Sampler;
import com.tsunami.timeapp.activity2.SublimePickerFragment;
import com.tsunami.timeapp.injector.ContextLifeCycle;
import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.mvp.presenters.Presenter;
import com.tsunami.timeapp.mvp.views.View;
import com.tsunami.timeapp.mvp.views.impl.MainView;
import com.tsunami.timeapp.mvp.views.impl.NoteView;
import com.tsunami.timeapp.ui.MainActivity;
import com.tsunami.timeapp.util.FileStreamUtil;
import com.tsunami.timeapp.utils.TimeUtils;
import com.xys.shortcut_lib.ShortcutActivity;
import com.xys.shortcut_lib.ShortcutSuperUtils;
import com.xys.shortcut_lib.ShortcutUtils;

import net.tsz.afinal.FinalDb;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

/**
 * @author shenxiaoming
 */
public class WeekViewNotePresenter implements Presenter, android.view.View.OnFocusChangeListener,
        DialogInterface.OnClickListener, TextWatcher {
    private NoteView view;
    private MainView mainview;
    private final Context mContext;
    private FinalDb mFinalDb;
    private SNote note;
    private int operateMode = 0;
    private MainPresenter.NotifyEvent<SNote> event;
    public final static String OPERATE_NOTE_TYPE_KEY = "OPERATE_NOTE_TYPE_KEY";
    // 添加的代码
    // 快捷方式名
    private String mShortcutName = "笔记快捷键";
    ////////////////////////
    public final static int VIEW_NOTE_MODE = 0x00;
    public final static int EDIT_NOTE_MODE = 0x01;
    public final static int CREATE_NOTE_MODE = 0x02;
    @Inject
    public WeekViewNotePresenter(@ContextLifeCycle("Activity") Context mContext, FinalDb mFinalDb) {
        this.mContext = mContext;
        this.mFinalDb = mFinalDb;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate( savedInstanceState );
        EventBus.getDefault().registerSticky(this);
    }

    @Override
    public void onResume() {

    }

    public void onPrepareOptionsMenu(){
        view.setDoneMenuItemVisible(false);
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.done:
                saveNote();
                view.finishView();
//                Log.e("note","note.finishView");
//                mainview.addNote(note);
//                Log.e("note","note.finishView");
//                mainview.scrollRecyclerViewToTop();
                Log.e("note","note.finishView");
                // 2 添加的代码
                EventBus.getDefault().post(event);
                /////////////////////////
                return true;
            case R.id.note_clock:
                saveNote();
                startAlarmPreferenceActivity();
                return true;
            case android.R.id.home:
                view.hideKeyBoard();
                if (view.isDoneMenuItemVisible()){
                    view.showNotSaveNoteDialog();
                    return true;
                }
                view.finishView();
                //startNoteMainActivity();
                // 2 添加的代码
                return true;
//            case R.id.setScheduleStartAndEndTime:
//                view.hideKeyBoard();
//                saveNote();
//                startTimePickerSamplerActivity();
//                //view.finishView();
////                state=0;
////                Open(state);
//                return true;
            case R.id.weatherWarning:
                view.hideKeyBoard();
                startWeatherDetialActivity();
                return true;
            case R.id.sendToDesk:
                view.hideKeyBoard();
                saveNote();
                sendNoteToDesk();
                return true;

            case R.id.editnote_more:
                view.hideKeyBoard();
                return true;

            ///////////////
            default:
                return false;
        }
    }

    private void startWeatherDetialActivity() {
        Intent intent = new Intent(mContext, WeatherDetail.class);
        if (!FileStreamUtil.load(mContext).isEmpty()) {
            // 获取当前网络状态
            ConnectivityManager connectivityManager = (ConnectivityManager)
                    mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo == null) {
                // 提醒当前无网络
                Toast.makeText(mContext, "请连接移动数据", Toast.LENGTH_SHORT).show();
                return;
            }
            LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
            // 获取network的位置提供器
            List<String> providerList = locationManager.getProviders(true);
            if (!providerList.contains(LocationManager.NETWORK_PROVIDER)) {
                Log.e("eeeeeeeee", "return");
                Toast.makeText(mContext, "请打开位置服务", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Log.e("eeeeeeeee", "start");
        mContext.startActivity(intent);
    }

    private void startAlarmPreferenceActivity() {
        Intent intent = new Intent(mContext, AlarmPreferencesActivity.class);
        mContext.startActivity(intent);
    }

    // 2 添加的代码
    public void sendNoteToDesk() {

        // 创建前判断是否存在
        if (!ShortcutSuperUtils.isShortCutExist(mContext, note.getLabel(), getShortCutIntent())) {
            ShortcutUtils.addShortcut(mContext, getShortCutIntent(), note.getLabel(), false,
                    BitmapFactory.decodeResource(mContext.getResources(), R.drawable.fab_c));
//            LayoutInflater inflater = (LayoutInflater)getSystemService(LAYOUT_INFLATER_SERVICE);
//            android.view.View toastRoot = inflater.inflate(R.layout.my_toast, null);
//            Toast toast=new Toast(mContext);
//            toast.setView(toastRoot);
//            TextView tv=(TextView)toastRoot.findViewById(R.id.TextViewInfo);
//            tv.setText("说明：这是一个自定义边框和底色的提示框。");
//            toast.show();
            Toast.makeText(mContext,"正在创建快捷方式...",Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(mContext, "已经在桌面了", Toast.LENGTH_SHORT).show();
        }

    }

    private Intent getShortCutIntent() {
        // 使用MAIN，可以避免部分手机(比如华为、HTC部分机型)删除应用时无法删除快捷方式的问题
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.putExtra("noteLabel",note.getLabel());
        intent.putExtra("noteContent",note.getContent());
        intent.putExtra("noteTypedTime",getOprTimeLineText(note));
//        // 2 添加的代码
//        Toast.makeText(mContext,"该笔记的标题为:" + note.getLabel()
//                + ",该笔记的内容为:" +note.getContent()
//                + ",该笔记的创建时间为:" +note.getCreateTime()
//                + ",该笔记的更新时间为:" + note.getLastOprTime()
//                + ",格式化时间:" + getOprTimeLineText(note),Toast.LENGTH_LONG).show();
//        //////////////////////////////////////////////
        intent.setClass(mContext, ShortcutActivity.class);
        return intent;
    }

    public Pair<Boolean, SublimeOptions> setOption(int dort) {

        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;
        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;
        options.setPickerToShow(dort == 0 ? SublimeOptions.Picker.DATE_PICKER : SublimeOptions.Picker.TIME_PICKER);
        options.setDisplayOptions(displayOptions);
        options.setCanPickDateRange(false);
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    public void Open(int dort) {

        SublimePickerFragment pickerFrag = new SublimePickerFragment();
        pickerFrag.setCallback(mFragmentCallback);

        // Options
        Pair<Boolean, SublimeOptions> optionsPair = setOption(dort);

        if (!optionsPair.first) { // If options are not valid
            Toast.makeText(mContext, "No pickers activated",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Valid options
        Bundle bundle = new Bundle();
        bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
        pickerFrag.setArguments(bundle);

        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        //pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
    }

    public int state = 0;
    SublimePickerFragment.Callback mFragmentCallback = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {
            //rlDateTimeRecurrenceInfo.setVisibility(View.GONE);
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                            int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {
            System.out.println(hourOfDay);
            state++;
            if (state == 1) {
                Toast.makeText(mContext, "StartTime", Toast.LENGTH_SHORT).show();
            }else if (state==2){
                Toast.makeText(mContext, "EndTime", Toast.LENGTH_SHORT).show();
            }
            if (state <= 2) {
                Open(state);
            }

        }
    };
    public void startTimePickerSamplerActivity(){
        Intent intent = new Intent(mContext,Sampler.class);
        //intent.putExtra("noteLabelAndContent","标题:" + note.getLabel() + '\n' + "内容:" + note.getContent() + '\n' + "ID:" + note.getId());
        intent.putExtra("noteLabel",note.getLabel());
        intent.putExtra("noteContent",note.getContent());
        intent.putExtra("noteID",note.getId());
        intent.putExtra("noteTypedTime",getOprTimeLineText(note));
        mContext.startActivity(intent);
    }
    ////////////////////////////////

    // 2 添加的代码
    public void startNoteMainActivity(){
        Intent intent = new Intent(mContext,MainActivity.class);
        mContext.startActivity(intent);
    }
    ////////////////////////////////

    @Override
    public void onStart() {

    }

    @Override
    public void onPause() {

    }

    //@Override
    public void onStop() {
        view.hideKeyBoard();

    }


    // 2 在Activity销毁时，使用了EventBus将最后的消息发送出去，
    // 消息包含新建的笔记的类型(新建类型或修改类型)，
    // 以及笔记的具体内容(笔记对象)。这时MainPresenter中的EventBus将会收到消息，
    // 并且将收到的消息放到主Activity中进行展示
    @Override
    public void onDestroy() {
//        if (event != null){
//            EventBus.getDefault().post(event);
//            Log.e("note","EventBus.getDefault().post(event);");
//        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void attachView(View v) {
        this.view  = (NoteView)v;
    }

    public void attachIntent(Intent intent){
        parseIntent(intent);
    }

    public boolean onKeyDown(int keyCode){
        if (keyCode == KeyEvent.KEYCODE_BACK){
            view.hideKeyBoard();
            if (view.isDoneMenuItemVisible()){
                view.showNotSaveNoteDialog();
                return true;
            }
        }
        return false;
    }

    private void parseIntent(Intent intent){
        if (intent != null && intent.getExtras() != null){
            operateMode = intent.getExtras().getInt(OPERATE_NOTE_TYPE_KEY, 0);
        }
    }

    public void onEventMainThread(SNote note) {
        this.note = note;
        initToolbar();
        initEditText();
        initTextView();
    }

    private void initToolbar(){
        view.setToolbarTitle(R.string.view_note);
        switch (operateMode){
            case CREATE_NOTE_MODE: view.setToolbarTitle(R.string.new_note);break;
            case EDIT_NOTE_MODE: view.setToolbarTitle(R.string.edit_note);break;
            case VIEW_NOTE_MODE: view.setToolbarTitle(R.string.view_note);break;
            default:break;
        }
    }

    private void initEditText(){
        switch (operateMode){
            case EDIT_NOTE_MODE: view.initViewOnEditMode(note);break;
            case VIEW_NOTE_MODE: view.initViewOnViewMode(note);break;
            default:view.initViewOnCreateMode(note);break;
        }
    }

    private void initTextView(){
        view.setOperateTimeLineTextView(getOprTimeLineText(note));
    }


    // 2 aFinalDb中的方法
    // boolean saveBindId(Object entity) 可以直接保存对象到数据库。
    private void saveNote(){
        view.hideKeyBoard();
        if (TextUtils.isEmpty(view.getLabelText())){
            note.setLabel(mContext.getString(R.string.default_label));
        }else {
            note.setLabel(view.getLabelText());
            Log.e("note","note.setLabel");
        }
        note.setContent(view.getContentText());
        Log.e("note","note.setContent");
        note.setLastOprTime(TimeUtils.getCurrentTimeInLong());
        Log.e("note","note.setLastOprTime");
//        // 2 添加的代码
//        Toast.makeText(mContext,"该笔记的标题为:" + note.getLabel()
//                + ",该笔记的内容为:" +note.getContent()
//                + ",该笔记的创建时间为:" +note.getCreateTime()
//                + ",该笔记的ID为:" + note.getId(),Toast.LENGTH_LONG).show();
//        //////////////////////////////////////////////
        note.setStatus(SNote.Status.NEED_PUSH.getValue());
        Log.e("note","note.setStatus");
        event = new MainPresenter.NotifyEvent<>();
        Log.e("note","note.setStatus");
        switch (operateMode){
            case CREATE_NOTE_MODE:
                note.setCreateTime(TimeUtils.getCurrentTimeInLong());
                Log.e("note","note.setCreateTime");
                event.setType(MainPresenter.NotifyEvent.CREATE_NOTE);
                Log.e("note","note.setType");
                mFinalDb.saveBindId(note);
                Log.e("note","note.saveBindId");
                break;
            default:
                event.setType(MainPresenter.NotifyEvent.UPDATE_NOTE);
                Log.e("note","note.setType");
                mFinalDb.update(note);
                Log.e("note","note.update");
                break;
        }
        event.setData(note);
        Log.e("note","note.setData");
        //view.finishView();
    }

    // 2 时间的格式化 传入笔记对象，如果没有创建时间
    // (笔者猜想或许因为从备份恢复笔记内容或是其它原因会导致笔记创建的时间消失)
    // 就只显示最后一次的修改时间，否则就都进行显示，这里用到了TimeUtils。
    private String getOprTimeLineText(SNote note){
        if (note == null || note.getLastOprTime() == 0)
            return "";
        String create = mContext.getString(R.string.create);
        String edit = mContext.getString(R.string.last_update);
        StringBuilder sb = new StringBuilder();
        if (note.getLastOprTime() <= note.getCreateTime() || note.getCreateTime() == 0){
            sb.append(mContext.getString(R.string.note_log_text, create, TimeUtils.getTime(note.getLastOprTime())));
            return sb.toString();
        }
        sb.append(mContext.getString(R.string.note_log_text, edit, TimeUtils.getTime(note.getLastOprTime())));
        sb.append("\n");
        sb.append(mContext.getString(R.string.note_log_text, create, TimeUtils.getTime(note.getCreateTime())));
        return sb.toString();
    }

    @Override
    public void onFocusChange(android.view.View v, boolean hasFocus) {
        if (hasFocus){
            view.setToolbarTitle(R.string.edit_note);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }


    // 2 对于保存按钮的自动隐现，实现思路就是判断文本是否改变即可:
    // 过要注意的是要过滤掉转义字符，此处用的正则表达式进行替换过滤。
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (view.isDoneMenuItemNull())
            return;
        String labelSrc = view.getLabelText();
        String contentSrc = view.getContentText();
        //String label = labelSrc.replaceAll("\\s*|\t|\r|\n", "");
        String content = contentSrc.replaceAll("\\s*|\t|\r|\n", "");
        if (!TextUtils.isEmpty(content)){
            if (TextUtils.equals(labelSrc, note.getLabel()) && TextUtils.equals(contentSrc, note.getContent())){
                view.setDoneMenuItemVisible(false);
                return;
            }
            view.setDoneMenuItemVisible(true);
        }else{
            view.setDoneMenuItemVisible(false);
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch (which){
            case DialogInterface.BUTTON_POSITIVE:
                saveNote();
                view.finishView();
                // 2 添加的代码
                EventBus.getDefault().post(event);
                /////////////////////////
                break;
            case DialogInterface.BUTTON_NEGATIVE:
                view.finishView();
                break;
            default:
                break;
        }
    }


}

