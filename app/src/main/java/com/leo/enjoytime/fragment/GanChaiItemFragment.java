package com.leo.enjoytime.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.GanChaiEntry;
import com.leo.enjoytime.model.JsonEntry;
import com.leo.enjoytime.network.AbstractNewWorkerManager;
import com.leo.enjoytime.network.NetWorkCallback;
import com.leo.enjoytime.utils.Utils;
import com.leo.enjoytime.view.SwipyRefreshLayout;
import com.leo.enjoytime.view.SwipyRefreshLayoutDirection;
import com.like.LikeButton;
import com.like.OnLikeListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link BaseFragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link GanChaiItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GanChaiItemFragment extends BaseFragment implements NetWorkCallback{
    private static final String TYPE_PARAM = "type";
    private static final String TAG = GanChaiItemFragment.class.getSimpleName();
    private int mType;

    private SwipyRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private GanChaiRcvAdapter adapter;
    private DBManager dbManager;
    private Response.Listener responseListener;
    private Response.ErrorListener errorListener;
    private int hasLoadPage = 0;
    private boolean isLoadMore = true;
    private AbstractNewWorkerManager newWorkerManager;

    public GanChaiItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param type type with android,ios,html5.
     * @return A new instance of fragment GanChaiItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GanChaiItemFragment newInstance(int type) {
        GanChaiItemFragment fragment = new GanChaiItemFragment();
        Bundle args = new Bundle();
        args.putInt(TYPE_PARAM, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getInt(TYPE_PARAM,1);
        }
        newWorkerManager = App.getNetWorkManager();
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
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(recyclerView.getContext()));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbManager = App.getDbManager();
        Context context = getContext();
        adapter = new GanChaiRcvAdapter(context);
        adapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemclick(View view, final GanChaiEntry entry) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.gotoWebView(getActivity(), entry.getSource());
                    }
                });
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<GanChaiEntry> list = dbManager.getDigestList(Const.LIMIT_COUNT, 0, String.valueOf(mType));
                if (list != null && list.size() != 0) {
                    adapter.addItems(list);
                    hasLoadPage = 1;
                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    loadNewData(true);
                }
            }
        }, 200);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(context,
//                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        newWorkerManager.cancelQuery(TAG);
    }

    private void loadNewData(boolean isNew) {
        if (isNew) {
            adapter.clearList();
            hasLoadPage = 1;
        }else{
            hasLoadPage++;
        }
        newWorkerManager.queryGanChai(TAG, mType, hasLoadPage, Const.LIMIT_COUNT,this);
    }

    @Override
    public void onSuccess(List<? extends JsonEntry> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        adapter.addItems((List<GanChaiEntry>) list);
        if(list.size() == Const.LIMIT_COUNT) {
            isLoadMore = true;
        }else{
            isLoadMore = false;
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(String errorMsg) {
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
        Log.e(TAG, "send volley request error, msg :" + errorMsg);
    }

    public interface OnItemClickListener {
        void onItemclick(View view, GanChaiEntry entry);
    }

    private class GanChaiRcvAdapter extends RecyclerView.Adapter<GanChaiRcvAdapter.ViewHolder>{
        private Context context;
        private List digestList = new ArrayList();
        private OnItemClickListener itemClickListener;

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public GanChaiRcvAdapter(Context context) {
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(context).
                    inflate(R.layout.fragment_ganchai_item, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            final GanChaiEntry entry = (GanChaiEntry) digestList.get(position);
            itemClickListener.onItemclick(holder.itemView, entry);
            holder.bind(entry);
        }

        @Override
        public int getItemCount() {
            return digestList.size();
        }

        public void clearList() {
            digestList.clear();
        }

        public void addItems(List<GanChaiEntry> list) {
            digestList.addAll(list);
            notifyDataSetChanged();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView txvTitle;
            TextView txvSummary;
            ImageView imgThumbnail;
            LikeButton likeButton;
            public ViewHolder(View itemView) {
                super(itemView);
                txvTitle = (TextView) itemView.findViewById(R.id.txv_title);
                txvSummary = (TextView) itemView.findViewById(R.id.txv_summary);
                imgThumbnail = (ImageView) itemView.findViewById(R.id.img_thumbnail);
                likeButton = (LikeButton) itemView.findViewById(R.id.like_button);
            }

            void bind(final GanChaiEntry entry){
                txvTitle.setText(entry.getTitle());
                if (!TextUtils.isEmpty(entry.getSummary())) {
                    txvSummary.setText(entry.getSummary());
                }
                if (!TextUtils.isEmpty(entry.getThumbnail())){
                    newWorkerManager.setMeizhiImg(getContext(),imgThumbnail, entry.getThumbnail(),GanChaiItemFragment.this);
                }
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
                        message.getData().putParcelable("entry", entry);
                        mHandler.sendMessage(message);
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        entry.setFavor_flag(Const.UNLIKE);
                        mHandler.removeMessages(MSG_UPDATE_ENTRY);
                        Message message = Message.obtain();
                        message.what = MSG_UPDATE_ENTRY;
                        message.getData().putParcelable("entry", entry);
                        mHandler.sendMessage(message);
                    }
                });
            }
        }

    }
}
