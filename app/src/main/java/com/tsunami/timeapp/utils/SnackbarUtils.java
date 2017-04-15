package com.tsunami.timeapp.utils;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.View;


/**
 * @author wangshujie
 */
public class SnackbarUtils {

    public static final int DURATION = Snackbar.LENGTH_LONG / 2 ;

    // 2 SnackbarUtils是作者封装的快速显示Snackbar消息的，
    // 这个要学习的是如何通过传入Activity或Fab本身来执行Snackbar.make(…)方法:
    // 可以传入FloatActionBar本身调用方法: SnackbarUtils.show(fab, message);
    public static void show(View view, int message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .show();
    }

    public static void show(Activity activity, int message) {
        View view = activity.getWindow().getDecorView();
        show(view, message);
    }

    public static void showAction(View view, int message, int action, View.OnClickListener listener) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction(action, listener)
                .show();
    }

    public static void showAction(Activity activity, int message, int action, View.OnClickListener listener) {
        View view = activity.getWindow().getDecorView();
        showAction(view, message, action, listener);
    }
}
