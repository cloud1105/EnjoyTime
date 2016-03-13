package com.leo.enjoytime.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.utils.ImageFileCacheUtils;

import uk.co.senab.photoview.PhotoView;

public class BigImageViewActivity extends AppCompatActivity {
    public final static String URL_PARAM = "url";
    public static final String TAG = BigImageViewActivity.class.getSimpleName();
    private PhotoView photoView;
//    PhotoViewAttacher mAttacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_big_image_view);
        photoView = (PhotoView) findViewById(R.id.photo);
        String url = getIntent().getStringExtra(URL_PARAM);
        if (url == null){
            Log.d(TAG, "big imageview load url is null");
            return;
        }
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
//        mAttacher = new PhotoViewAttacher(photoView);
        photoView.setImageBitmap(ImageFileCacheUtils.getInstance().getBigImage(url,
                Const.REQ_W,Const.REQ_H));
        // If you later call mImageView.setImageDrawable/setImageBitmap/setImageResource/etc then you just need to call
//        mAttacher.update();

    }
}
