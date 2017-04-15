package com.tsunami.timeapp.injector.module;

import android.content.Context;

import com.tsunami.timeapp.App;
import com.tsunami.timeapp.BuildConfig;
import com.tsunami.timeapp.injector.ContextLifeCycle;

import net.tsz.afinal.FinalDb;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * @author shenxiaoming
 */
@Module
public class AppModule {
    private final App app;

    public AppModule(App app) {
        this.app = app;
    }

    @Provides @Singleton
    App provideApplication() {
        return app;
    }

    @Provides @Singleton @ContextLifeCycle("App")
    Context provideActivityContext() {
        return app.getApplicationContext();
    }

    @Provides @Singleton
    FinalDb.DaoConfig provideDaoConfig(@ContextLifeCycle("App") Context context) {
        FinalDb.DaoConfig config = new FinalDb.DaoConfig();
        config.setDbName("notes.db");
        config.setDbVersion(2);
        config.setDebug(BuildConfig.DEBUG);
        config.setContext(context);
        config.setDbUpdateListener((db, oldVersion, newVersion) -> {
            if (newVersion == 2 && oldVersion == 1) {
                db.execSQL("ALTER TABLE '" + "notes" + "' ADD COLUMN " +
                        "`createTime`" + " INTEGER;");
                db.execSQL("ALTER TABLE '" + "notes" + "' ADD COLUMN " +
                        "status" + " INTEGER;");
                db.execSQL("ALTER TABLE '" + "notes" + "' ADD COLUMN " +
                        "guid" + " TEXT;");
                db.execSQL("UPDATE '" + "notes" + "' SET type = 0 " +
                        "WHERE type = 1 OR type = 2;");
                db.execSQL("UPDATE '" + "notes" + "' SET type = 1 " +
                        "WHERE type = 3;");
                db.execSQL("UPDATE '" + "notes" + "' SET status = 2 " +
                        "WHERE type = 1;");
            }
        });
        return config;
    }

    @Provides @Singleton
    FinalDb provideFinalDb(FinalDb.DaoConfig config) {
        return FinalDb.create(config);
    }
}
