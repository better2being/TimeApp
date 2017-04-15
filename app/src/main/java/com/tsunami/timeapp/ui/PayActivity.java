package com.tsunami.timeapp.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;


import com.tsunami.timeapp.R;

import butterknife.Bind;

/**
 * @author wangshujie
 */
public class PayActivity extends BaseActivity{
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutView() {
        return R.layout.activity_pay;
    }


    @Override
    protected void initToolbar(){
        super.initToolbar(toolbar);
        toolbar.setTitle(R.string.pay_for_me);
    }

}