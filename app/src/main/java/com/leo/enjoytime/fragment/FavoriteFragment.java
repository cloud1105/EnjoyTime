package com.leo.enjoytime.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.Entry;
import com.leo.enjoytime.utils.Utils;
import com.leo.enjoytime.view.SwipyRefreshLayout;
import com.leo.enjoytime.view.SwipyRefreshLayoutDirection;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.Date;
import java.util.List;

/**
 * A simple {@link BaseFragment} subclass.
 */
public class FavoriteFragment extends BaseFragment {
    private SwipyRefreshLayout swipyRefreshLayout;
    private RecyclerView recyclerView;
    private FavoriteItemAdapter adapter;
    private DBManager manager;
    private int hasLoadPage = 0;
    private boolean isLoadMore = true;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        manager = App.getDbmanager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_dev_common_item, container, false);
        swipyRefreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle);
        swipyRefreshLayout.setDirection(SwipyRefreshLayoutDirection.BOTTOM);
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (direction == SwipyRefreshLayoutDirection.BOTTOM) {
                    if (isLoadMore) {
                        hasLoadPage++;
                        loadMoreData(rootView);
                    } else {
                        Snackbar.make(rootView, "没有更多数据了", Snackbar.LENGTH_SHORT).show();
                    }
                }
                swipyRefreshLayout.setRefreshing(false);
            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false));
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        List list = manager.getFavorlist(Const.LIMIT_COUNT, hasLoadPage);

        if (list != null && list.size() != 0){
            adapter = new FavoriteItemAdapter(list);
            recyclerView.setAdapter(adapter);
        }else{
            // TODO: 16/4/3  no more data text
        }
        return  rootView;
    }

    private void loadMoreData(View rootView) {
        List<Entry> list = manager.getFavorlist(Const.LIMIT_COUNT, hasLoadPage);
        if (list !=null && list.size()!= 0){
            if (list.size()<Const.LIMIT_COUNT){
                isLoadMore = false;
            }
            adapter.addMoreList( manager.getFavorlist(Const.LIMIT_COUNT,hasLoadPage));
        }else{
            Snackbar.make(rootView, "没有更多数据了", Snackbar.LENGTH_SHORT).show();
        }
        swipyRefreshLayout.setRefreshing(false);
    }

    class FavoriteItemAdapter extends RecyclerView.Adapter<FavoriteItemAdapter.ViewHolder>{

        private List<Entry> sourceList;

        public FavoriteItemAdapter(List<Entry> list){
            this.sourceList = list;
        }

        public void addMoreList(List<Entry> moreList) {
            sourceList.addAll(moreList);
            notifyDataSetChanged();
        }

        public void removeItem(Entry entry){
            sourceList.remove(entry);
            notifyDataSetChanged();
        }


        @Override
        public FavoriteItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.article_item,parent,false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(FavoriteItemAdapter.ViewHolder holder, int position) {
               holder.bindData(sourceList.get(position),position);
        }

        @Override
        public int getItemCount() {
            return sourceList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txvDesc,txvCreateAt;
            private LikeButton btnLike;

            void bindData(final Entry entry, final int position){
                txvDesc.setText(entry.getDesc());
                Date temp = Utils.formatDateFromStr(entry.getCreate_at());
                String formatDate = Utils.getFormatDateStr(temp);
                txvCreateAt.setText(formatDate);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.gotoWebView(getActivity(), entry.getUrl());
                    }
                });
                btnLike.setLiked(true);
                btnLike.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {

                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        entry.setFavor_flag(Const.UNLIKE);
                        mHandler.removeMessages(MSG_UPDATE_ENTRY);
                        Message message = Message.obtain();
                        message.what = MSG_UPDATE_ENTRY;
                        message.getData().putParcelable("entry", entry);
                        mHandler.sendMessage(message);
                        adapter.removeItem(entry);
                    }
                });
            }


            public ViewHolder(View itemView) {
                super(itemView);
                txvDesc = (TextView) itemView.findViewById(R.id.txv_article_desc);
                txvCreateAt = (TextView) itemView.findViewById(R.id.txv_create_date);
                btnLike = (LikeButton) itemView.findViewById(R.id.like_button);
            }
        }
    }

}
