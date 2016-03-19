package com.leo.enjoytime.fragment;

import android.support.v4.app.Fragment;

import com.leo.enjoytime.App;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by leo on 16/3/19.
 */
public abstract class BaseFragment extends Fragment {

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}