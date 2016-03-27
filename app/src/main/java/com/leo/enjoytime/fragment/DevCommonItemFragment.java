package com.leo.enjoytime.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.Entry;
import com.leo.enjoytime.network.VolleyUtils;
import com.leo.enjoytime.utils.Utils;
import com.leo.enjoytime.view.SwipyRefreshLayout;
import com.leo.enjoytime.view.SwipyRefreshLayoutDirection;
import com.like.LikeButton;
import com.like.OnLikeListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DevCommonItemFragment extends BaseFragment {
    private static final String TAG = DevCommonItemFragment.class.getSimpleName();
    private static final String TYPE = "type";
    private String articleType;
    private SwipyRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private DBManager dbManager;
    private GanhuoRcyAdapter adapter;
    private Response.Listener responseListener;
    private Response.ErrorListener errorListener;
    private int hasLoadPage = 0;
    private boolean isLoadMore = true;
    private boolean isNew = false;

    public DevCommonItemFragment() {
        // Required empty public constructor
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
        }
        responseListener = new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                List<Entry> list = parseEntryList(response);
                if (list == null) return;
                adapter.addItems(list);
                if (list.size() == Const.LIMIT_COUNT) {
                    isLoadMore = true;
                } else {
                    isLoadMore = false;
                }
                swipeRefreshLayout.setRefreshing(false);

            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "send volley request error, msg :" + error.getLocalizedMessage());
            }
        };
    }

    @Nullable
    private List<Entry> parseEntryList(JSONObject response) {
        JSONArray array = new JSONArray();
        array = parseJsonArrayFromUrlResponse(response, array);
        if (array == null || array.length() == 0) return null;
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
        return list;
    }

    @Nullable
    private JSONArray parseJsonArrayFromUrlResponse(JSONObject response, JSONArray array) {
        try {
            String result = response.getString("error");
            if ("false".equals(result)) {
                array = response.getJSONArray("results");
            } else {
                Toast.makeText(getContext(), "没有更多数据了", Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                Log.e(TAG, "no more data");
            }
        } catch (JSONException e) {
            Log.e(TAG, "parse JSONObject error :" + e.getLocalizedMessage());
        }
        if (array.length() == 0) {
            Toast.makeText(getContext(), "没有数据", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            Log.e(TAG, "no data");
            return null;
        }
        return array;
    }


    @Override
    public void onPause() {
        super.onPause();
        VolleyUtils.cancelQuery(TAG);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dbManager = App.getDbmanager();
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
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.addItemDecoration(new DividerItemDecoration(context,
//                DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
    }


    private void loadNewData(boolean isNew) {
        if (isNew) {
            adapter.clearList();
            hasLoadPage = 1;
        } else {
            hasLoadPage++;
        }
        VolleyUtils.queryGanhuo(TAG, articleType, hasLoadPage, Const.LIMIT_COUNT,
                responseListener, errorListener);
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
            holder.setLikeButtonListener(new OnLikeListener() {
                @Override
                public void liked(LikeButton likeButton) {
                    entry.setFavor_flag(Const.LIKE);
                    dbManager.changeArticleToLikeOrUnlike(entry);
                    notifyItemChanged(position);
                }

                @Override
                public void unLiked(LikeButton likeButton) {
                    entry.setFavor_flag(Const.UNLIKE);
                    dbManager.changeArticleToLikeOrUnlike(entry);
                    notifyItemChanged(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return entryList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txvDesc;
            private TextView date;
            private LikeButton likeButton;

            void bindData(Entry entry) {
                String dateStr = entry.getCreate_at();
                String text = entry.getDesc();
                Date temp = Utils.formatDateFromStr(dateStr);
                String formatDate = Utils.getFormatDateStr(temp);
                date.setText(formatDate);
                txvDesc.setText(text);
            }

            public void setLikeButtonListener(OnLikeListener likeListener) {
                likeButton.setOnLikeListener(likeListener);
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
