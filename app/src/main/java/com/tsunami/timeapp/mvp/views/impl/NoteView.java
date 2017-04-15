package com.tsunami.timeapp.mvp.views.impl;

import android.support.annotation.StringRes;

import com.tsunami.timeapp.model.SNote;
import com.tsunami.timeapp.mvp.views.View;
/**
 * @author shenxiaoming
 */
public interface NoteView extends View {
    void finishView();
    void setToolbarTitle(String title);
    void setToolbarTitle(@StringRes int title);
    void initViewOnEditMode(SNote note);
    void initViewOnViewMode(SNote note);
    void initViewOnCreateMode(SNote note);
    void setOperateTimeLineTextView(String text);
    void setDoneMenuItemVisible(boolean visible);
    boolean isDoneMenuItemVisible();
    boolean isDoneMenuItemNull();
    String getLabelText();
    String getContentText();
    void hideKeyBoard();
    void showKeyBoard();
    void showNotSaveNoteDialog();
}
