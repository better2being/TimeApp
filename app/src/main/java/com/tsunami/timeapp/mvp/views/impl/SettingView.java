package com.tsunami.timeapp.mvp.views.impl;

import android.preference.Preference;
import android.support.annotation.StringRes;

import com.tsunami.timeapp.mvp.views.View;


/**
 * @author shenxiaoming
 */
public interface SettingView extends View {
    // 2 包含有对每个设置项进行设置的方法
    void findPreference();
    void setRightHandModePreferenceChecked(boolean checked);
    void setCardLayoutPreferenceChecked(boolean checked);
    void setFeedbackPreferenceSummary(CharSequence c);
    void setFeedbackPreferenceClickListener(Preference.OnPreferenceClickListener l);
    void setEverNoteAccountPreferenceSummary(CharSequence c);
    void setEverNoteAccountPreferenceTitle(CharSequence c);
    void initPreferenceListView(android.view.View view);
    void showSnackbar(@StringRes int message);
    void showThemeChooseDialog();
    boolean isResume();
    void showUnbindEverNoteDialog();
    void toast(@StringRes int message);
    void reload();
}
