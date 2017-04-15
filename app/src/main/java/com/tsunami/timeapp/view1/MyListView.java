package com.tsunami.timeapp.view1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * @author wangshujie
 */
public class MyListView extends ListView {

    /**
     * 解决ListView高度自适应和ScrollView冲突
     */
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

    private float mLastY;
    // 标记当前listview是否被按下
    private boolean isDown = false;

    /**
     * 解决与onTouch的ACTION_DOWN冲突，取出ACTION_DOWN的y坐标
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mLastY = event.getRawY();
            isDown = true;
        }

        return super.dispatchTouchEvent(event);
    }

    public boolean getIsDown() {
        return isDown;
    }

    public void setIsDown(boolean down) {
        isDown = down;
    }

    public float getLastY() {
        return mLastY;
    }



    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


}