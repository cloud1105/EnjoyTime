package com.leo.enjoytime.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.leo.enjoytime.R;
import com.leo.enjoytime.fragment.BigImageViewFragment;
import com.leo.enjoytime.view.ViewPagerFixed;

import java.util.List;

public class ImageViewActivity extends AppCompatActivity {

    public final static String CURRENT_URL_PARAM = "url";
    public final static String INDEX = "index";
    public final static String URLS_PARAM = "urls";
    public static final String TAG = BigImageViewFragment.class.getSimpleName();
    private String current_url;
    private List urlList;
    private int index;
    private ViewPagerFixed pager;
    private TextView txvIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        if (getIntent() == null) {
            Log.d(TAG, "getIntent() is null");
            return;
        }
        Bundle bundle = getIntent().getExtras();
        current_url = bundle.getString(CURRENT_URL_PARAM);
        urlList = bundle.getStringArrayList(URLS_PARAM);
        index = bundle.getInt(INDEX);
        if (current_url == null) {
            Log.d(TAG, "current_url is null");
            return;
        }
        if (urlList == null || urlList.size() == 0) {
            Log.d(TAG, "urlList is empty");
            return;
        }

        pager = (ViewPagerFixed) findViewById(R.id.pager_pic);
        ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager());
        adapter.setUrlList(urlList);
        pager.setAdapter(adapter);
        if (savedInstanceState != null) {
            index = savedInstanceState.getInt(INDEX);
        }
        pager.setCurrentItem(index);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        position + 1, pager.getAdapter().getCount());
                txvIndex.setText(text);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        txvIndex = (TextView) findViewById(R.id.indicator);
        String indexStr = getString(R.string.viewpager_indicator,index+1,urlList.size());
        txvIndex.setText(indexStr);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //save the Index of currently displayed page.
        outState.putInt(INDEX, pager.getCurrentItem());
    }

    class ImagePagerAdapter extends FragmentPagerAdapter {

        private List<String> urlList;

        public void setUrlList(List urlList) {
            this.urlList = urlList;
        }

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return BigImageViewFragment.newInstance(urlList.get(position));
        }

        @Override
        public int getCount() {
            return urlList == null ? 0 : urlList.size();
        }
    }
}
