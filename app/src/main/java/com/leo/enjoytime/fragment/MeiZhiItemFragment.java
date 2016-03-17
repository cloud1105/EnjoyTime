package com.leo.enjoytime.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.activity.ImageViewActivity;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.Entry;
import com.leo.enjoytime.network.VolleyUtils;
import com.leo.enjoytime.view.SpacesItemDecoration;
import com.leo.enjoytime.view.SwipyRefreshLayout;
import com.leo.enjoytime.view.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MeiZhiItemFragment extends Fragment {
    private static final String TAG = MeiZhiItemFragment.class.getSimpleName();
    private SwipyRefreshLayout refreshLayout;
    private RecyclerView recyclerView;
    private int hasLoadPage = 0;
    private boolean isLoadMore = true;
    private boolean isNew = false;
    private DBManager dbManager;
    private MeizhiAdapter meizhiAdapter;
    private Response.Listener responseListener;
    private Response.ErrorListener errorListener;

    public MeiZhiItemFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray array = new JSONArray();
                try {
                    String result = response.getString("error");
                    if ("false".equals(result)) {
                        array = response.getJSONArray("results");
                    } else {
                        Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                        refreshLayout.setRefreshing(false);
                        Log.e(TAG, "no more data");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "parse JSONObject error :" + e.getLocalizedMessage());
                }
                if (array.length() == 0) {
                    Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(false);
                    Log.e(TAG, "no data");
                    return;
                }
                List<Entry> list = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object;
                    try {
                        object = array.getJSONObject(i);
                        Entry entry = new Entry();
                        entry.setUrl(object.getString("url"));
                        entry.setType(object.getString("type"));
                        entry.setDesc(object.getString("desc"));
                        entry.setCreate_at(object.getString("publishedAt"));
                        entry.setFavor_flag(Const.UNLIKE);
                        list.add(entry);
                        if (dbManager.getDataByUrl(entry.getUrl()) == null) {
                            isNew = true;
                            dbManager.insertData(entry);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                meizhiAdapter.addItems(list);
                if (list.size() == Const.LIMIT_COUNT) {
                    isLoadMore = true;
                } else {
                    isLoadMore = false;
                }
                refreshLayout.setRefreshing(false);

            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                refreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "send volley request error, msg :" + error.getLocalizedMessage());
            }
        };
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
        dbManager = App.getDbmanager();
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
            public void onItemclick(View view, final Entry entry) {
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
                List<Entry> list = dbManager.getDataList(Const.LIMIT_COUNT, 0, Const.REQUEST_TYPE_MEIZHI);
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
        VolleyUtils.queryGanhuo(TAG, Const.REQUEST_TYPE_MEIZHI, hasLoadPage, Const.LIMIT_COUNT,
                responseListener, errorListener);
    }

    private class MeizhiAdapter extends RecyclerView.Adapter<MeizhiAdapter.VH> {
        private Context context;
        List<Entry> entryList = new ArrayList<>();
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

        public void addItems(List<Entry> list) {
            entryList.addAll(list);
            notifyDataSetChanged();
        }

        public ArrayList getMeiZhiUrlList(){
            ArrayList list = new ArrayList();
            for (int i = 0;i<entryList.size();i++){
               Entry entry = entryList.get(i);
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
            private NetworkImageView imageView;

            public VH(View itemView) {
                super(itemView);
                imageView = (NetworkImageView) itemView.findViewById(R.id.img_meizhi);
            }

            void bindData(Entry entry) {
                imageView.setDefaultImageResId(R.drawable.default_image);
                imageView.setErrorImageResId(R.drawable.default_image);
                VolleyUtils.setMeizhiImg(imageView, entry.getUrl());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemclick(View view, Entry entry);
    }
}
