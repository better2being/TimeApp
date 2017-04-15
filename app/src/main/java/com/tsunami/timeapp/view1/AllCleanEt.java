package com.tsunami.timeapp.view1;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * @author wangshujie
 */
public class AllCleanEt extends EditText {

    private Drawable dRight;
    private Rect rBounds;

    public AllCleanEt(Context context) {
        super(context);
        initView();
    }

    public AllCleanEt(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AllCleanEt(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView() {
        setEditTextDrawable();
        // 监听内容改变
        addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                AllCleanEt.this.setEditTextDrawable();
            }
        });
    }

    /**
     * 控制显示图片
     */
    public void setEditTextDrawable() {
        if (getText().length() == 0) {
            setCompoundDrawables(null, null, null, null);
        } else {
            setCompoundDrawables(null, null, dRight, null);
        }
    }

    /**
     * 显示右侧X图片
     *      左上右下
     */
    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null) {
            dRight = right;
        }
        super.setCompoundDrawables(left, top, right, bottom);
    }
}
