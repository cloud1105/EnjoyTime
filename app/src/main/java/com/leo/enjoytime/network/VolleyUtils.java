package com.leo.enjoytime.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.utils.BitmapCache;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by leo on 16/3/9.
 */
public class VolleyUtils {
    private final static String TAG = VolleyUtils.class.getSimpleName();
    private static RequestQueue queue;

    public static void init(Context context) {
        queue = Volley.newRequestQueue(context);
    }


    public static void queryGanhuo(String tag, String type, int page, int count,
                                   Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url;
        switch (type) {
            case Const.REQUEST_TYPE_ANDROID:
                url = Const.ANDROID_DATA_URL + count + "/" + page;
                break;
            case Const.REQUEST_TYPE_iOS:
                url = Const.IOS_DATA_URL + count + "/" + page;
                break;
            case Const.REQUEST_TYPE_MEIZHI:
                url = Const.MEIZHI_DATA_URL + count + "/" + page;
                try {
                    url = Const.GANHUO_HOST_URL + "/data/" + URLEncoder.encode("福利", "utf-8") + "/" + count + "/" + page;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                break;
            default:
                url = Const.ALL_DATA_URL + count + "/" + page;
                break;

        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(url, listener, errorListener);
        objectRequest.setTag(tag);
        queue.add(objectRequest);
    }

    public static void queryGanChai(String tag, int type, int page, int count,
                                    Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        String url = Const.getGanChaiDigestListUrl(type,page,count);
        JsonObjectRequest objectRequest = new JsonObjectRequest(url, listener, errorListener);
        objectRequest.setTag(tag);
        queue.add(objectRequest);
    }


    public static void setMeizhiImg(NetworkImageView imageView, String url) {
        ImageLoader imageLoader = new ImageLoader(queue, new BitmapCache());
        imageView.setImageUrl(url, imageLoader);
    }


    public static void cancelQuery(String tag) {
        queue.cancelAll(tag);
    }

}
