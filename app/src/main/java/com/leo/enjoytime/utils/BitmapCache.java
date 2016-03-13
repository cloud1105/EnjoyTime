package com.leo.enjoytime.utils;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by leo on 16/3/13.
 */
public class BitmapCache implements ImageLoader.ImageCache {
    @Override
    public Bitmap getBitmap(String url) {
        return ImageFileCacheUtils.getInstance().getImage(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        ImageFileCacheUtils.getInstance().saveBitmap(bitmap, url);
    }
}
