package com.leo.enjoytime.fragment;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.leo.enjoytime.App;
import com.leo.enjoytime.model.Entry;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by leo on 16/3/19.
 */
public abstract class BaseFragment extends Fragment {

    protected static final int MSG_UPDATE_ENTRY = 0;
    protected Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_UPDATE_ENTRY:
                    final Entry entry = msg.getData().getParcelable("entry");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                        App.getDbmanager().changeArticleToLikeOrUnlike(entry);
                        }
                    }).start();
                    break;
                default:
                    break;
            }
        }
    };

    @Override public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}