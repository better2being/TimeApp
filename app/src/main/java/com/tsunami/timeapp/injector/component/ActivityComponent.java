package com.tsunami.timeapp.injector.component;

import android.content.Context;

import com.tsunami.timeapp.activity1.SamplerMy;
import com.tsunami.timeapp.injector.Activity;
import com.tsunami.timeapp.injector.ContextLifeCycle;
import com.tsunami.timeapp.injector.module.ActivityModule;
import com.tsunami.timeapp.ui.MainActivity;
import com.tsunami.timeapp.ui.NoteActivity;
import com.tsunami.timeapp.ui.SettingActivity;
import com.tsunami.timeapp.ui.TrashActivity;
import com.tsunami.timeapp.ui.NewNoteActivity;

import net.tsz.afinal.FinalDb;

import dagger.Component;

/**
 * @author shenxiaoming
 */
@Activity
@Component(dependencies = AppComponent.class, modules = {ActivityModule.class})
public interface ActivityComponent {
    void inject(MainActivity activity);
    void inject(NoteActivity activity);
    void inject(SettingActivity activity);

    void inject(SamplerMy activity);
    /**
     * 2 添加的代码
     * @param activity
     */
    void inject(TrashActivity activity);
    void inject(NewNoteActivity activity);
    //////////////////
    android.app.Activity activity();
    FinalDb finalDb();
    @ContextLifeCycle("Activity") Context activityContext();
    @ContextLifeCycle("App") Context appContext();
}
