package com.tsunami.timeapp.activity2.weekview;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.weekview.apiclient.EuclidState;
import com.tsunami.timeapp.ui.NewNoteActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author wangshujie
 */


public abstract class FriendBaseAvtivity extends AppCompatActivity implements WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener {
    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;

    protected TextView mTextViewProfileName;
    protected TextView mTextViewProfileDescription;
    protected TextView mTextEvent;
    public static ShapeDrawable sOverlayShape;
    static int sScreenWidth;
    static int sProfileImageHeight;
    private int eventColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekviewbase);

        Toolbar toolbar = (Toolbar) findViewById(R.id.weekviewtoolbar);
        setSupportActionBar(toolbar);


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            // 2 添加的代码
            getSupportActionBar().setTitle("日程表");
            /////////////////////////////////
        }



        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set long press listener for empty view
        mWeekView.setEmptyViewLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);
        mToolbarProfile = (RelativeLayout) findViewById(R.id.toolbar_profile);
        mProfileDetails = (LinearLayout) findViewById(R.id.wrapper_profile_details);
        mButtonProfile = findViewById(R.id.button_profile);
        mButtonProfile.post(new Runnable() {
            @Override
            public void run() {
                mInitialProfileButtonX = mButtonProfile.getX();
            }
        });
        findViewById(R.id.toolbar_profile_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("back");
                animateCloseProfileDetails();
            }
        });
        System.out.println("why");
        mTextViewProfileName = (TextView) findViewById(R.id.text_view_profile_name);
        mTextViewProfileDescription = (TextView) findViewById(R.id.text_view_profile_description);

        System.out.println("whywhy");
        sScreenWidth = getResources().getDisplayMetrics().widthPixels;
        sProfileImageHeight = getResources().getDimensionPixelSize(R.dimen.height_profile_image);
        //sOverlayShape = buildAvatarCircleOverlay();

        mTestEvent=(LinearLayout)findViewById(R.id.testEvent);
        mTextEvent=(TextView) findViewById(R.id.henguanjian);
        System.out.println("whywhywhy");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weekview_friend_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id){
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;

            case android.R.id.home:
                FriendBaseAvtivity.this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }

    protected String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH)+1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        animateOpenProfileDetails(10);
        Animator testAnim= ObjectAnimator.ofFloat(mWeekView,"rotationY", 0.0F, 360.0F/2);
        testAnim.setDuration(1000/2);
        //testAnim.setD
        // mProfileDetails.setBackgroundColor(event.getColor());
        //testAnim.setInterpolator(new AccelerateInterpolator());

        mTestEvent.setBackgroundColor(event.getColor());
        mTextEvent.setText(event.getName());
        Animator showEvent=ObjectAnimator.ofFloat(mTestEvent,"rotationY",270.0f/2-180f,360.0f/2-180f);
        showEvent.setDuration(250/2);
        showEvent.setStartDelay(750/2);
        //showEvent.setInterpolator(new AccelerateInterpolator());
        showEvent.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                mTestEvent.setX(0);
                mTestEvent.setY(0+mToolbarProfile.getHeight());
                mTestEvent.setVisibility(View.VISIBLE);
                // mTestEvent.bringToFront();
                mButtonProfile.bringToFront();

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


        AnimatorSet animatorSet=new AnimatorSet();






        animatorSet.playTogether(testAnim,showEvent);
        animatorSet.start();
        int color=event.getColor();
        System.out.println(color);
        int r=color/256/256%256;
        int g=color/256%256;
        int b=color%256;
        r=r-(r+256)/2;
        g=g-(g+256)/2;
        b=b-(b+256)/2;
        System.out.println(r+":"+g+":"+b);
        eventColor=color/256/256/256*256*256*256+r*256*256+g*256+b;
        //testAnim.AnimatorListener();

        //mToolbarProfile.setVisibility(View.VISIBLE);
        Toast.makeText(this, event.getName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        final AlertDialog dlg = new AlertDialog.Builder(this).create();
        dlg.show();
        Window window = dlg.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,shrew_exit_dialog.xml文件中定义view内容
        window.setContentView(R.layout.alertdialog);
        // 为确认按钮添加事件,执行退出应用操作
        TextView tv_nan = (TextView) window.findViewById(R.id.tv_content1);
        tv_nan.setText("编辑");
        tv_nan.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SdCardPath")
            public void onClick(View v) {
                Toast.makeText(FriendBaseAvtivity.this,"" + event.getName(),Toast.LENGTH_SHORT).show();
                dlg.cancel();
            }
        });
        TextView tv_nv = (TextView) window.findViewById(R.id.tv_content2);
        tv_nv.setText("删除");
        tv_nv.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dlg.cancel();
            }
        });
    }

    @Override
    public void onEmptyViewLongPress(Calendar time) {
        startActivity(new Intent(FriendBaseAvtivity.this,NewNoteActivity.class));
    }

    public WeekView getWeekView() {
        return mWeekView;
    }
    protected RelativeLayout mToolbarProfile;
    protected LinearLayout mProfileDetails;
    protected LinearLayout mTestEvent;
    protected View mButtonProfile;
    private float mInitialProfileButtonX;

    private EuclidState mState = EuclidState.Closed;
    private AnimatorSet mOpenProfileAnimatorSet=null;
    private AnimatorSet mCloseProfileAnimatorSet=null;
    private Animation mProfileButtonShowAnimation=null;

    private void animateCloseProfileDetails() {
        mState = EuclidState.Closing;
        getCloseProfileAnimatorSet().start();
    }
    private static final int STEP_DELAY_HIDE_DETAILS_ANIMATION = 80;
    protected int getStepDelayHideDetailsAnimation() {
        return STEP_DELAY_HIDE_DETAILS_ANIMATION;
    }
    private AnimatorSet getCloseProfileAnimatorSet() {
        if (mCloseProfileAnimatorSet == null) {
            Animator profileToolbarAnimator = ObjectAnimator.ofFloat(mToolbarProfile, View.X,
                    0, mToolbarProfile.getWidth());

            Animator profilePhotoAnimator = ObjectAnimator.ofFloat(mTestEvent, View.X,
                    0, mTestEvent.getWidth());
            profilePhotoAnimator.setStartDelay(getStepDelayHideDetailsAnimation());

            Animator profileButtonAnimator = ObjectAnimator.ofFloat(mButtonProfile, View.X,
                    mInitialProfileButtonX, 320 + mInitialProfileButtonX);
            profileButtonAnimator.setStartDelay(getStepDelayHideDetailsAnimation() * 2);

            Animator profileDetailsAnimator = ObjectAnimator.ofFloat(mProfileDetails, "translationX",
                    0, mToolbarProfile.getWidth());
            profileDetailsAnimator.setStartDelay(getStepDelayHideDetailsAnimation() * 2);

            Animator weekViewMove=ObjectAnimator.ofFloat(mWeekView,"rotationY",180.0f,360.0f);
            weekViewMove.setDuration(1000);
            weekViewMove.setInterpolator(new AccelerateInterpolator());
            //weekViewMove.setStartDelay(getStepDelayHideDetailsAnimation());

            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(profileToolbarAnimator);
            profileAnimators.add(profilePhotoAnimator);
            profileAnimators.add(profileButtonAnimator);
            profileAnimators.add(profileDetailsAnimator);
            profileAnimators.add(weekViewMove);

            mCloseProfileAnimatorSet = new AnimatorSet();
            mCloseProfileAnimatorSet.playTogether(profileAnimators);
            mCloseProfileAnimatorSet.setDuration(500);
            mCloseProfileAnimatorSet.setInterpolator(new AccelerateInterpolator());
            mCloseProfileAnimatorSet.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //   if (mListViewAnimator != null) {
                    //       mListViewAnimator.reset();
                    //      mListViewAnimationAdapter.notifyDataSetChanged();
                    // }
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mToolbarProfile.setVisibility(View.INVISIBLE);
                    mButtonProfile.setVisibility(View.INVISIBLE);
                    mProfileDetails.setVisibility(View.INVISIBLE);
                    mTestEvent.setVisibility(View.INVISIBLE);
                    // mListView.setEnabled(true);
                    // mListViewAnimator.disableAnimations();

                    mState = EuclidState.Closed;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
        return mCloseProfileAnimatorSet;
    }

    private void animateOpenProfileDetails(int profileDetailsAnimationDelay) {
        mState = EuclidState.Opening;
        createOpenProfileButtonAnimation();
        System.out.println("createAnimation");
        getOpenProfileAnimatorSet(profileDetailsAnimationDelay).start();
        System.out.println("AnimationStart");
    }
    private void createOpenProfileButtonAnimation() {
        if (mProfileButtonShowAnimation == null) {
            mProfileButtonShowAnimation = AnimationUtils.loadAnimation(this, R.anim.profile_button_scale);
            mProfileButtonShowAnimation.setDuration(500);
            mProfileButtonShowAnimation.setInterpolator(new AccelerateInterpolator());
            mProfileButtonShowAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    System.out.println("look");
                    mButtonProfile.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
    private AnimatorSet getOpenProfileAnimatorSet(int profileDetailsAnimationDelay) {

        System.out.println("testset");
        if (mOpenProfileAnimatorSet == null) {
            List<Animator> profileAnimators = new ArrayList<>();
            profileAnimators.add(getOpenProfileToolbarAnimator());
            profileAnimators.add(getOpenProfileDetailsAnimator());

            System.out.println("allright");
            mOpenProfileAnimatorSet = new AnimatorSet();
            mOpenProfileAnimatorSet.playTogether(profileAnimators);
            mOpenProfileAnimatorSet.setDuration(500);
        }
        System.out.println("ok");
        mOpenProfileAnimatorSet.setStartDelay(profileDetailsAnimationDelay);
        //mOpenProfileAnimatorSet.setInterpolator(new AccelerateInterpolator());
        mOpenProfileAnimatorSet.setInterpolator(new DecelerateInterpolator());
        //System.out""
        return mOpenProfileAnimatorSet;
    }

    private Animator getOpenProfileToolbarAnimator() {
        //int height=;
        //System.out.println(height);
        Animator mOpenProfileToolbarAnimator = ObjectAnimator.ofFloat(mToolbarProfile, "translationY",-mToolbarProfile.getHeight(),0);
        //   (-mToolbarProfile.getHeight()), 0);
        System.out.println("toolbat run");
        mOpenProfileToolbarAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                System.out.println("setready");
                mToolbarProfile.setX(0);
                System.out.println("setok");
                mToolbarProfile.setBackgroundColor(eventColor);
                mToolbarProfile.bringToFront();
                mToolbarProfile.setVisibility(View.VISIBLE);
                mProfileDetails.setX(0);
                mProfileDetails.setBackgroundColor(eventColor);
                mProfileDetails.bringToFront();
                mProfileDetails.setVisibility(View.VISIBLE);

                mButtonProfile.setX(mInitialProfileButtonX);
                mButtonProfile.bringToFront();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mButtonProfile.startAnimation(mProfileButtonShowAnimation);

                mState = EuclidState.Opened;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return mOpenProfileToolbarAnimator;
    }

    /**
     * This method creates animator which shows profile details.
     *
     * @return - animator object.
     */
    private Animator getOpenProfileDetailsAnimator() {
        Animator mOpenProfileDetailsAnimator = ObjectAnimator.ofFloat(mProfileDetails, "translationY",
                getResources().getDisplayMetrics().heightPixels,
                0);
        return mOpenProfileDetailsAnimator;
    }
    @Override
    public void onBackPressed() {
        if (mState == EuclidState.Opened) {
            animateCloseProfileDetails();
        } else if (mState == EuclidState.Closed) {
            super.onBackPressed();
        }
    }
}

