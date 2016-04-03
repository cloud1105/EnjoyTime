package com.leo.enjoytime.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.leo.enjoytime.fragment.FavoriteFragment;

/**
 * Created by leo on 16/4/3.
 */
public class FavorAdapter extends FragmentStatePagerAdapter {
    public FavorAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return new FavoriteFragment();
    }

    @Override
    public int getCount() {
        return 1;
    }
}
