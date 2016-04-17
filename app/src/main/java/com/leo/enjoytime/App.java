package com.leo.enjoytime;

import android.app.Application;
import android.content.Context;

import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.network.AbstractNewWorkerManager;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by leo on 16/3/6.
 */
public class App extends Application {
    private static DBManager manager;
    private static AbstractNewWorkerManager newWorkerManager;
    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new DBManager(getApplicationContext());
        newWorkerManager = AbstractNewWorkerManager.getInstance();
        newWorkerManager.init(getApplicationContext());
        refWatcher = LeakCanary.install(this);
        CrashReport.initCrashReport(getApplicationContext(), "900025781", false);
    }

    public static AbstractNewWorkerManager getNetWorkManager(){
        return newWorkerManager;
    }

    public static DBManager getDbManager(){
      return manager;
    }
}
