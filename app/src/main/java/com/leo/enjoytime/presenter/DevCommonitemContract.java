package com.leo.enjoytime.presenter;

import com.leo.enjoytime.BasePresenter;
import com.leo.enjoytime.BaseView;
import com.leo.enjoytime.model.GanhuoEntry;
import com.leo.enjoytime.model.JsonEntry;

import java.util.List;

/**
 * Created by leo on 16/5/1.
 */
public interface DevCommonitemContract {
    interface View extends BaseView<Presenter> {
        void showResumeView(List<GanhuoEntry> list);
        void clearList();
        void showSuccessView(List<? extends JsonEntry>list);
        void showErrorView(String errorMsg);
        void showNewMsgView();
    }

    interface Presenter extends BasePresenter {
        void loadNewData(boolean isNew);
        void incomingNewItem();
        void likeOrUnlike(int flag, final GanhuoEntry entry);
    }
}
