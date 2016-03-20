package com.leo.enjoytime.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.leo.enjoytime.activity.MainActivity;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.fragment.DevCommonItemFragment;
import com.leo.enjoytime.fragment.MeiZhiItemFragment;

/**
 * Created by leo on 16/3/19.
 */
public class GanhuoFragmentPagerAdapter extends FragmentStatePagerAdapter {

    public GanhuoFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    public static final int ANDROID_TYPE = 0;
    public static final int IOS_TYPE = 1;
    public static final int MEIZHI_TYPE = 2;
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case ANDROID_TYPE:
                return DevCommonItemFragment.newInstance(Const.REQUEST_TYPE_ANDROID);
            case IOS_TYPE:
                return DevCommonItemFragment.newInstance(Const.REQUEST_TYPE_iOS);
            case MEIZHI_TYPE:
                return new MeiZhiItemFragment();
            default:
                return DevCommonItemFragment.newInstance(Const.REQUEST_TYPE_ANDROID);
        }

    }

    @Override
    public int getCount() {
        return MainActivity.GANHUO_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case ANDROID_TYPE:
                return Const.REQUEST_TYPE_ANDROID;
            case IOS_TYPE:
                return Const.REQUEST_TYPE_iOS;
            case MEIZHI_TYPE:
                return Const.REQUEST_TYPE_MEIZHI;
            default:
                return Const.REQUEST_TYPE_ANDROID;
        }
    }
}
