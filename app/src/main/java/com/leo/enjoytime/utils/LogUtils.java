package com.leo.enjoytime.utils;

import android.util.Log;

import com.leo.enjoytime.BuildConfig;

/**
 * Created by leo on 16/4/12.
 */
public class LogUtils {

    public static void loggerV(String tag,String msg){
       if (BuildConfig.DEBUG){
           Log.v(tag,msg);
       }
    }

    public static void loggerD(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.d(tag,msg);
        }
    }

    public static void loggerI(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.i(tag,msg);
        }
    }

    public static void loggerW(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.w(tag,msg);
        }
    }

    public static void loggerE(String tag,String msg){
        if (BuildConfig.DEBUG){
            Log.e(tag,msg);
        }
    }
}
