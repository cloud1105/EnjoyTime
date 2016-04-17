package com.leo.enjoytime.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.Entry;
import com.leo.enjoytime.network.AbstractNewWorkerManager;
import com.leo.enjoytime.network.NetWorkCallback;
import com.leo.enjoytime.utils.LogUtils;
import com.leo.enjoytime.utils.Utils;
import com.leo.enjoytime.view.SwipyRefreshLayout;
import com.leo.enjoytime.view.SwipyRefreshLayoutDirection;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * android and ios's Fragment
 * Created by leo on 16/3/19.
 */
public class DevCommonItemFragment extends BaseFragment implements NetWorkCallback {
    private static final String TAG = DevCommonItemFragment.class.getSimpleName();
    private static final String TYPE = "type";
    private String articleType;
    private SwipyRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DBManager dbManager;
    private AbstractNewWorkerManager newWorkerManager;
    private GanhuoRcyAdapter adapter;
    private int hasLoadPage = 0;
    private boolean isLoadMore = true;


    public DevCommonItemFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type article type.
     * @return A new instance of fragment DevCommonFragment.
     */
    public static DevCommonItemFragment newInstance(String type) {
        DevCommonItemFragment fragment = new DevCommonItemFragment();
        Bundle args = new Bundle();
        args.putString(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            articleType = getArguments().getString(TYPE);
        }else{
            throw new IllegalArgumentException("no arguments find in DevCommonItemFragment");
        }
        newWorkerManager = App.getNetWorkManager();
    }


    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Entry> list = dbManager.getDataList(Const.LIMIT_COUNT, 0, articleType);
                if (list != null && list.size() != 0) {
                    adapter.addItems(list);
                    hasLoadPage = 1;
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    loadNewData(true);
                }
            }
        }, 200);
    }


    @Override
    public void onPause() {
        super.onPause();
        newWorkerManager.cancelQuery(TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbManager = App.getDbManager();
        Context context = getContext();
        adapter = new GanhuoRcyAdapter(context);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemclick(View view, final Entry entry) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.gotoWebView(getActivity(),entry.getUrl());
                    }
                });
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }


    private void loadNewData(boolean isNew) {
        if (isNew) {
            adapter.clearList();
            hasLoadPage = 1;
        } else {
            hasLoadPage++;
        }
        newWorkerManager.queryGanhuo(TAG, articleType, hasLoadPage, Const.LIMIT_COUNT,this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_dev_common_item, container, false);
        swipeRefreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.TOP) {
                    loadNewData(true);
                } else if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    if (isLoadMore) {
                        loadNewData(false);
                    } else {
                        Snackbar.make(rootView, "没有更多数据了", Snackbar.LENGTH_SHORT).show();
                    }
                }
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        // TODO: WiFi网络或数据网络打开时加载最新网络数据  16/4/12
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        return rootView;

    }

    @Override
    public void onSuccess(List<Entry> list) {
        if (list == null || list.size() == 0) {
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        adapter.addItems(list);
        if (list.size() == Const.LIMIT_COUNT) {
            isLoadMore = true;
        } else {
            isLoadMore = false;
        }
        if (newWorkerManager.isNew || 1 == hasLoadPage){
            Snackbar.make(recyclerView, R.string.snackbar_new_msg,Snackbar.LENGTH_SHORT).show();
        }
        if (swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onError(Exception e) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
        LogUtils.loggerE(TAG, "request error, msg :" + e.getLocalizedMessage());
    }

    public interface OnItemClickListener {
        void onItemclick(View view, Entry entry);
    }

    private class GanhuoRcyAdapter extends RecyclerView.Adapter<GanhuoRcyAdapter.ViewHolder> {
        private Context context;
        List<Entry> entryList = new ArrayList<>();
        private OnItemClickListener itemClickListener;

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public GanhuoRcyAdapter(Context context) {
            this.context = context;
        }

        public void clearList() {
            entryList.clear();
        }

        public void addItems(List<Entry> list) {
            entryList.addAll(list);
            notifyDataSetChanged();
        }


        @Override
        public GanhuoRcyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View root = LayoutInflater.from(context).inflate(R.layout.article_item, parent, false);
            return new GanhuoRcyAdapter.ViewHolder(root);
        }

        @Override
        public void onBindViewHolder(final GanhuoRcyAdapter.ViewHolder holder, final int position) {
            final Entry entry = entryList.get(position);
            itemClickListener.onItemclick(holder.itemView, entry);
            holder.bindData(entry);
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txvDesc;
            private TextView date;
            private LikeButton likeButton;

            void bindData(final Entry entry) {
                String dateStr = entry.getCreate_at();
                String text = entry.getDesc();
                Date temp = Utils.formatDateFromStr(dateStr);
                String formatDate = Utils.getFormatDateStr(temp);
                date.setText(formatDate);
                txvDesc.setText(text);
                if (entry.getFavor_flag() == 1) {
                    likeButton.setLiked(true);
                }else {
                    likeButton.setLiked(false);
                }
                likeButton.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        entry.setFavor_flag(Const.LIKE);
                        mHandler.removeMessages(MSG_UPDATE_ENTRY);
                        Message message = Message.obtain();
                        message.what = MSG_UPDATE_ENTRY;
                        message.getData().putParcelable("entry",entry);
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        entry.setFavor_flag(Const.UNLIKE);
                        mHandler.removeMessages(MSG_UPDATE_ENTRY);
                        Message message = Message.obtain();
                        message.what = MSG_UPDATE_ENTRY;
                        message.getData().putParcelable("entry",entry);
                        mHandler.sendMessage(message);
                    }
                });
            }

            public ViewHolder(View itemView) {
                super(itemView);
                date = (TextView) itemView.findViewById(R.id.txv_create_date);
                txvDesc = (TextView) itemView.findViewById(R.id.txv_article_desc);
                likeButton = (LikeButton) itemView.findViewById(R.id.like_button);
            }
        }

    }

}
