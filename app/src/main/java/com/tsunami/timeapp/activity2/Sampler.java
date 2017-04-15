
package com.tsunami.timeapp.activity2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.appeaser.sublimepickerlibrary.helpers.SublimeOptions;
import com.appeaser.sublimepickerlibrary.recurrencepicker.SublimeRecurrencePicker;
import com.tsunami.timeapp.JsonService;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.weekview.NoJsonServiceBasicActivity;
import com.tsunami.timeapp.db1.UserDB;
import com.tsunami.timeapp.model1.Schedule;

import java.text.DateFormat;
import java.util.Calendar;
/**
 * @author wangshujie
 */
public class Sampler extends AppCompatActivity {

    private final int INVALID_VAL = -1;

    // 2 判断是否点击了设置开始时间按钮
    private boolean clickLeftLaunchPicker = true;

    private JsonService jsonService=new JsonService();
    //2  进入选择时间的按钮
    ImageView ivLeftLaunchPicker;
    ImageView ivRightLaunchPicker;

    // 2 选择的开始和结束时间
    private int startYear,startMonth,startDayOfMonth,startHour,startMinute;
    private int endYear,endMonth,endDayOfMonth,endHour,endMinute;


    // SublimePicker options
    CheckBox cbDatePicker, cbTimePicker, cbRecurrencePicker, cbAllowDateRangeSelection;
    RadioButton rbDatePicker, rbTimePicker, rbRecurrencePicker;

    // Labels
    TextView tvPickerToShow, tvActivatedPickers;

    ScrollView svMainContainer;

    // Views to display the chosen Date, Time & Recurrence options
    TextView tvYear, tvMonth, tvDay, tvHour,
            tvMinute, tvRecurrenceOption, tvRecurrenceRule,
            tvStartDate, tvEndDate;
    RelativeLayout rlDateTimeRecurrenceInfo;
    LinearLayout llDateHolder, llDateRangeHolder;

    // Chosen values
    SelectedDate mSelectedDate;
    int mHour, mMinute;
    String mRecurrenceOption, mRecurrenceRule;

    SublimePickerFragment.Callback mFragmentCallback = new SublimePickerFragment.Callback() {
        @Override
        public void onCancelled() {
            rlDateTimeRecurrenceInfo.setVisibility(View.GONE);
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
                            cbAllowDateRangeSelection.getBottom());
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sampler);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 2 添加的代码
            getSupportActionBar().setTitle("选择日程时间");
            /////////////////////////////////
        }

        // Finish on navigation icon click
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ivLeftLaunchPicker = (ImageView) findViewById(R.id.ivLeftLaunchPicker);
        ivRightLaunchPicker = (ImageView) findViewById(R.id.ivRightLaunchPicker);



        cbDatePicker = (CheckBox) findViewById(R.id.cbDatePicker);
        cbTimePicker = (CheckBox) findViewById(R.id.cbTimePicker);
        cbRecurrencePicker = (CheckBox) findViewById(R.id.cbRecurrencePicker);
        rbDatePicker = (RadioButton) findViewById(R.id.rbDatePicker);
        rbTimePicker = (RadioButton) findViewById(R.id.rbTimePicker);
        rbRecurrencePicker = (RadioButton) findViewById(R.id.rbRecurrencePicker);
        tvPickerToShow = (TextView) findViewById(R.id.tvPickerToShow);
        tvActivatedPickers = (TextView) findViewById(R.id.tvActivatedPickers);
        svMainContainer = (ScrollView) findViewById(R.id.svMainContainer);

        cbAllowDateRangeSelection = (CheckBox) findViewById(R.id.cbAllowDateRangeSelection);

        llDateHolder = (LinearLayout) findViewById(R.id.llDateHolder);
        llDateRangeHolder = (LinearLayout) findViewById(R.id.llDateRangeHolder);

        // Initialize views to display the chosen Date, Time & Recurrence options
        tvYear = ((TextView) findViewById(R.id.tvYear));
        tvMonth = ((TextView) findViewById(R.id.tvMonth));
        tvDay = ((TextView) findViewById(R.id.tvDay));

        tvStartDate = ((TextView) findViewById(R.id.tvStartDate));
        tvEndDate = ((TextView) findViewById(R.id.tvEndDate));

        tvHour = ((TextView) findViewById(R.id.tvHour));
        tvMinute = ((TextView) findViewById(R.id.tvMinute));

        tvRecurrenceOption = ((TextView) findViewById(R.id.tvRecurrenceOption));
        tvRecurrenceRule = ((TextView) findViewById(R.id.tvRecurrenceRule));

        rlDateTimeRecurrenceInfo
                = (RelativeLayout) findViewById(R.id.rlDateTimeRecurrenceInfo);

        // 2 点击时间选择器按钮的响应 颜色设置
        ivRightLaunchPicker.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        ivLeftLaunchPicker.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);

//        // 2 重置状态
//        dealWithSavedInstanceState(savedInstanceState);
//
//        // 2 承载时间选择器的Dialog
//        SublimePickerFragment pickerFrag = new SublimePickerFragment();
//
//
//        // 2 设置回调函数
//        pickerFrag.setCallback(mFragmentCallback);
//
//        // 时间选择器的功能选项 日期 时间 循环 和 空
//        Pair<Boolean, SublimeOptions> optionsPair = getOptions();
//
//        if (!optionsPair.first) { // 2 功能选型为空  无激活的选择器
//            Toast.makeText(Sampler.this, "No pickers activated",
//                    Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // 2 存在有效选择
//        Bundle bundle = new Bundle();
//        bundle.putParcelable("SUBLIME_OPTIONS", optionsPair.second);
//        pickerFrag.setArguments(bundle);
//
//        // 2 设置为没有标题 界面设置
//        pickerFrag.setStyle(DialogFragment.STYLE_NO_TITLE, 0);
//        pickerFrag.show(getSupportFragmentManager(), "SUBLIME_PICKER");


        ivLeftLaunchPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 2 承载时间选择器的Dialog
                SublimePickerFragment pickerFrag = new SublimePickerFragment();


                // 2 设置回调函数
                pickerFrag.setCallback(mFragmentCallback);

                // 时间选择器的功能选项 日期 时间 循环 和 空
                Pair<Boolean, SublimeOptions> optionsPair = getOptions();

                if (!optionsPair.first) { // 2 功能选型为空  无激活的选择器
                    Toast.makeText(Sampler.this, "No pickers activated",
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
                Pair<Boolean, SublimeOptions> optionsPair = getOptions();

                if (!optionsPair.first) { // 2 功能选型为空  无激活的选择器
                    Toast.makeText(Sampler.this, "No pickers activated",
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

        // De/activates Date Picker
        cbDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbDatePicker.setVisibility(cbDatePicker.isChecked() ?
                        View.VISIBLE : View.GONE);
                onActivatedPickersChanged();
            }
        });

        // De/activates Time Picker
        cbTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbTimePicker.setVisibility(cbTimePicker.isChecked() ?
                        View.VISIBLE : View.GONE);
                onActivatedPickersChanged();
            }
        });

        // De/activates Recurrence Picker
        cbRecurrencePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbRecurrencePicker.setVisibility(cbRecurrencePicker.isChecked() ?
                        View.VISIBLE : View.GONE);
                onActivatedPickersChanged();
            }
        });

        cbAllowDateRangeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // restore state
        dealWithSavedInstanceState(savedInstanceState);
    }

    void dealWithSavedInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState == null) { // Default
            cbDatePicker.setChecked(true);
            cbTimePicker.setChecked(true);
            cbRecurrencePicker.setChecked(true);
            cbAllowDateRangeSelection.setChecked(false);

            rbDatePicker.setChecked(true);
        } else { // Restore
            cbDatePicker.setChecked(savedInstanceState.getBoolean(SS_DATE_PICKER_CHECKED));
            cbTimePicker.setChecked(savedInstanceState.getBoolean(SS_TIME_PICKER_CHECKED));
            cbRecurrencePicker
                    .setChecked(savedInstanceState.getBoolean(SS_RECURRENCE_PICKER_CHECKED));
            cbAllowDateRangeSelection
                    .setChecked(savedInstanceState.getBoolean(SS_ALLOW_DATE_RANGE_SELECTION));

            rbDatePicker.setVisibility(cbDatePicker.isChecked() ?
                    View.VISIBLE : View.GONE);
            rbTimePicker.setVisibility(cbTimePicker.isChecked() ?
                    View.VISIBLE : View.GONE);
            rbRecurrencePicker.setVisibility(cbRecurrencePicker.isChecked() ?
                    View.VISIBLE : View.GONE);

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

    // Validates & returns SublimePicker options
    Pair<Boolean, SublimeOptions> getOptions() {
        SublimeOptions options = new SublimeOptions();
        int displayOptions = 0;

        if (cbDatePicker.isChecked()) {
            displayOptions |= SublimeOptions.ACTIVATE_DATE_PICKER;
        }

        if (cbTimePicker.isChecked()) {
            displayOptions |= SublimeOptions.ACTIVATE_TIME_PICKER;
        }

        if (cbRecurrencePicker.isChecked()) {
            displayOptions |= SublimeOptions.ACTIVATE_RECURRENCE_PICKER;
        }

        if (rbDatePicker.getVisibility() == View.VISIBLE && rbDatePicker.isChecked()) {
            options.setPickerToShow(SublimeOptions.Picker.DATE_PICKER);
        } else if (rbTimePicker.getVisibility() == View.VISIBLE && rbTimePicker.isChecked()) {
            options.setPickerToShow(SublimeOptions.Picker.TIME_PICKER);
        } else if (rbRecurrencePicker.getVisibility() == View.VISIBLE && rbRecurrencePicker.isChecked()) {
            options.setPickerToShow(SublimeOptions.Picker.REPEAT_OPTION_PICKER);
        }

        options.setDisplayOptions(displayOptions);

        // Enable/disable the date range selection feature
        options.setCanPickDateRange(cbAllowDateRangeSelection.isChecked());

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
        if (!cbDatePicker.isChecked()
                && !cbTimePicker.isChecked()
                && !cbRecurrencePicker.isChecked()) {

            // None of the pickers have been activated
            tvActivatedPickers.setText("Pickers to activate (choose at least one):");
            tvPickerToShow.setText("Picker to show on dialog creation: N/A");
        } else {
            // At least one picker is active
            tvActivatedPickers.setText("Pickers to activate:");
            tvPickerToShow.setText("Picker to show on dialog creation:");

            if ((rbDatePicker.isChecked() && rbDatePicker.getVisibility() != View.VISIBLE)
                    || (rbTimePicker.isChecked() && rbTimePicker.getVisibility() != View.VISIBLE)
                    || (rbRecurrencePicker.isChecked() && rbRecurrencePicker.getVisibility() != View.VISIBLE)) {
                if (rbDatePicker.getVisibility() == View.VISIBLE) {
                    rbDatePicker.setChecked(true);
                    return;
                }

                if (rbTimePicker.getVisibility() == View.VISIBLE) {
                    rbTimePicker.setChecked(true);
                    return;
                }

                if (rbRecurrencePicker.getVisibility() == View.VISIBLE) {
                    rbRecurrencePicker.setChecked(true);
                }
            }
        }
    }

    // Show date, time & recurrence options that have been selected
    private void updateInfoView() {
        if (mSelectedDate != null) {
            if (mSelectedDate.getType() == SelectedDate.Type.SINGLE) {
                llDateRangeHolder.setVisibility(View.GONE);
                llDateHolder.setVisibility(View.VISIBLE);

                tvYear.setText(applyBoldStyle("YEAR: ")
                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.YEAR))));
                tvMonth.setText(applyBoldStyle("MONTH: ")
                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.MONTH))));
                Log.e("当前时间为","   " + (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1));
                if(clickLeftLaunchPicker == false) {
                    Toast.makeText(Sampler.this, "结束时间为" + mSelectedDate.getStartDate().get(Calendar.YEAR) + "-"
                            + (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1) + "-"
                            + mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH) + "-"
                            + mHour + "-"
                            + mMinute, Toast.LENGTH_SHORT).show();

                    endYear = mSelectedDate.getStartDate().get(Calendar.YEAR);
                    endMonth = (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1);
                    endDayOfMonth = mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH);
                    endHour = mHour;
                    endMinute = mMinute;
                } else {
                    Toast.makeText(Sampler.this, "开始时间为" + mSelectedDate.getStartDate().get(Calendar.YEAR) + "-"
                            + (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1) + "-"
                            + mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH) + "-"
                            + mHour + "-"
                            + mMinute, Toast.LENGTH_SHORT).show();

                    startYear = mSelectedDate.getStartDate().get(Calendar.YEAR);
                    startMonth = (mSelectedDate.getStartDate().get(Calendar.MONTH) + 1);
                    startDayOfMonth = mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH);
                    startHour = mHour;
                    startMinute = mMinute;

                }
                tvDay.setText(applyBoldStyle("DAY: ")
                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH))));
            } else if (mSelectedDate.getType() == SelectedDate.Type.RANGE) {
                llDateHolder.setVisibility(View.GONE);
                llDateRangeHolder.setVisibility(View.VISIBLE);

                tvStartDate.setText(applyBoldStyle("START: ")
                        .append(DateFormat.getDateInstance().format(mSelectedDate.getStartDate().getTime())));
                tvEndDate.setText(applyBoldStyle("END: ")
                        .append(DateFormat.getDateInstance().format(mSelectedDate.getEndDate().getTime())));
            }
        }

        tvHour.setText(applyBoldStyle("HOUR: ").append(String.valueOf(mHour)));
        tvMinute.setText(applyBoldStyle("MINUTE: ").append(String.valueOf(mMinute)));

        tvRecurrenceOption.setText(applyBoldStyle("RECURRENCE OPTION: ")
                .append(mRecurrenceOption));
        tvRecurrenceRule.setText(applyBoldStyle("RECURRENCE RULE: ").append(
                mRecurrenceRule));

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
        outState.putBoolean(SS_DATE_PICKER_CHECKED, cbDatePicker.isChecked());
        outState.putBoolean(SS_TIME_PICKER_CHECKED, cbTimePicker.isChecked());
        outState.putBoolean(SS_RECURRENCE_PICKER_CHECKED, cbRecurrencePicker.isChecked());
        outState.putBoolean(SS_ALLOW_DATE_RANGE_SELECTION, cbAllowDateRangeSelection.isChecked());

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
        outState.putBoolean(SS_INFO_VIEW_VISIBILITY,
                rlDateTimeRecurrenceInfo.getVisibility() == View.VISIBLE);
        outState.putInt(SS_SCROLL_Y, svMainContainer.getScrollY());

        super.onSaveInstanceState(outState);
    }

    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_item:
                //String.valueOf(mMinute);
                // JsonService jsonService=new JsonService();
                jsonService.updateArrangement("20160827",mHour*60+mMinute);
                //jsonService.updateTest();
                //jsonService.dayTable("20160827","testtable");
                Toast.makeText(this, "上传成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.remove_item:
                Toast.makeText(this, "Hehehehe", Toast.LENGTH_SHORT).show();
                break;
            case R.id.addTimeDataToSchedule:

                Intent intentFromNote = getIntent();
                String noteLabel = intentFromNote.getStringExtra("noteLabel");
                String noteContent = intentFromNote.getStringExtra("noteContent");
                int noteID = intentFromNote.getIntExtra("noteID",0);
                String noteTypedTime = intentFromNote.getStringExtra("noteTypedTime");

//                //String noteLabelAndContent = intentFromNote.getStringExtra("noteLabelAndContent");
//                Intent intent = new Intent(Sampler.this,NoJsonServiceBasicActivity.class);
//
//                intent.putExtra("startYear", startYear);
//                intent.putExtra("startMonth", startMonth);
//                intent.putExtra("startDayOfMonth", startDayOfMonth);
//                intent.putExtra("startHour", startHour);
//                intent.putExtra("startMinute", startMinute);
//
//
//                intent.putExtra("endYear", endYear);
//                intent.putExtra("endMonth", endMonth);
//                intent.putExtra("endDayOfMonth", endDayOfMonth);
//                intent.putExtra("endHour", endHour);
//                intent.putExtra("endMinute", endMinute);
//
//
//                intent.putExtra("noteLabel", noteLabel);
//                intent.putExtra("noteContent", noteContent);
//                intent.putExtra("noteID", noteID);
//                intent.putExtra("noteTypedTime", noteTypedTime);
//               // intent.putExtra("samplerNoteLabelAndContent",noteLabelAndContent);
//
//                startActivity(intent);
//                //Toast.makeText(this, "传递时间数据成功", Toast.LENGTH_SHORT).show();
//                Toast.makeText(Sampler.this, "结束时间为" + endYear + "-"
//                        + endMonth + "-"
//                        + endDayOfMonth + "-"
//                        + endHour + "-"
//                        + endMinute, Toast.LENGTH_SHORT).show();
//
//                Toast.makeText(Sampler.this, "结束时间为" + startYear + "-"
//                        + startMonth + "-"
//                        + startDayOfMonth + "-"
//                        + startHour + "-"
//                        + startMinute, Toast.LENGTH_SHORT).show();
//                break;

                Schedule s = new Schedule();
                s.setWorkname(noteLabel);
                s.setWorktext(noteContent);
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
                UserDB.getInstance(Sampler.this).saveSchedule(s);
                break;
            default:
        }
        return true;
    }


}
