package com.tsunami.timeapp.injector.component;

import android.content.Context;

import com.tsunami.timeapp.App;
import com.tsunami.timeapp.injector.ContextLifeCycle;
import com.tsunami.timeapp.injector.module.AppModule;

import net.tsz.afinal.FinalDb;

import javax.inject.Singleton;

import dagger.Component;

/**
 * @author shenxiaoming
 */
@Singleton
@Component(modules = AppModule.class)
public interface AppComponent {
    App app();
    @ContextLifeCycle("App") Context context();
    FinalDb finalDb();
    FinalDb.DaoConfig daoConfig();
}
