package com.tsunami.timeapp.activity1;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.tsunami.timeapp.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import butterknife.Bind;
import butterknife.ButterKnife;
/**
 * @author shenxiaoming
 */
public class NewSignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";

    @Bind(R.id.input_name) EditText _nameText;
    /*@Bind(R.id.input_email) EditText _emailText;*/
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_signup) Button _signupButton;
    @Bind(R.id.link_login) TextView _loginLink;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsignup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(_passwordText.getWindowToken(),0);
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed();
            return;
        }

        _signupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(NewSignupActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("创建账户中...");
        progressDialog.show();

        String name = _nameText.getText().toString();
//        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onSignupSuccess or onSignupFailed
                        // depending on success
//                        onSignupSuccess();
//                        // onSignupFailed();
//                        progressDialog.dismiss();
                        // 创建网络访问的url地址栏    192.168.173.1
                        String url = "http://192.168.1.119:8080/ServeNew/RegisterCheck";
                        RequestParams rp = new RequestParams(url);
                        // 封装该rp对象的请求参数

                        rp.addBodyParameter("username", name);
                        rp.addBodyParameter("password", password);
                        // xutils post提交
                        x.http().post(rp, new Callback.CommonCallback<String>() {
                            @Override
                            public void onSuccess(String result) {
                                // 登录dialog清除
                                progressDialog.dismiss();
                                if ("ok".equals(result)) {
                                    Toast.makeText(NewSignupActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                                } else {
                                    switch (result) {
                                        case "repetition":

                                            Toast.makeText(NewSignupActivity.this, "用户名已存在", Toast.LENGTH_LONG).show();
                                            break;
                                        default:
                                            Toast.makeText(NewSignupActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                }
                            }

                            @Override
                            public void onError(Throwable throwable, boolean b) {
                                progressDialog.dismiss();
                                Toast.makeText(NewSignupActivity.this, "注册出错", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(CancelledException e) {

                            }
                            @Override
                            public void onFinished() {

                            }
                        });
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signupButton.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    public void onSignupFailed() {
        //Toast.makeText(getBaseContext(), "创建账户失败", Toast.LENGTH_LONG).show();

        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        /*String email = _emailText.getText().toString();*/
        String password = _passwordText.getText().toString();

        // 首字符不能为数字     1
        if (name.charAt(0) >= '0' && name.charAt(0) <= '9') {
            Toast.makeText(getBaseContext(), "首字符不能为数字", Toast.LENGTH_LONG).show();
            return false;
        }

        if (name.isEmpty() || name.length() < 3) {
            //_nameText.setError("至少3个字符");
            Toast.makeText(getBaseContext(), "用户名至少3个字符", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _nameText.setError(null);
        }

        /*if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
           // _emailText.setError("请输入有效的邮箱地址");
            Toast.makeText(getBaseContext(), "请输入有效的邮箱地址", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _emailText.setError(null);
        }*/

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
           // _passwordText.setError("密码在4到10个字符之间");
            Toast.makeText(getBaseContext(), "密码在4到10个字符之间", Toast.LENGTH_LONG).show();
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}