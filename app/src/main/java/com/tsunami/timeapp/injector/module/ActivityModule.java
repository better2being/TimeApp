package com.tsunami.timeapp.injector.module;

import android.content.Context;

import com.tsunami.timeapp.injector.Activity;
import com.tsunami.timeapp.injector.ContextLifeCycle;

import dagger.Module;
import dagger.Provides;

/**
 * @author shenxiaoming
 */
@Module
public class ActivityModule {
    private final android.app.Activity activity;
    public ActivityModule(android.app.Activity activity) {
        this.activity = activity;
    }

    @Provides @Activity
    android.app.Activity provideActivity() {
        return activity;
    }

    @Provides @Activity @ContextLifeCycle("Activity")
    Context provideContext() {
        return activity;
    }
}
