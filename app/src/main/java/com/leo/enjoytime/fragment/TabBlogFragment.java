package com.leo.enjoytime.fragment;

import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.leo.enjoytime.R;
import com.leo.enjoytime.activity.AtomActivity;
import com.leo.enjoytime.activity.RssReaderActivity;
import com.leo.enjoytime.activity.WebViewActivity;
import com.leo.enjoytime.model.BlogEntry;
import com.leo.enjoytime.view.DividerItemDecoration;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TabBlogFragment extends Fragment {

    private final static String TAG = TabBlogFragment.class.getSimpleName();

    private RecyclerView recyclerView;

    private BlogAdapter adapter;

    public TabBlogFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        XmlResourceParser parser = getResources().getXml(R.xml.blog_list);
        List<BlogEntry> blogList = new ArrayList<>();
        BlogEntry entry = null;
        try {
            int eventType = parser.getEventType();
            while (eventType != XmlResourceParser.END_DOCUMENT){
                if (eventType == XmlResourceParser.START_TAG){
                    String name = parser.getName();
                    switch (name){
                        case "item":
                            entry = new BlogEntry();
                            //读取xml属性
                            String intentClass = parser.getAttributeValue(null,"class");
                            if (intentClass.equalsIgnoreCase("rss")){
                                entry.setIntentActivity(RssReaderActivity.class);
                            }else if (intentClass.equalsIgnoreCase("html")){
                                entry.setIntentActivity(WebViewActivity.class);
                            }else if (intentClass.equalsIgnoreCase("atom")){
                                entry.setIntentActivity(AtomActivity.class);
                            }
                            break;
                        case "title":
                            if (entry != null){
                                //读取XML项中间的内容
                                entry.setTitle(parser.nextText());
                            }
                            break;
                        case "description":
                            if (entry != null){
                                entry.setDesc(parser.nextText());
                            }
                            break;
                        case "link":
                            if (entry != null){
                                entry.setUrl(parser.nextText());
                            }
                            break;
                        default:
                            break;
                    }
                }else if (eventType == XmlResourceParser.END_TAG){
                    if (parser.getName().equalsIgnoreCase("item") && entry != null) {
                        blogList.add(entry);
                    }
                }

                //如果xml没有结束，则导航到下一个item节点
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(TAG,"xml parse error :"+e.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG,"xml parse error IOException:"+e.getLocalizedMessage());
        }

        adapter = new BlogAdapter();
        adapter.setList(blogList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_tab_blog, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        return rootView;
    }

    class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.ViewHolder>{

        private List<BlogEntry> list;

        public void setList(List<BlogEntry> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(getContext()).inflate(R.layout.blog_item,parent,false);
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

            public void bindData(final BlogEntry entry){
                if (entry == null){
                    return;
                }
                txvTitle.setText(entry.getTitle());
                txvDesc.setText(entry.getDesc());
                this.itemView.setTag(entry.getClass());
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(),entry.getIntentActivity());
                        intent.putExtra("title",entry.getTitle());
                        intent.putExtra("url",entry.getUrl());
                        startActivity(intent);
                    }
                });
            }
        }

    }


}