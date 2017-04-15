package com.tsunami.timeapp.activity2;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.appeaser.sublimepickerlibrary.datepicker.SelectedDate;
import com.tsunami.timeapp.R;


/**
 * @author wangshujie
 */
public class TestTimePickerActivity extends AppCompatActivity{

    private Button btn_testTimePicker;
    RelativeLayout rlDateTimeRecurrenceInfo;
    // Chosen values
    SelectedDate mSelectedDate;

    int mHour, mMinute;
    String mRecurrenceOption, mRecurrenceRule;

//    ScrollView svMainContainer;
//    svMainContainer = (ScrollView) findViewById(R.id.svMainContainer);




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testtimepicker);

        btn_testTimePicker = (Button) findViewById(R.id.btn_testTimePicker);
        btn_testTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });
    }



    // Show date, time & recurrence options that have been selected
//    private void updateInfoView() {
//        if (mSelectedDate != null) {
//            if (mSelectedDate.getType() == SelectedDate.Type.SINGLE) {
//                llDateRangeHolder.setVisibility(View.GONE);
//                llDateHolder.setVisibility(View.VISIBLE);
//
//                tvYear.setText(applyBoldStyle("YEAR: ")
//                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.YEAR))));
//                tvMonth.setText(applyBoldStyle("MONTH: ")
//                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.MONTH))));
//                tvDay.setText(applyBoldStyle("DAY: ")
//                        .append(String.valueOf(mSelectedDate.getStartDate().get(Calendar.DAY_OF_MONTH))));
//            } else if (mSelectedDate.getType() == SelectedDate.Type.RANGE) {
//                llDateHolder.setVisibility(View.GONE);
//                llDateRangeHolder.setVisibility(View.VISIBLE);
//
//                tvStartDate.setText(applyBoldStyle("START: ")
//                        .append(DateFormat.getDateInstance().format(mSelectedDate.getStartDate().getTime())));
//                tvEndDate.setText(applyBoldStyle("END: ")
//                        .append(DateFormat.getDateInstance().format(mSelectedDate.getEndDate().getTime())));
//            }
//        }
//
//        tvHour.setText(applyBoldStyle("HOUR: ").append(String.valueOf(mHour)));
//        tvMinute.setText(applyBoldStyle("MINUTE: ").append(String.valueOf(mMinute)));
//
//        tvRecurrenceOption.setText(applyBoldStyle("RECURRENCE OPTION: ")
//                .append(mRecurrenceOption));
//        tvRecurrenceRule.setText(applyBoldStyle("RECURRENCE RULE: ").append(
//                mRecurrenceRule));
//
//        rlDateTimeRecurrenceInfo.setVisibility(View.VISIBLE);
//    }
}
