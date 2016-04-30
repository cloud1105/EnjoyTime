package com.leo.enjoytime.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.activity.ImageViewActivity;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.GanhuoEntry;
import com.leo.enjoytime.model.JsonEntry;
import com.leo.enjoytime.network.AbstractNewWorkerManager;
import com.leo.enjoytime.network.NetWorkCallback;
import com.leo.enjoytime.utils.LogUtils;
import com.leo.enjoytime.view.SpacesItemDecoration;
import com.leo.enjoytime.view.SwipyRefreshLayout;
import com.leo.enjoytime.view.SwipyRefreshLayoutDirection;

import java.util.ArrayList;
import java.util.List;

public class MeiZhiItemFragment extends BaseFragment implements NetWorkCallback{
    private static final String TAG = MeiZhiItemFragment.class.getSimpleName();
    private SwipyRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private int hasLoadPage = 0;
    private boolean isLoadMore = true;
    private DBManager dbManager;
    private AbstractNewWorkerManager newWorkerManager;
    private MeizhiAdapter meizhiAdapter;

    public MeiZhiItemFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newWorkerManager = App.getNetWorkManager();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_mei_zhi_item, container, false);
        refreshLayout = (SwipyRefreshLayout) rootView.findViewById(R.id.swipe_refresh);
        refreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
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
        refreshLayout.post(new Runnable() {
            @Override
            public void run() {
                refreshLayout.setRefreshing(true);
            }
        });
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycle);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbManager = App.getDbManager();
        Context context = getContext();
        meizhiAdapter = new MeizhiAdapter(context);
        recyclerView.setAdapter(meizhiAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new SpacesItemDecoration(16));

        /**
         *   fix RecyclerView bug see:
         *   http://stackoverflow.com/questions/26827222/how-to-change-contents-of-recyclerview-while-scrolling
         **/
        recyclerView.setOnTouchListener(
                new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (refreshLayout.isRefreshing()) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                }
        );
        meizhiAdapter.setItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemclick(View view, final GanhuoEntry entry) {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gotoBigMeizhi(entry.getUrl());
                    }
                });
            }
        });
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<GanhuoEntry> list = dbManager.getDataList(Const.LIMIT_COUNT, 0, Const.REQUEST_TYPE_MEIZHI);
                if (list != null && list.size() != 0) {
                    meizhiAdapter.addItems(list);
                    hasLoadPage = 1;
                    refreshLayout.setRefreshing(false);
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

    @SuppressWarnings("unchecked")
    private void gotoBigMeizhi(String url) {
        Intent intent = new Intent(getActivity(), ImageViewActivity.class);
        Bundle bundle = new Bundle();
        ArrayList list = meizhiAdapter.getMeiZhiUrlList();
        bundle.putString(ImageViewActivity.CURRENT_URL_PARAM,url);
        bundle.putStringArrayList(ImageViewActivity.URLS_PARAM, list);
        bundle.putInt(ImageViewActivity.INDEX, list.indexOf(url));
        intent.putExtras(bundle);
        startActivity(intent);
    }


    private void loadNewData(boolean isNew) {
        if (isNew) {
            meizhiAdapter.clearList();
            hasLoadPage = 1;
        } else {
            hasLoadPage++;
        }
        newWorkerManager.queryGanhuo(TAG, Const.REQUEST_TYPE_MEIZHI, hasLoadPage, Const.LIMIT_COUNT,this);
    }

    @Override
    public void onSuccess(List<? extends JsonEntry> list) {
        meizhiAdapter.addItems((List<GanhuoEntry>) list);
        if (list.size() == Const.LIMIT_COUNT) {
            isLoadMore = true;
        } else {
            isLoadMore = false;
        }
        if (newWorkerManager.isNew && 1 == hasLoadPage){
            Snackbar.make(recyclerView, R.string.snackbar_new_msg,Snackbar.LENGTH_SHORT).show();
        }
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onError(String errorMsg) {
        refreshLayout.setRefreshing(false);
        Toast.makeText(getContext(), "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
        LogUtils.loggerE(TAG, "send volley request error, msg :" + errorMsg);
    }

    private class MeizhiAdapter extends RecyclerView.Adapter<MeizhiAdapter.VH> {
        private Context context;
        List<GanhuoEntry> entryList = new ArrayList<>();
        private OnItemClickListener itemClickListener;

        public void setItemClickListener(OnItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        public MeizhiAdapter(Context context) {
            this.context = context;
        }

        public void clearList() {
            entryList.clear();
        }

        public void addItems(List<GanhuoEntry> list) {
            entryList.addAll(list);
            notifyDataSetChanged();
        }

        public ArrayList getMeiZhiUrlList(){
            ArrayList list = new ArrayList();
            for (int i = 0;i<entryList.size();i++){
                GanhuoEntry entry = entryList.get(i);
               list.add(i, entry.getUrl());
            }
            return list;
        }

        @Override
        public MeizhiAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MeizhiAdapter.VH(LayoutInflater.from(context).
                    inflate(R.layout.meizhi_item, parent, false));
        }

        @Override
        public void onBindViewHolder(MeizhiAdapter.VH holder, int position) {
            itemClickListener.onItemclick(holder.itemView, entryList.get(position));
            holder.bindData(entryList.get(position));
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            private ImageView imageView;

            public VH(View itemView) {
                super(itemView);
                imageView = (ImageView) itemView.findViewById(R.id.img_meizhi);
            }

            void bindData(GanhuoEntry entry) {
                newWorkerManager.setMeizhiImg(getContext(),imageView, entry.getUrl(),MeiZhiItemFragment.this);
            }
        }
    }

    public interface OnItemClickListener {
        void onItemclick(View view, GanhuoEntry entry);
    }
}
