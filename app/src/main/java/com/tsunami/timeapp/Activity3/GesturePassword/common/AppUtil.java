
package com.tsunami.timeapp.Activity3.GesturePassword.common;

import android.content.Context;
import android.view.WindowManager;
/**
 * @author zhenglifeng
 */
public class AppUtil {
    
	/**
     * ��ȡ��Ļ�ֱ���
     * @param context
     * @return
     */
    public static int[] getScreenDispaly(Context context) {
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int width = windowManager.getDefaultDisplay().getWidth();// �ֻ���Ļ�Ŀ��
		int height = windowManager.getDefaultDisplay().getHeight();// �ֻ���Ļ�ĸ߶�
		int result[] = { width, height };
		return result;
	}
    
}
