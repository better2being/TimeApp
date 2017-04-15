package com.tsunami.timeapp.Activity3.Toast;

/**
 * @author zhenglifeng
 */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tsunami.timeapp.R;

public class ToastMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_toast_main);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View toastRoot = getLayoutInflater().inflate(R.layout.my_toast, null);
                Toast toast=new Toast(getApplicationContext());
                toast.setView(toastRoot);
                TextView tv=(TextView)toastRoot.findViewById(R.id.TextViewInfo);
                tv.setText("说明：这是一个自定义边框和底色的提示框。");
                toast.show();
            }
        });

    }
}
