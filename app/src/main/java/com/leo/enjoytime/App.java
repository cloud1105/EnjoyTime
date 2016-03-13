package com.leo.enjoytime;

import android.app.Application;

import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.network.VolleyUtils;

/**
 * Created by leo on 16/3/6.
 */
public class App extends Application {
    private static DBManager manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = new DBManager(getApplicationContext());
        VolleyUtils.init(getApplicationContext());
    }

    public static DBManager getDbmanager(){
      return manager;
    }
}
