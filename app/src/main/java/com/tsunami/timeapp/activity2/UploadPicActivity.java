package com.tsunami.timeapp.activity2;


import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.tsunami.timeapp.R;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
/**
 * @author wangshujie
 */

public class UploadPicActivity extends AppCompatActivity{

    private Button btn_uploadPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpic);

        btn_uploadPic = (Button) findViewById(R.id.btn_uploadPic);
        btn_uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                File outputImage = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");
//
//                // 创建网络访问的url地址栏    192.168.173.1
//                String url = "http://192.168.1.121:8080/ServeNew/loginCheck";
//                RequestParams rp = new RequestParams(url);
//                // 封装该rp对象的请求参数
//                rp.addBodyParameter("fileName", outputImage);
//
//                // xutils post提交
//                x.http().post(rp, new Callback.CommonCallback<String>() {
//                    @Override
//                    public void onSuccess(String result) {
//
//                        if ("ok".equals(result)) {
//                            Toast.makeText(UploadPicActivity.this, "上传成功", Toast.LENGTH_LONG).show();
//                        } else {
//                            switch (result) {
//                                case "repetition":
//
//                                    Toast.makeText(UploadPicActivity.this, "上传失败", Toast.LENGTH_LONG).show();
//                                    break;
//                                default:
//                                    Toast.makeText(UploadPicActivity.this, "上传失败", Toast.LENGTH_LONG).show();
//                                    break;
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onError(Throwable throwable, boolean b) {
//
//                        Toast.makeText(UploadPicActivity.this, "Error", Toast.LENGTH_LONG).show();
//                    }
//
//                    @Override
//                    public void onCancelled(CancelledException e) {
//
//                    }
//
//                    @Override
//                    public void onFinished() {
//
//                    }
//                });










            }


        });
    }



    public void onUploadPic(View view){

        File outputImage = new File(Environment.getExternalStorageDirectory(), "output_image.jpg");

        // 创建网络访问的url地址栏    192.168.173.1
        String url = "http://192.168.173.1:8080/ServeNew/RegisterCheck";
        RequestParams rp = new RequestParams(url);
        // 封装该rp对象的请求参数
        rp.addBodyParameter("picFile", outputImage);

        // xutils post提交
        x.http().post(rp, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                if ("ok".equals(result)) {
                    Toast.makeText(UploadPicActivity.this, "注册成功", Toast.LENGTH_LONG).show();
                } else {
                    switch (result) {
                        case "repetition":

                            Toast.makeText(UploadPicActivity.this, "用户名已存在", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            Toast.makeText(UploadPicActivity.this, "注册失败", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            }

            @Override
            public void onError(Throwable throwable, boolean b) {

                Toast.makeText(UploadPicActivity.this, "注册出错", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancelled(CancelledException e) {

            }

            @Override
            public void onFinished() {

            }
        });
    }




}
