package com.tsunami.timeapp.activity1;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tsunami.timeapp.App;

import com.tsunami.timeapp.activity2.SublimePickerFragment;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.injector.component.DaggerActivityComponent;
import com.tsunami.timeapp.injector.module.ActivityModule;
import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.model1.Schedule;
import com.tsunami.timeapp.model1.User;
import com.tsunami.timeapp.mvp.presenters.impl.NotePresenter;
import com.tsunami.timeapp.mvp.views.impl.NoteView;
import com.tsunami.timeapp.ui.BaseActivity;
import com.tsunami.timeapp.utils.DialogUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.Bind;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.DialogFragment;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.tsunami.timeapp.JsonService;
import com.tsunami.timeapp.R;
import java.util.Calendar;
import com.tsunami.timeapp.activity2.weekview.NoJsonServiceBasicActivity;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;
/**
 * @author shenxiaoming
 */

public class SamplerMy extends BaseActivity implements NoteView, View.OnLayoutChangeListener {
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.label_edit_text) MaterialEditText labelEditText;
    @Bind(R.id.content_edit_text) MaterialEditText contentEditText;
    @Bind(R.id.opr_time_line_text) TextView oprTimeLineTextView;
    @Inject NotePresenter notePresenter;
    private MenuItem doneMenuItem;

    //  1  监听键盘
    //Activity最外层的Layout视图
    private View activityRootView;
    //屏幕高度
    private int screenHeight = 0;
    //软件盘弹起后所占高度阀值
    private int keyHeight = 0;

    private final int INVALID_VAL = -1;

    // 2 判断是否点击了设置开始时间按钮
    private boolean clickLeftLaunchPicker = true;

    private JsonService jsonService=new JsonService();
    //2  进入选择时间的按钮
    TextView ivLeftLaunchPicker;
    TextView ivRightLaunchPicker;

    // 2 选择的开始和结束时间
    private int startYear,startMonth,startDayOfMonth,startHour,startMinute;
    private int endHour,endMinute;

    // Labels
    TextView tvPickerToShow, tvActivatedPickers;
    ScrollView svMainContainer;

    // Views to display the chosen Date, Time & Recurrence options
    TextView  tvRecurrenceOption, tvRecurrenceRule;
    RelativeLayout rlDateTimeRecurrenceInfo;

    // Chosen values
    SelectedDate mSelectedDate;
    int mHour, mMinute;
    String mRecurrenceOption, mRecurrenceRule;

    SublimePickerFragment.Callback mFragmentCallback = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {
//            rlDateTimeRecurrenceInfo.setVisibility(View.GONE);
        }

        @Override
        public void onDateTimeRecurrenceSet(SelectedDate selectedDate,
                                            int hourOfDay, int minute,
                                            SublimeRecurrencePicker.RecurrenceOption recurrenceOption,
                                            String recurrenceRule) {

            mSelectedDate = selectedDate;
            mHour = hourOfDay;
            mMinute = minute;
            mRecurrenceOption = recurrenceOption != null ?
                    recurrenceOption.name() : "n/a";
            mRecurrenceRule = recurrenceRule != null ?
                    recurrenceRule : "n/a";

            updateInfoView();

            svMainContainer.post(new Runnable() {
                @Override
                public void run() {
                    svMainContainer.scrollTo(svMainContainer.getScrollX(),
                            1000);
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.sampler_my);

        // 2 因为没有对Bundle数据进行操作，那么是如何区分编辑模式和新建模式呢，
        // 答案就是通过Activity下的getIntent()方法可以获取Intent，
        // 再通过重写自BaseActivity的parseIntent()方法设定操作方式
        super.onCreate(savedInstanceState);
        initializePresenter();
        notePresenter.onCreate(savedInstanceState);

        /*--------------------------------------------------------------- */
        // modified by 1
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 2 添加的代码
            getSupportActionBar().setTitle("新建日程");
            /////////////////////////////////
        }

//        // 返回键 navigation icon click
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });

        ivLeftLaunchPicker = (TextView) findViewById(R.id.ivLeftLaunchPicker);
        ivRightLaunchPicker = (TextView) findViewById(R.id.ivRightLaunchPicker);


        tvPickerToShow = (TextView) findViewById(R.id.tvPickerToShow);
        tvActivatedPickers = (TextView) findViewById(R.id.tvActivatedPickers);
        svMainContainer = (ScrollView) findViewById(R.id.svMainContainer);

        tvRecurrenceOption = ((TextView) findViewById(R.id.tvRecurrenceOption));
        tvRecurrenceRule = ((TextView) findViewById(R.id.tvRecurrenceRule));

        rlDateTimeRecurrenceInfo
                = (RelativeLayout) findViewById(R.id.rlDateTimeRecurrenceInfo);

        ivLeftLaunchPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2 承载时间选择器的Dialog
                SublimePickerFragment pickerFrag = new SublimePickerFragment();

                // 2 设置回调函数
                pickerFrag.setCallback(mFragmentCallback);

                // 时间选择器的功能选项 日期 时间 循环 和 空
                Pair<Boolean, SublimeOptions> optionsPair = getOptionsLeft();

                if (!optionsPair.first) { // 2 功能选型为空  无激活的选择器
                    Toast.makeText(SamplerMy.this, "No pickers activated",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2 存在有效选择
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                pickerFrag.setArguments(bundle);

                // 2 设置为没有标题 界面设置
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
                clickLeftLaunchPicker = true;
            }
        });
        ivRightLaunchPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // 2 承载时间选择器的Dialog
                SublimePickerFragment pickerFrag = new SublimePickerFragment();


                // 2 设置回调函数
                pickerFrag.setCallback(mFragmentCallback);

                // 时间选择器的功能选项 日期 时间 循环 和 空
                Pair<Boolean, SublimeOptions> optionsPair = getOptionsRight();

                if (!optionsPair.first) { // 2 功能选型为空  无激活的选择器
                    Toast.makeText(SamplerMy.this, "No pickers activated",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                // 2 存在有效选择
                Bundle bundle = new Bundle();
                bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
                pickerFrag.setArguments(bundle);

                // 2 设置为没有标题 界面设置
                pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
                pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");
                clickLeftLaunchPicker = false;
            }
        });

        // restore state
        dealWithSavedInstanceState(savedInstanceState);

        //      1
        activityRootView = findViewById(R.id.sampler_my);
        //获取屏幕高度
        screenHeight = this.getWindowManager().getDefaultDisplay().getHeight();
        //阀值设置为屏幕高度的1/3
        keyHeight = screenHeight/3;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //添加layout大小发生改变监听器
        activityRootView.addOnLayoutChangeListener(this);
    }

    private void initializePresenter() {
        notePresenter.attachView(this);
       // notePresenter.attachIntent(getIntent());
    }

    @Override
    protected void initializeDependencyInjector() {
        App app = (App) getApplication();
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(app.getAppComponent())
                .build();
        mActivityComponent.inject(this);
    }

    @Override
    protected void onStop() {
        notePresenter.onStop();
        super.onStop();
    }
    @Override
    public void onDestroy() {
        notePresenter.onDestroy();
        super.onDestroy();
    }
    @Override
    protected int getLayoutView() {
//        return R.layout.activity_note;
        return R.layout.sampler_my;
    }
    @Override
    protected void initToolbar(){
        super.initToolbar(toolbar);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    // 2 动态修改菜单  要实现动态修改菜单，关键点在于:
//    onCreateOptionsMenu(Menu menu)方法只在Activity创建时调用一次，
//    而onPrepareOptionsMenu(Menu menu)在每次访问菜单的时候调用。
//    此界面中具体实现逻辑为: 首次通过inflater填充菜单，之后一旦访问菜单即设置为不可见。
//    菜单的add()方法是追加式的，需要先调用clear()方法。
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        doneMenuItem = menu.getItem(0);
        notePresenter.onPrepareOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    // 2
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.upload_schedule:
//                jsonService.updateArrangement("20160827",mHour*60+mMinute);
                //jsonService.updateTest();
                //jsonService.dayTable("20160827","testtable");
//                Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
                // 登录中dialog
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progressDialog.setMessage("上传中...");
                progressDialog.setCancelable(true);
                // 登录取消
                progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                progressDialog.show();

                // 创建网络访问的url地址栏
                String url = "http://192.168.1.119:8080/ServeNew/arrangeCheck";
                RequestParams rp = new RequestParams(url);
                // 封装该rp对象的请求参数
                rp.addBodyParameter("op","update");
                rp.addBodyParameter("client", User.getInstance().getUsername());
                rp.addBodyParameter("workname", labelEditText.getText().toString());
                rp.addBodyParameter("worktext", contentEditText.getText().toString());
                if (startMonth < 10) {
                    if (startDayOfMonth < 10) {
                        rp.addBodyParameter("date", "" + startYear + "0" + startMonth + "0" + startDayOfMonth);
                    } else {
                        rp.addBodyParameter("date", "" + startYear + "0" + startMonth + startDayOfMonth);
                    }
                } else {
                    if (startDayOfMonth < 10) {
                        rp.addBodyParameter("date", "" + startYear + startMonth + "0" + startDayOfMonth);
                    } else {
                        rp.addBodyParameter("date", "" + startYear + startMonth + startDayOfMonth);
                    }
                }

                rp.addBodyParameter("starttime", Integer.toString(startHour * 60 + startMinute));
                rp.addBodyParameter("endtime", Integer.toString(endHour * 60 + endMinute));
                // xutils post提交
                x.http().post(rp, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        // dialog清除
                        progressDialog.dismiss();
                        if ("ok".equals(result)) {
                            // 传进的内部类的变量需为final
                            Toast.makeText(SamplerMy.this, "上传成功", Toast.LENGTH_SHORT).show();
                        } else if ("repetition".equals(result)){
                            Toast.makeText(SamplerMy.this, "已上传", Toast.LENGTH_SHORT).show();
                        } else if ("order".equals(result)) {
                            Toast.makeText(SamplerMy.this, "日程有误，开始时间晚于结束时间", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onError(Throwable throwable, boolean b) {
                        Toast.makeText(SamplerMy.this, "上传出错", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onCancelled(CancelledException e) {
                    }
                    @Override
                    public void onFinished() {
                        progressDialog.dismiss();
                    }
                });
                return true;
//            case R.id.remove_item:
//                Toast.makeText(this, "Hehehehe", Toast.LENGTH_SHORT).show();
//                return true;
            case R.id.addTimeDataToSchedule:

                if (startHour * 60 + startMinute >= endHour * 60 + endMinute) {
                    Toast.makeText(SamplerMy.this, "开始时间晚于结束时间，请重新输入", Toast.LENGTH_SHORT).show();
                    return true;
                }
//                Intent intentFromNote = getIntent();
//                String noteLabelAndContent = intentFromNote.getStringExtra("noteLabelAndContent");
                String noteLabelAndContent =
                        "标题:" + getLabelText()
                                + '\n' + "内容:" + getContentText();
                Intent intent = new Intent(SamplerMy.this,NoJsonServiceBasicActivity.class);
                intent.putExtra("startYear", startYear);
                intent.putExtra("startMonth", startMonth);
                intent.putExtra("startDayOfMonth", startDayOfMonth);
                intent.putExtra("startHour", startHour);
                intent.putExtra("startMinute", startMinute);
                intent.putExtra("endYear", startYear);
                intent.putExtra("endMonth", startMonth);
                intent.putExtra("endDayOfMonth", startDayOfMonth);
                intent.putExtra("endHour", endHour);
                intent.putExtra("endMinute", endMinute);
                intent.putExtra("samplerNoteLabelAndContent",noteLabelAndContent);

                Schedule s = new Schedule();
                s.setWorkname(getLabelText());
                s.setWorktext(getContentText());
                String date;
                if (startMonth < 10) {
                    if (startDayOfMonth < 10) {
                        date = "" + startYear + "0" + startMonth + "0" + startDayOfMonth;
                    } else {
                        date =  "" + startYear + "0" + startMonth + startDayOfMonth;
                    }
                } else {
                    if (startDayOfMonth < 10) {
                        date = "" + startYear + startMonth + "0" + startDayOfMonth;
                    } else {
                        date = "" + startYear + startMonth + startDayOfMonth;
                    }
                }

                s.setDate(date);
                s.setStarttime(startHour * 60 + startMinute);
                s.setEndtime(endHour * 60 + endMinute);
                UserDB.getInstance(SamplerMy.this).saveSchedule(s);

                startActivity(intent);
                SamplerMy.this.finish();

                return true;

            case android.R.id.home:
                SamplerMy.this.finish();
                startActivity(new Intent(SamplerMy.this,NoJsonServiceBasicActivity.class));
                // 2 添加的代码
                return true;
        }
        if (notePresenter.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return notePresenter.onKeyDown(keyCode) || super.onKeyDown(keyCode, event);
    }
    @Override
    public void finishView() {
        finish();
    }
    @Override
    public void setToolbarTitle(String title) {
        if (toolbar != null){
            toolbar.setTitle(title);
        }
    }
    @Override
    public void setToolbarTitle(int title) {
        if (toolbar != null){
            toolbar.setTitle(title);
        }
    }
    // 2 三个方法分别初始化为编辑模式、查看模式、创建模式
    @Override
    public void initViewOnEditMode(SNote note) {
        showKeyBoard();
        // 2 对于EditText等View，可以调用labelEditText.requestFocus()捕获焦点
        labelEditText.requestFocus();
        //labelEditText.setText(note.getLabel());
        //contentEditText.setText(note.getContent());
        labelEditText.setSelection(note.getLabel().length());
        contentEditText.setSelection(note.getContent().length());

        // 2 对文本改变和焦点改变设置了监听，分别用于控制菜单显示和Toolbar标题:
        labelEditText.addTextChangedListener(notePresenter);
        contentEditText.addTextChangedListener(notePresenter);
    }
    @Override
    public void initViewOnViewMode(SNote note) {
        hideKeyBoard();
        //labelEditText.setText(note.getLabel());
        //contentEditText.setText(note.getContent());
        labelEditText.setOnFocusChangeListener(notePresenter);
        contentEditText.setOnFocusChangeListener(notePresenter);
        labelEditText.addTextChangedListener(notePresenter);
        contentEditText.addTextChangedListener(notePresenter);
    }
    @Override
    public void initViewOnCreateMode(SNote note) {
        labelEditText.requestFocus();
        //labelEditText.addTextChangedListener(notePresenter);
        contentEditText.addTextChangedListener(notePresenter);
    }
    @Override
    public void setOperateTimeLineTextView(String text) {
        oprTimeLineTextView.setText(text);
    }
    @Override
    public void setDoneMenuItemVisible(boolean visible) {
        if (doneMenuItem != null){
            doneMenuItem.setVisible(visible);
        }
    }
    @Override
    public boolean isDoneMenuItemVisible() {
        return doneMenuItem != null && doneMenuItem.isVisible();
    }
    @Override
    public boolean isDoneMenuItemNull() {
        return doneMenuItem == null;
    }
    @Override
    public String getLabelText() {
        return labelEditText.getText().toString();
    }

    @Override
    public String getContentText() {
        return contentEditText.getText().toString();
    }
    // 2 过显示对话框可以友好地提示用户将笔记保存。
    @Override
    public void showNotSaveNoteDialog(){
        AlertDialog.Builder builder = DialogUtils.makeDialogBuilder(this);
        builder.setTitle(R.string.not_save_note_leave_tip);
        builder.setPositiveButton(R.string.sure, notePresenter);
        builder.setNegativeButton(R.string.cancel, notePresenter);
        builder.show();
    }
    // 2  键盘显示与隐藏
    @Override
    public void hideKeyBoard(){
        hideKeyBoard(labelEditText);
    }

    @Override
    public void showKeyBoard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void hideKeyBoard(EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    void dealWithSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) { // Default

        } else { // Restore

            onActivatedPickersChanged();

            if (savedInstanceState.getBoolean(SS_INFO_VIEW_VISIBILITY)) {
                int startYear = savedInstanceState.getInt(SS_START_YEAR);

                if (startYear != INVALID_VAL) {
                    Calendar startCal = Calendar.getInstance();
                    startCal.set(startYear, savedInstanceState.getInt(SS_START_MONTH),
                            savedInstanceState.getInt(SS_START_DAY));

                    Calendar endCal = Calendar.getInstance();
                    endCal.set(savedInstanceState.getInt(SS_END_YEAR),
                            savedInstanceState.getInt(SS_END_MONTH),
                            savedInstanceState.getInt(SS_END_DAY));
                    mSelectedDate = new SelectedDate(startCal, endCal);
                }

                mHour = savedInstanceState.getInt(SS_HOUR);
                mMinute = savedInstanceState.getInt(SS_MINUTE);
                mRecurrenceOption = savedInstanceState.getString(SS_RECURRENCE_OPTION);
                mRecurrenceRule = savedInstanceState.getString(SS_RECURRENCE_RULE);

                updateInfoView();
            }

            final int scrollY = savedInstanceState.getInt(SS_SCROLL_Y);

            if (scrollY != 0) {
                svMainContainer.post(new Runnable() {
                    @Override
                    public void run() {
                        svMainContainer.scrollTo(svMainContainer.getScrollX(),
                                scrollY);
                    }
                });
            }

            // Set callback
            SublimePickerFragment restoredFragment = (SublimePickerFragment)
                    getSupportFragmentManager().findFragmentByTag("SUBLIME_PICKER");
            if (restoredFragment != null) {
                restoredFragment.setCallback(mFragmentCallback);
            }
        }
    }

    Pair<Boolean, SublimeOptions> getOptionsLeft() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
//        displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);

        options.setDisplayOptions(displayOptions);

        // Enable/disable the date range selection feature

        // If 'displayOptions' is zero, the chosen options are not valid
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    Pair<Boolean, SublimeOptions> getOptionsRight() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;

        options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);

        options.setDisplayOptions(displayOptions);

        // If 'displayOptions' is zero, the chosen options are not valid
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    // Validates & returns SublimePicker options
    Pair<Boolean, SublimeOptions> getOptions() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;
//        if (cbDatePicker.isChecked()) {
//            displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
//        }
//
//        if (cbTimePicker.isChecked()) {
//            displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
//        }
//
//        if (cbRecurrencePicker.isChecked()) {
//            displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;
//        }

        options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
//        if (rbDatePicker.getVisibility() == View.VISIBLE && rbDatePicker.isChecked()) {
//            options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
//        } else if (rbTimePicker.getVisibility() == View.VISIBLE && rbTimePicker.isChecked()) {
//            options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
//        } else if (rbRecurrencePicker.getVisibility() == View.VISIBLE && rbRecurrencePicker.isChecked()) {
//            options.setPickerToShow(SublimeOptions.Picker.REPEAT_OPTION_PICKER);
//        }

        options.setDisplayOptions(displayOptions);


        // Example for setting date range:
        // Note that you can pass a date range as the initial date params
        // even if you have date-range selection disabled. In this case,
        // the user WILL be able to change date-range using the header
        // TextViews, but not using long-press.

        /*Calendar startCal = Calendar.getInstance();
        startCal.set(2016, 2, 4);
        Calendar endCal = Calendar.getInstance();
        endCal.set(2016, 2, 17);

        options.setDateParams(startCal, endCal);*/

        // If 'displayOptions' is zero, the chosen options are not valid
        return new Pair<>(displayOptions != 0 ? Boolean.TRUE : Boolean.FALSE, options);
    }

    // Re-evaluates the state based on checked/unchecked
    // options - toggles visibility of RadioButtons based
    // on activated/deactivated pickers - updates TextView
    // labels accordingly.
    // This is also a sample app only method & can be skipped.
    void onActivatedPickersChanged() {
//        if (!cbDatePicker.isChecked()
//                && !cbTimePicker.isChecked()
//                && !cbRecurrencePicker.isChecked()) {
//
//            // None of the pickers have been activated
//            tvActivatedPickers.setText("Pickers to activate (choose at least one):");
//            tvPickerToShow.setText("Picker to show on dialog creation: N/A");
//        } else {
//            // At least one picker is active
//            tvActivatedPickers.setText("Pickers to activate:");
//            tvPickerToShow.setText("Picker to show on dialog creation:");
//
//            if ((rbDatePicker.isChecked() && rbDatePicker.getVisibility() != View.VISIBLE)
//                    || (rbTimePicker.isChecked() && rbTimePicker.getVisibility() != View.VISIBLE)
//                    || (rbRecurrencePicker.isChecked() && rbRecurrencePicker.getVisibility() != View.VISIBLE)) {
//                if (rbDatePicker.getVisibility() == View.VISIBLE) {
//                    rbDatePicker.setChecked(true);
//                    return;
//                }
//
//                if (rbTimePicker.getVisibility() == View.VISIBLE) {
//                    rbTimePicker.setChecked(true);
//                    return;
//                }
//
//                if (rbRecurrencePicker.getVisibility() == View.VISIBLE) {
//                    rbRecurrencePicker.setChecked(true);
//                }
//            }
//        }
    }

    // Show date, time & recurrence options that have been selected
    private void updateInfoView() {
        if(clickLeftLaunchPicker == false) {
            Toast.makeText(SamplerMy.this, "结束时间为" + startYear + " "
                    + startMonth + "-"
                    + startDayOfMonth + " "
                    + mHour + ":"
                    + mMinute, Toast.LENGTH_SHORT).show();

//                    endYear = mSelectedDate.getStartDate().get(Calendar.YEAR);
//                    endMonth = (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1);
//                    endDayOfMonth = mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH);
            endHour = mHour;
            endMinute = mMinute;
        } else
        if (mSelectedDate != null) {
            if (mSelectedDate.getType() == SelectedDate.Type.SINGLE) {
//                llDateRangeHolder.setVisibility(View.GONE);
//                llDateHolder.setVisibility(View.VISIBLE);

//                tvYear.setText(applyBoldStyle("YEAR: ")
//                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.YEAR))));
//                tvMonth.setText(applyBoldStyle("MONTH: ")
//                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.MONTH))));
                Log.e("当前时间为","   " + (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1));
                if(clickLeftLaunchPicker == false) {
                    Toast.makeText(SamplerMy.this, "结束时间为" + startYear + " "
                            + startMonth + ""
                            + startDayOfMonth + " "
                            + mHour + ":"
                            + mMinute, Toast.LENGTH_SHORT).show();

//                    endYear = mSelectedDate.getStartDate().get(Calendar.YEAR);
//                    endMonth = (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1);
//                    endDayOfMonth = mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH);
                    endHour = mHour;
                    endMinute = mMinute;
                } else {
                    Toast.makeText(SamplerMy.this, "开始时间为" + mSelectedDate.getStartDate().get(Calendar.YEAR) + " "
                            + (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1) + "-"
                            + mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH) + " "
                            + mHour + ":"
                            + mMinute, Toast.LENGTH_SHORT).show();

                    startYear = mSelectedDate.getStartDate().get(Calendar.YEAR);
                    startMonth = (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1);
                    startDayOfMonth = mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH);
                    startHour = mHour;
                    startMinute = mMinute;
                }
//                tvDay.setText(applyBoldStyle("DAY: ")
//                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH))));
            } else if (mSelectedDate.getType() == SelectedDate.Type.RANGE) {
//                llDateHolder.setVisibility(View.GONE);
//                llDateRangeHolder.setVisibility(View.VISIBLE);

//                tvStartDate.setText(applyBoldStyle("START: ")
//                        .append(DateFormat.getDateInstance().format(mSelectedDate.getStartDate().getTime())));
//                tvEndDate.setText(applyBoldStyle("END: ")
//                        .append(DateFormat.getDateInstance().format(mSelectedDate.getEndDate().getTime())));
            }
        }

//        tvHour.setText(applyBoldStyle("HOUR: ").append(String.valueOf(mHour)));
//        tvMinute.setText(applyBoldStyle("MINUTE: ").append(String.valueOf(mMinute)));
//
//        tvRecurrenceOption.setText(applyBoldStyle("RECURRENCE OPTION: ")
//                .append(mRecurrenceOption));
//        tvRecurrenceRule.setText(applyBoldStyle("RECURRENCE RULE: ").append(
//                mRecurrenceRule));

        // rlDateTimeRecurrenceInfo.setVisibility(View.VISIBLE);
    }

    // Applies a StyleSpan to the supplied text
    private SpannableStringBuilder applyBoldStyle(String text) {
        SpannableStringBuilder ss = new SpannableStringBuilder(text);
        ss.setSpan(new StyleSpan(Typeface.BOLD), 0, text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }

    // Keys for saving state
    final String SS_DATE_PICKER_CHECKED = "saved.state.date.picker.checked";
    final String SS_TIME_PICKER_CHECKED = "saved.state.time.picker.checked";
    final String SS_RECURRENCE_PICKER_CHECKED = "saved.state.recurrence.picker.checked";
    final String SS_ALLOW_DATE_RANGE_SELECTION = "saved.state.allow.date.range.selection";
    final String SS_START_YEAR = "saved.state.start.year";
    final String SS_START_MONTH = "saved.state.start.month";
    final String SS_START_DAY = "saved.state.start.day";
    final String SS_END_YEAR = "saved.state.end.year";
    final String SS_END_MONTH = "saved.state.end.month";
    final String SS_END_DAY = "saved.state.end.day";
    final String SS_HOUR = "saved.state.hour";
    final String SS_MINUTE = "saved.state.minute";
    final String SS_RECURRENCE_OPTION = "saved.state.recurrence.option";
    final String SS_RECURRENCE_RULE = "saved.state.recurrence.rule";
    final String SS_INFO_VIEW_VISIBILITY = "saved.state.info.view.visibility";
    final String SS_SCROLL_Y = "saved.state.scroll.y";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Save state of CheckBoxes
        // State of RadioButtons can be evaluated

        int startYear = mSelectedDate != null ? mSelectedDate.getStartDate().get(Calendar.YEAR) : INVALID_VAL;
        int startMonth = mSelectedDate != null ? mSelectedDate.getStartDate().get(Calendar.MONTH) : INVALID_VAL;
        int startDayOfMonth = mSelectedDate != null ? mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH) : INVALID_VAL;

        int endYear = mSelectedDate != null ? mSelectedDate.getEndDate().get(Calendar.YEAR) : INVALID_VAL;
        int endMonth = mSelectedDate != null ? mSelectedDate.getEndDate().get(Calendar.MONTH) : INVALID_VAL;
        int endDayOfMonth = mSelectedDate != null ? mSelectedDate.getEndDate().get(Calendar.DAY_OF_MONTH) : INVALID_VAL;

        // Save data
        outState.putInt(SS_START_YEAR, startYear);
        outState.putInt(SS_START_MONTH, startMonth);
        outState.putInt(SS_START_DAY, startDayOfMonth);
        outState.putInt(SS_END_YEAR, endYear);
        outState.putInt(SS_END_MONTH, endMonth);
        outState.putInt(SS_END_DAY, endDayOfMonth);
        outState.putInt(SS_HOUR, mHour);
        outState.putInt(SS_MINUTE, mMinute);
        outState.putString(SS_RECURRENCE_OPTION, mRecurrenceOption);
        outState.putString(SS_RECURRENCE_RULE, mRecurrenceRule);
//        outState.putBoolean(SS_INFO_VIEW_VISIBILITY,
//                rlDateTimeRecurrenceInfo.getVisibility() == View.VISIBLE);
        outState.putInt(SS_SCROLL_Y, svMainContainer.getScrollY());

        super.onSaveInstanceState(outState);
    }

    // 1
    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        //现在认为只要控件将Activity向上推的高度超过了1/3屏幕高，就认为软键盘弹起
        if(oldBottom != 0 && bottom != 0 &&(oldBottom - bottom > keyHeight)){
            // 软键盘弹出，按钮消失
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                    ivLeftLaunchPicker,
                    "alpha",
                    1F,
                    0F);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                    ivRightLaunchPicker,
                    "alpha",
                    1F,
                    0F);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300);
            set.setInterpolator(new DecelerateInterpolator());
            set.playTogether(animator1, animator2);
            set.start();
            // 监听动画结束
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ivLeftLaunchPicker.setVisibility(View.GONE);
                    ivRightLaunchPicker.setVisibility(View.GONE);
                }
            });

        }else if(oldBottom != 0 && bottom != 0 &&(bottom - oldBottom > keyHeight)){
            // 软键盘收起，按钮出现
            ObjectAnimator animator1 = ObjectAnimator.ofFloat(
                    ivLeftLaunchPicker,
                    "alpha",
                    0F,
                    1F);
            ObjectAnimator animator2 = ObjectAnimator.ofFloat(
                    ivRightLaunchPicker,
                    "alpha",
                    0F,
                    1F);
            AnimatorSet set = new AnimatorSet();
            set.setDuration(300);
            set.setInterpolator(new DecelerateInterpolator());
            set.playTogether(animator1, animator2);
            set.start();
            // 监听动画结束
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    ivLeftLaunchPicker.setVisibility(View.VISIBLE);
                    ivRightLaunchPicker.setVisibility(View.VISIBLE);
                }
            });
        }
    }
}
