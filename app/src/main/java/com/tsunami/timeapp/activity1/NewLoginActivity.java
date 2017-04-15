package com.tsunami.timeapp.activity1;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;



import com.tsunami.timeapp.R;
import com.tsunami.timeapp.activity2.UserActivity;
import com.tsunami.timeapp.db1.UserDB;

import org.xutils.common.Callback;
import org.xutils.x;
import org.xutils.http.RequestParams;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author shenxiaoming
 */
public class NewLoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private long exitTime = 0;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newlogin);
        ButterKnife.bind(this);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(_passwordText.getWindowToken(),0);
//                startActivity(new Intent(NewLoginActivity.this, UserActivity.class));
                login();// ZLF911
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), NewSignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        //ZLF 如果保存过密码
        SharedPreferences pref = getSharedPreferences("data",MODE_PRIVATE);
        _emailText.setText(pref.getString("user_name",""), TextView.BufferType.EDITABLE);
        _passwordText.setText(pref.getString("user_password",""), TextView.BufferType.EDITABLE);
    }

    public void login() {

        // 2 无密码进入
        if(true) {
            startActivity(new Intent(NewLoginActivity.this, UserActivity.class));
            UserDB.getInstance(NewLoginActivity.this).saveUserName("tsunami");
            UserDB.getInstance(NewLoginActivity.this).saveUserPassword("000000");
            // 结束登录界面
            NewLoginActivity.this.finish();
            return;
        }






       /* if (!validate()) {
            onLoginFailed();
            return;
        }*/

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(NewLoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("登陆中...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
//                        onLoginSuccess();
//                        // onLoginFailed();
//                        progressDialog.dismiss();
                                        // 创建网络访问的url地址栏
                        String url = "http://192.168.1.119:8080/ServeNew/loginCheck";
                        RequestParams rp = new RequestParams(url);
                        // 封装该rp对象的请求参数
                        rp.addBodyParameter("username", email);
                        rp.addBodyParameter("password", password);
                        // xutils post提交
                        x.http().post(rp, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // 登录dialog清除
                                progressDialog.dismiss();
                                if ("ok".equals(result)) {
                                    // 传进的内部类的变量需为final
                                        Toast.makeText(NewLoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                                    //ZLF  如果成功，将用户名密码保存入文件
                                        SharedPreferences.Editor editor = getSharedPreferences("data",MODE_PRIVATE).edit();
                                        editor.putString("user_name",email);
                                        editor.putString("user_password", password);
                                        editor.apply();
                                    Log.e("login", _emailText.getText().toString());
                                    Log.e("login2", email);
                                        UserDB.getInstance(NewLoginActivity.this).saveUserName(_emailText.getText().toString());

                                        UserDB.getInstance(NewLoginActivity.this).saveUserPassword(_passwordText.getText().toString());
                                        startActivity(new Intent(NewLoginActivity.this, UserActivity.class));
                                        // 结束登录界面
                                        NewLoginActivity.this.finish();
                                    } else {
                                        Toast.makeText(NewLoginActivity.this, R.string.password_wrong, Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                }

                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                progressDialog.dismiss();
                                Toast.makeText(NewLoginActivity.this, "登录出错", Toast.LENGTH_LONG).show();
                            }
                            @Override
                            public void onCancelled(CancelledException e) {

                            }
                            @Override
                            public void onFinished() {
                                _loginButton.setEnabled(true);
                            }
                        });
                    }
                }, 1500); //zlf 这里延迟3秒是几个意思？
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        _loginButton.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
       // Toast.makeText(getBaseContext(), "登陆失败", Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
           // _emailText.setError("请输入有效的邮箱地址");
            Toast.makeText(getBaseContext(), "请输入有效的邮箱地址", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
           // _passwordText.setError( "密码在4到10个字符之间");
            Toast.makeText(getBaseContext(), "密码在4到10个字符之间", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _passwordText.setError(null);
        }*/

        return valid;
    }



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
                NewLoginActivity.this.startActivity(intent);
                System.exit(0);

            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
