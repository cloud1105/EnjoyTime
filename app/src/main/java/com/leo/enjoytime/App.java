package com.leo.enjoytime;

import android.app.Application;
import android.content.Context;
import com.bugsnag.android.Bugsnag;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.network.VolleyUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by leo on 16/3/6.
 */
public class App extends Application {
    private static DBManager manager;
    public static RefWatcher getRefWatcher(Context context) {
        App application = (App) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new DBManager(getApplicationContext());
        VolleyUtils.init(getApplicationContext());
        Bugsnag.init(this);
        refWatcher = LeakCanary.install(this);
    }

    public static DBManager getDbmanager(){
      return manager;
    }
}
