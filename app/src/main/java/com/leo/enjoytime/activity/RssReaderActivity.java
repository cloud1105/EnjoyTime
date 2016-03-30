package com.leo.enjoytime.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.leo.enjoytime.R;
import com.leo.enjoytime.model.Entry;
import com.leo.enjoytime.network.VolleyUtils;
import com.leo.enjoytime.utils.Utils;
import com.leo.enjoytime.view.DividerItemDecoration;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RssReaderActivity extends AppCompatActivity {

    private final static String TAG = RssReaderActivity.class.getSimpleName();
    private String rssUrl;
    private Response.Listener<XmlPullParser> listener;
    private Response.ErrorListener errorListener;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private RssReaderAdapter adapter;
    private static final int CONVERT_XML_TO_ENTRY = 1;
    @SuppressWarnings("handlerleak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CONVERT_XML_TO_ENTRY:
                    List<Entry> list = msg.getData().getParcelableArrayList("list");
                    if (list != null && list.size() != 0) {
                        if (adapter == null) {
                            adapter = new RssReaderAdapter();
                        }
                        adapter.setList(list);
                    }

                    setProgressBarIndeterminateVisibility(false);
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
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        adapter = new RssReaderAdapter();
        recyclerView.setAdapter(adapter);
        setProgressBarIndeterminate(true);
        setProgressBarIndeterminateVisibility(true);
        Intent intent = getIntent();
        if (intent != null) {
            setTitle(intent.getStringExtra("title"));
            rssUrl = intent.getStringExtra("url");
        }
        if (TextUtils.isEmpty(rssUrl)) {
            return;
        }

        listener = new Response.Listener<XmlPullParser>() {
            @Override
            public void onResponse(final XmlPullParser response) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayList<Entry> entryList = parseRssXml(response);
                        Message msg = Message.obtain();
                        msg.what = CONVERT_XML_TO_ENTRY;
                        msg.getData().putParcelableArrayList("list",entryList);
                        mHandler.sendMessage(msg);
                    }
                }).start();
            }
        };
        errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "网络错误，请检查网络后重试", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "send volley request error, msg :" + error.getLocalizedMessage());
            }
        };
        VolleyUtils.queryRssPage(TAG, rssUrl, listener, errorListener);
    }

    @NonNull
    private ArrayList<Entry> parseRssXml(XmlPullParser response) {
        ArrayList<Entry> list = new ArrayList<>();
        Entry entry = null;
        try {
            int eventType = response.getEventType();
            while(XmlPullParser.END_DOCUMENT != eventType){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        String tag = response.getName();
                        if ("item".equalsIgnoreCase(tag)){
                            entry = new Entry();

                        }else if (entry != null){
                            if ("title".equalsIgnoreCase(tag)){
                                entry.setTitle(new String(response.nextText().getBytes(),"UTF-8"));
                            }else if ("description".equalsIgnoreCase(tag)){
                                entry.setDesc(new String(response.nextText().getBytes(),"UTF-8"));
                            }else if ("link".equalsIgnoreCase(tag)){
                                entry.setUrl(response.nextText());
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (response.getName().equalsIgnoreCase("item") && entry != null) {
                            list.add(entry);
                            entry = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = response.next();
            }
        }catch (XmlPullParserException e) {
            Log.e(TAG, "xml parse error :" + e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG,"xml parse error IOException:"+e.getLocalizedMessage());
        }
        return list;
    }

    @Override
    protected void onPause() {
        super.onPause();
        VolleyUtils.cancelQuery(TAG);
    }

    class RssReaderAdapter extends RecyclerView.Adapter<RssReaderAdapter.ViewHolder>{

        private List<Entry> list;

        public void setList(List<Entry> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(RssReaderActivity.this).inflate(R.layout.blog_item,parent,false);
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
            }else{
                return 0;
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder{
            private TextView txvTitle;
            private TextView txvDesc;

            public ViewHolder(View itemView) {
                super(itemView);
                txvTitle = (TextView) itemView.findViewById(R.id.txv_title);
                txvDesc = (TextView) itemView.findViewById(R.id.txv_desc);
            }

            public void bindData(final Entry entry){
                if (entry == null){
                    return;
                }
                txvTitle.setText(entry.getTitle());
                txvDesc.setText(Html.fromHtml(entry.getDesc()));
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.gotoWebView(RssReaderActivity.this,entry.getUrl());
                    }
                });
            }

        }
    }
}
