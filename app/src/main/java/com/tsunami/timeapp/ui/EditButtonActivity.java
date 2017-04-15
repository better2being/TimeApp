package com.tsunami.timeapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


import com.tsunami.timeapp.R;
import com.tsunami.timeapp.mvp.views.impl.MainView;

/**
 * @author wangshujie
 */
public class EditButtonActivity extends AppCompatActivity{

    private Button btn_edit;
    private Button btn_note;
    private MainView mainView;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button_edit);


    }


    private void initView() {
        btn_edit = (Button) findViewById(R.id.btn_edit);
        btn_note = (Button) findViewById(R.id.btn_note);
    }

    public void onClickEdit(View v) {
       startActivity(new Intent(EditButtonActivity.this,NoteActivity.class));

    }

    public void onClickNote(View v) {
      startActivity(new Intent(EditButtonActivity.this,MainActivity.class));
//        EditButtonActivity.this.finish();
    }

    public void onClickSettings(View v) {
        startActivity(new Intent(EditButtonActivity.this,SettingActivity.class));
    //    EditButtonActivity.this.finish();
    }


    public void onClickTrash(View v) {
        startActivity(new Intent(EditButtonActivity.this,TrashActivity.class));
        //    EditButtonActivity.this.finish();
//        mainView.showFab(false);
//        mainView.enableSwipeRefreshLayout(false);
    }
}
