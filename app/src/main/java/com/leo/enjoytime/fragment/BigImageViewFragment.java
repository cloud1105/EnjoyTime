package com.leo.enjoytime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.utils.ImageFileCacheUtils;

import uk.co.senab.photoview.PhotoView;

public class BigImageViewFragment extends Fragment {
    public final static String URL_PARAM = "url";
    public static final String TAG = BigImageViewFragment.class.getSimpleName();
    private PhotoView photoView;
    private String url;
//    PhotoViewAttacher mAttacher;


    public BigImageViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param url the url of image.
     * @return A new instance of fragment BigImageViewFragment.
     */
    public static BigImageViewFragment newInstance(String url) {
        BigImageViewFragment fragment = new BigImageViewFragment();
        Bundle args = new Bundle();
        args.putString(URL_PARAM, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL_PARAM);
        }
        if (TextUtils.isEmpty(url)){
            throw new IllegalArgumentException("BigImageViewFragment: url is null");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_big_image_view,container,false);
        // Attach a PhotoViewAttacher, which takes care of all of the zooming functionality.
//        mAttacher = new PhotoViewAttacher(photoView);
        photoView = (PhotoView) rootView.findViewById(R.id.photo);
        photoView.setImageBitmap(ImageFileCacheUtils.getInstance().getBigImage(url,
                Const.REQ_W,Const.REQ_H));
        // If you later call mImageView.setImageDrawable/setImageBitmap/setImageResource/etc then you just need to call
//        mAttacher.update();
        return rootView;
    }
}
