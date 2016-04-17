package com.leo.enjoytime.network;

import android.content.Context;
import android.widget.ImageView;

/**
 * Created by leo on 16/4/12.
 */
public abstract class AbstractNewWorkerManager {
    protected NetWorkCallback callback;
    public boolean isNew = false;

    public static AbstractNewWorkerManager getInstance() {
        // TODO: 根据xml配置一个int值决定网络库实例 16/4/12
        return new VolleyUtils();
    }

    public void setCallback(NetWorkCallback callback) {
        this.callback = callback;
    }

    public void init(Context context){}

    public abstract void queryGanhuo(String tag, String type, int page, int count,NetWorkCallback callback);

    public abstract void queryGanChai(String tag, int type, int page, int count,NetWorkCallback callback);

    public abstract void setMeizhiImg(ImageView imageView, String url,NetWorkCallback callback);

    public abstract void queryRssPage(String tag,String url,NetWorkCallback callback);

    public abstract void queryAtomPage(String tag,String url,NetWorkCallback callback);

    public abstract void cancelQuery(String tag);
}
