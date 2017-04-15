package com.tsunami.timeapp.mvp.presenters;

import android.os.Bundle;

import com.tsunami.timeapp.mvp.views.View;

/**
 * @author shenxiaoming
 */
public interface Presenter {

    // 2 Presenter接口将Activity的生命周期抽象出来，
    // 并且通过attachView将Activity传入，
    // 用于MainPresenter的初始化。
    // 注意View是本项目中所定义的，并非安卓API。
    void onCreate (Bundle savedInstanceState);

    void onResume();

    void onStart ();

    void onPause();

    //void onStop ();

    void onDestroy();

    void attachView (View v);

}
