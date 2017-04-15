package com.tsunami.timeapp.apps;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import org.xutils.x;
import com.baidu.apistore.sdk.ApiStoreSDK;

import java.io.File;

/**
 * @author jilihao
 */
public class MyXutils extends Application {

    // 默认存放图片的路径
    public final static String DEFAULT_SAVE_IMAGE_PATH = Environment.getExternalStorageDirectory() + File.separator + "CircleDemo" + File.separator + "Images"
            + File.separator;

    private static Context mContext;
    @Override
    public void onCreate() {
        ApiStoreSDK.init(this, "c9fede3ca8b6f60bf44c5d0c4f24ec2c");
        Log.i("APP", "APP");
        super.onCreate();
        // 初始化xutils线程池
        x.Ext.init(this);
        x.Ext.setDebug(true);

        mContext = getApplicationContext();
        //LeakCanary.install(this);
//        QPManager.getInstance(getApplicationContext()).initRecord();
    }

    public static Context getContext(){
        return mContext;
    }
}
