package com.leo.enjoytime.presenter;

import com.leo.enjoytime.App;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.GanhuoEntry;
import com.leo.enjoytime.model.JsonEntry;
import com.leo.enjoytime.network.AbstractNewWorkerManager;
import com.leo.enjoytime.network.NetWorkCallback;
import java.util.List;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by leo on 16/5/1.
 */
public class DevCommonItemPresenter implements DevCommonitemContract.Presenter,NetWorkCallback {
    private DBManager dbManager;
    private AbstractNewWorkerManager newWorkerManager;
    private String type;
    private DevCommonitemContract.View view;
    private int hasLoadPage = 0;
    private String tag;

    public DevCommonItemPresenter(DevCommonitemContract.View view, String articleType,String tag) {
        this.view = view;
        dbManager = App.getDbManager();
        newWorkerManager = App.getNetWorkManager();
        type = articleType;
        this.tag = tag;
        view.setPresenter(this);
    }


    @Override
    public void start() {
        Observable.just(dbManager.getDataList(Const.LIMIT_COUNT, 0, type))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<GanhuoEntry>>() {
                    @Override
                    public void call(List<GanhuoEntry> ganhuoEntries) {
                        hasLoadPage = 1;
                        view.showResumeView(ganhuoEntries);
                    }
                });
    }

    @Override
    public void cancelQuery(String tag) {
        newWorkerManager.cancelQuery(tag);
    }

    @Override
    public void loadNewData(boolean isNew) {
        if (isNew) {
            view.clearList();
            hasLoadPage = 1;
        } else {
            hasLoadPage++;
        }
        newWorkerManager.queryGanhuo(tag, type, hasLoadPage, Const.LIMIT_COUNT,this);
    }

    @Override
    public void incomingNewItem() {
        if (newWorkerManager.isNew || 1 == hasLoadPage){
          view.showNewMsgView();
        }
    }

    @Override
    public void likeOrUnlike(int flag, final GanhuoEntry entry) {
        entry.setFavor_flag(flag);
        Observable.create(new Observable.OnSubscribe<GanhuoEntry>() {
            @Override
            public void call(Subscriber<? super GanhuoEntry> subscriber) {
                subscriber.onNext(entry);
                subscriber.onCompleted();
            }
        }).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(new Action1<GanhuoEntry>() {
                    @Override
                    public void call(GanhuoEntry ganhuoEntry) {
                        App.getDbManager().changeArticleToLikeOrUnlike(entry);
                    }
                });
    }

    @Override
    public void onSuccess(List<? extends JsonEntry> list) {
        view.showSuccessView(list);
    }

    @Override
    public void onError(String errorMsg) {
        view.showErrorView(errorMsg);
    }
}
