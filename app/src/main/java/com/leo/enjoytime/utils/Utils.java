package com.leo.enjoytime.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;

import com.leo.enjoytime.activity.WebViewActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by leo on 16/3/9.
 */
public class Utils {
    /***
     * 获取默认格式的日期字符串
     * @param date
     * @return
     */
    private final static String TAG = Utils.class.getSimpleName();
    public static String getFormatDateStr(final Date date){
        if(null == date)return null;
        return DateFormat.getDateInstance(DateFormat.DEFAULT).format(date);
    }

    public static Date formatDateFromStr(final String dateStr){
        Date date = new Date();
        if(!TextUtils.isEmpty(dateStr)){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
            try {
                date = sdf.parse(dateStr);
            }catch (Exception e){
                Log.d(TAG,"Error,format Date error");
            }
        }
        return date;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Converts a dp value to pixels.
     * @param dp
     * @return Pixel value of dp.
     */
    public static float dpToPix(Context ctx,float dp) {
        //return SCALE * dp + FLOAT_INT_AVG_NUDGE;
        //InternalDimension id = new InternalDimension(dp, TypedValue.COMPLEX_UNIT_DIP);
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,  ctx.getResources().getDisplayMetrics());

    }

    public static void gotoWebView(Activity activity,String url) {
        Intent intent = new Intent();
        intent.setAction("com.leo.enjoytime.VIEW");
        intent.putExtra(WebViewActivity.URL_PARAM, url);
        activity.startActivity(intent);
    }

}
