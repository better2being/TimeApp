package com.tsunami.timeapp.activity1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.Toast;

import com.tsunami.timeapp.R;


/**
 * @author shenxiaoming
 */
public class BaseActivity extends AppCompatActivity {

    private long exitTime = 0;

    /**
     * 返回按钮连按两次退出程序 2秒
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if((System.currentTimeMillis() - exitTime) > 2000) {
                Toast.makeText(this.getApplicationContext(), R.string.exit, Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                Intent intent=new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                BaseActivity.this.startActivity(intent);
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
