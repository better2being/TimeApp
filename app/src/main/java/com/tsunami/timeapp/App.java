package com.tsunami.timeapp;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;


import com.baidu.apistore.sdk.ApiStoreSDK;
import com.evernote.client.android.EvernoteSession;
import com.tsunami.timeapp.injector.component.AppComponent;
import com.tsunami.timeapp.injector.component.DaggerAppComponent;
import com.tsunami.timeapp.injector.module.AppModule;

import org.xutils.x;

import java.io.File;

/**
 * @author shenxiaoming
 */
public class App extends Application{

    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + "CircleDemo" + File.separator + "Images"
            + File.separator;

    private static Context mContext;

    private AppComponent mAppComponent;
    @Override
    public void onCreate() {
        // 2 添加的代码
        ApiStoreSDK.init(this, "c9fede3ca8b6f60bf44c5d0c4f24ec2c");
        Log.i("APP", "APP");
        ////////////////////////////////
        super.onCreate();

        mContext = getApplicationContext();

        // 2 添加的代码
        // 初始化xutils线程池
        x.Ext.init(this);
        x.Ext.setDebug(true);
        ////////////////////////////////

        initializeInjector();
        buildEverNoteSession();
    }

    private void buildEverNoteSession(){
        EvernoteSession.EvernoteService service;
        if (BuildConfig.DEBUG)
            service = EvernoteSession.EvernoteService.SANDBOX;
        else
            service = EvernoteSession.EvernoteService.PRODUCTION;
        new EvernoteSession.Builder(this)
                .setEvernoteService(service)
                .setSupportAppLinkedNotebooks(false)
                .build(BuildConfig.EVER_NOTE_KEY, BuildConfig.EVER_NOTE_SECRET)
                .asSingleton();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }


    private void initializeInjector() {
        mAppComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getAppComponent() {
        return mAppComponent;
    }

    public static Context getContext(){
        return mContext;
    }

}
