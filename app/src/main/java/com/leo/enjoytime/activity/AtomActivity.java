package com.leo.enjoytime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.model.Atom;
import com.leo.enjoytime.model.BlogEntry;
import com.leo.enjoytime.model.JsonEntry;
import com.leo.enjoytime.network.AbstractNewWorkerManager;
import com.leo.enjoytime.network.NetWorkCallback;
import com.leo.enjoytime.utils.LogUtils;
import com.leo.enjoytime.utils.Utils;
import com.leo.enjoytime.view.DividerItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class AtomActivity extends AppCompatActivity implements NetWorkCallback{

    private final static String TAG = AtomActivity.class.getSimpleName();
    private String rssUrl;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RssReaderAdapter adapter;
    private static final int CONVERT_XML_TO_ENTRY = 1;
    private AbstractNewWorkerManager newWorkerManager;

    @SuppressWarnings("handlerleak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONVERT_XML_TO_ENTRY:
                    List<BlogEntry> list = msg.getData().getParcelableArrayList("list");
                    if (list != null && list.size() != 0) {
                        if (adapter == null) {
                            adapter = new RssReaderAdapter();
                        }
                        adapter.setList(list);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rss_reader);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));

        adapter = new RssReaderAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        if (intent != null) {
            setTitle(intent.getStringExtra("title"));
            rssUrl = intent.getStringExtra("url");
        }
        if (TextUtils.isEmpty(rssUrl)) {
            return;
        }

        newWorkerManager = App.getNetWorkManager();
        newWorkerManager.queryAtomPage(TAG, rssUrl,this);

    }



    @Override
    protected void onPause() {
        super.onPause();
        newWorkerManager.cancelQuery(TAG);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess(List<? extends JsonEntry> list) {
        Message msg = Message.obtain();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("list", (ArrayList<Atom>) list);
        msg.setData(bundle);
        msg.what = CONVERT_XML_TO_ENTRY;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onError(String errorMsg) {
        Toast.makeText(this, "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
        LogUtils.loggerE(TAG, "request error, msg :" + errorMsg);
    }

    class RssReaderAdapter extends RecyclerView.Adapter<RssReaderAdapter.ViewHolder> {

        private List<BlogEntry> list;

        public void setList(List<BlogEntry> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(AtomActivity.this).inflate(R.layout.blog_item, parent, false);
            return new ViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindData(list.get(position));
        }

        @Override
        public int getItemCount() {
            if (list != null && list.size() != 0) {
                return list.size();
            } else {
                return 0;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView txvTitle;
            private TextView txvDesc;

            public ViewHolder(View itemView) {
                super(itemView);
                txvTitle = (TextView) itemView.findViewById(R.id.txv_title);
                txvDesc = (TextView) itemView.findViewById(R.id.txv_desc);
            }

            public void bindData(final BlogEntry entry) {
                if (entry == null) {
                    return;
                }
                txvTitle.setText(entry.getTitle());
                txvDesc.setText(Html.fromHtml(entry.getDesc()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.gotoWebView(AtomActivity.this, entry.getUrl());
                    }
                });
            }

        }
    }

}
