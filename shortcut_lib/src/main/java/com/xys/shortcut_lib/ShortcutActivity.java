package com.xys.shortcut_lib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * 通过指定多个Launcher入口实现
 * <p/>
 * Created by xuyisheng on 15/10/30.
 * Version 1.0
 */
public class ShortcutActivity extends Activity {

    private TextView note_label_shortcut;
    private TextView note_content_shortcut;
    private TextView opr_time_line_text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shortcut_main);

        note_label_shortcut = (TextView) findViewById(R.id.note_label_shortcut);
        note_content_shortcut = (TextView) findViewById(R.id.note_content_shortcut);
        opr_time_line_text = (TextView) findViewById(R.id.opr_time_line_text);

        Intent intent = getIntent();
        String noteLabel = intent.getStringExtra("noteLabel");
        String noteContent = intent.getStringExtra("noteContent");
        String noteTypedTime = intent.getStringExtra("noteTypedTime");



        note_label_shortcut.setText(noteLabel);
        note_content_shortcut.setText(noteContent);
        opr_time_line_text.setText(noteTypedTime);


    }
}
