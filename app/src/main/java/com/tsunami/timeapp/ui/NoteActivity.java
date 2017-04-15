package com.tsunami.timeapp.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.tsunami.timeapp.App;

import com.tsunami.timeapp.R;
import com.tsunami.timeapp.injector.component.DaggerActivityComponent;
import com.tsunami.timeapp.injector.module.ActivityModule;
import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.mvp.presenters.impl.NotePresenter;
import com.tsunami.timeapp.mvp.views.impl.NoteView;
import com.tsunami.timeapp.utils.DialogUtils;
import com.rengwuxian.materialedittext.MaterialEditText;

import javax.inject.Inject;

import butterknife.Bind;

/**
 * @author wangshujie
 */
public class NoteActivity extends BaseActivity implements NoteView{
    @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.label_edit_text) MaterialEditText labelEditText;
    @Bind(R.id.content_edit_text) MaterialEditText contentEditText;
    @Bind(R.id.opr_time_line_text) TextView oprTimeLineTextView;
    @Inject NotePresenter notePresenter;
    private MenuItem doneMenuItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // 2 因为没有对Bundle数据进行操作，那么是如何区分编辑模式和新建模式呢，
        // 答案就是通过Activity下的getIntent()方法可以获取Intent，
        // 再通过重写自BaseActivity的parseIntent()方法设定操作方式
        super.onCreate(savedInstanceState);
        initializePresenter();
        notePresenter.onCreate(savedInstanceState);
    }

    private void initializePresenter() {
        notePresenter.attachView(this);
        notePresenter.attachIntent(getIntent());
    }

    @Override
    protected void initializeDependencyInjector() {
        App app = (App) getApplication();
        mActivityComponent = DaggerActivityComponent.builder()
                .activityModule(new ActivityModule(this))
                .appComponent(app.getAppComponent())
                .build();
        mActivityComponent.inject(this);
    }

    @Override
    protected void onStop() {
        notePresenter.onStop();
        super.onStop();
    }

    @Override
    public void onDestroy() {
        notePresenter.onDestroy();
        super.onDestroy();
    }


    @Override
    protected int getLayoutView() {
        return R.layout.activity_note;
    }

    @Override
    protected void initToolbar(){
        super.initToolbar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    // 2 动态修改菜单  要实现动态修改菜单，关键点在于:
//    onCreateOptionsMenu(Menu menu)方法只在Activity创建时调用一次，
//    而onPrepareOptionsMenu(Menu menu)在每次访问菜单的时候调用。
//    此界面中具体实现逻辑为: 首次通过inflater填充菜单，之后一旦访问菜单即设置为不可见。
//    菜单的add()方法是追加式的，需要先调用clear()方法。
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        doneMenuItem = menu.getItem(0);
        notePresenter.onPrepareOptionsMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    // 2
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (notePresenter.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return notePresenter.onKeyDown(keyCode) || super.onKeyDown(keyCode, event);
    }

    @Override
    public void finishView() {
        finish();
    }

    @Override
    public void setToolbarTitle(String title) {
        if (toolbar != null){
            toolbar.setTitle(title);
        }
    }

    @Override
    public void setToolbarTitle(int title) {
        if (toolbar != null){
            toolbar.setTitle(title);
        }
    }

    // 2 三个方法分别初始化为编辑模式、查看模式、创建模式
    @Override
    public void initViewOnEditMode(SNote note) {
        showKeyBoard();
        // 2 对于EditText等View，可以调用labelEditText.requestFocus()捕获焦点
        labelEditText.requestFocus();
        labelEditText.setText(note.getLabel());
        contentEditText.setText(note.getContent());
        labelEditText.setSelection(note.getLabel().length());
        contentEditText.setSelection(note.getContent().length());

        // 2 对文本改变和焦点改变设置了监听，分别用于控制菜单显示和Toolbar标题:
        labelEditText.addTextChangedListener(notePresenter);
        contentEditText.addTextChangedListener(notePresenter);
    }

    @Override
    public void initViewOnViewMode(SNote note) {
        hideKeyBoard();
        labelEditText.setText(note.getLabel());
        contentEditText.setText(note.getContent());
        labelEditText.setOnFocusChangeListener(notePresenter);
        contentEditText.setOnFocusChangeListener(notePresenter);
        labelEditText.addTextChangedListener(notePresenter);
        contentEditText.addTextChangedListener(notePresenter);
    }

    @Override
    public void initViewOnCreateMode(SNote note) {
        labelEditText.requestFocus();
        //labelEditText.addTextChangedListener(notePresenter);
        contentEditText.addTextChangedListener(notePresenter);
    }

    @Override
    public void setOperateTimeLineTextView(String text) {
        oprTimeLineTextView.setText(text);
    }

    @Override
    public void setDoneMenuItemVisible(boolean visible) {
        if (doneMenuItem != null){
            doneMenuItem.setVisible(visible);
        }
    }

    @Override
    public boolean isDoneMenuItemVisible() {
        return doneMenuItem != null && doneMenuItem.isVisible();
    }

    @Override
    public boolean isDoneMenuItemNull() {
        return doneMenuItem == null;
    }

    @Override
    public String getLabelText() {
        return labelEditText.getText().toString();
    }

    @Override
    public String getContentText() {
        return contentEditText.getText().toString();
    }

    // 2 过显示对话框可以友好地提示用户将笔记保存。
    @Override
    public void showNotSaveNoteDialog(){
        AlertDialog.Builder builder = DialogUtils.makeDialogBuilder(this);
        builder.setTitle(R.string.not_save_note_leave_tip);
        builder.setPositiveButton(R.string.sure, notePresenter);
        builder.setNegativeButton(R.string.cancel, notePresenter);
        builder.show();
    }

    // 2  键盘显示与隐藏
    @Override
    public void hideKeyBoard(){
        hideKeyBoard(labelEditText);
    }

    @Override
    public void showKeyBoard(){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

    }

    private void hideKeyBoard(EditText editText){
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
