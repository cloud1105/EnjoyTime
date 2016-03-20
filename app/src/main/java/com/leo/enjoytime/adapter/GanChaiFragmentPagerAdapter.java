package com.leo.enjoytime.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.leo.enjoytime.activity.MainActivity;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.fragment.GanChaiItemFragment;

/**
 * Created by leo on 16/3/19.
 */
public class GanChaiFragmentPagerAdapter extends FragmentStatePagerAdapter {
    private static final int ANDROID_TYPE = 0;
    public static final int BLOG_TYPE = 1;

    public GanChaiFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ANDROID_TYPE:
                return GanChaiItemFragment.newInstance(ANDROID_TYPE);
            case BLOG_TYPE:

            default:
                return GanChaiItemFragment.newInstance(ANDROID_TYPE);
        }

    }

    @Override
    public int getCount() {
        return MainActivity.GANCHAI_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ANDROID_TYPE:
                return Const.TITLE_ANDROID;
            case BLOG_TYPE:
                return Const.TITLE_BLOG;
            default:
                return Const.TITLE_ANDROID;
        }
    }

}
