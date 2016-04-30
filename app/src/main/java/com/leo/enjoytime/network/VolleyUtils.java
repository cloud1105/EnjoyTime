package com.leo.enjoytime.network;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.leo.enjoytime.App;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.db.DBManager;
import com.leo.enjoytime.model.Atom;
import com.leo.enjoytime.model.GanChaiEntry;
import com.leo.enjoytime.model.GanhuoEntry;
import com.leo.enjoytime.model.Rss;
import com.leo.enjoytime.utils.BitmapCache;
import com.leo.enjoytime.utils.LogUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 16/3/9.
 */
public class VolleyUtils extends AbstractNewWorkerManager {
    private final static String TAG = VolleyUtils.class.getSimpleName();
    private static RequestQueue queue;
    private Response.Listener listenerForGanhuo = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            List<GanhuoEntry> list = parseGanhuoEntryList(response);
            if (callback != null) {
                callback.onSuccess(list);
            }
        }
    };

    private Response.Listener listenerForGanChai = new Response.Listener<JSONObject>() {
        @Override
        public void onResponse(JSONObject response) {
            List<GanChaiEntry> list = parseGanChaiEntryList(response);
            if (callback != null) {
                callback.onSuccess(list);
            }
        }
    };

    private Response.Listener listenerForAtom =  new Response.Listener<XmlPullParser>() {
        @Override
        public void onResponse(XmlPullParser response) {
            List<Atom> list = parseAtomXml(response);
            if (callback != null) {
                callback.onSuccess(list);
            }
        }
    };

    private Response.Listener listenerForRss =  new Response.Listener<XmlPullParser>() {
        @Override
        public void onResponse(XmlPullParser response) {
            List<Rss> list = parseRssXml(response);
            if (callback != null) {
                callback.onSuccess(list);
            }
        }
    };

    private  Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            if (callback != null) {
                callback.onError(error.getLocalizedMessage());
            }
        }
    };

    @Override
    public void init(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    @Override
    public void queryGanhuo(String tag, String type, int page, int count,NetWorkCallback callback) {
        setCallback(callback);
        String url;
        switch (type) {
            case Const.REQUEST_TYPE_ANDROID:
                url = Const.ANDROID_DATA_URL + count + "/" + page;
                break;
            case Const.REQUEST_TYPE_iOS:
                url = Const.IOS_DATA_URL + count + "/" + page;
                break;
            case Const.REQUEST_TYPE_MEIZHI:
                url = Const.MEIZHI_DATA_URL + count + "/" + page;
                try {
                    url = Const.GANHUO_HOST_URL + "/" + URLEncoder.encode("福利", "utf-8") + "/" + count + "/" + page;
                } catch (UnsupportedEncodingException e) {
                    LogUtils.loggerE(TAG,e.getLocalizedMessage());
                }
                break;
            default:
                url = Const.ALL_DATA_URL + count + "/" + page;
                break;

        }

        JsonObjectRequest objectRequest = new JsonObjectRequest(url, listenerForGanhuo, errorListener);
        objectRequest.setTag(tag);
        queue.add(objectRequest);
    }

    @Override
    public void queryGanChai(String tag, int type, int page, int count,NetWorkCallback callback) {
        setCallback(callback);
        String url = Const.getGanChaiDigestListUrl(type,page,count);
        JsonObjectRequest objectRequest = new JsonObjectRequest(url, listenerForGanChai, errorListener);
        objectRequest.setTag(tag);
        queue.add(objectRequest);
    }

    @Override
    public void setMeizhiImg(Context context,ImageView imageView, String url,NetWorkCallback callback) {
        setCallback(callback);
        ImageLoader imageLoader = new ImageLoader(queue, new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(imageView, R.drawable.default_image,R.drawable.default_image);
        imageLoader.get(url, listener);
    }

    @Override
    public void queryAtomPage(String tag,String url,NetWorkCallback callback){
        setCallback(callback);
        XMLRequest xmlRequest = new XMLRequest(url,listenerForAtom,errorListener);
        xmlRequest.setTag(tag);
        queue.add(xmlRequest);
    }

    @Override
    public void queryRssPage(String tag,String url,NetWorkCallback callback){
        setCallback(callback);
        XMLRequest xmlRequest = new XMLRequest(url,listenerForRss,errorListener);
        xmlRequest.setTag(tag);
        queue.add(xmlRequest);
    }

    @Override
    public void cancelQuery(String tag) {
        queue.cancelAll(tag);
    }

    public List<GanhuoEntry> parseGanhuoEntryList(JSONObject response) {
        JSONArray array = parseJsonArrayFromUrlResponse(response);
        if (array == null || array.length() == 0) {
            return null;
        }
        List<GanhuoEntry> list = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            JSONObject object;
            try {
                object = array.getJSONObject(i);
                GanhuoEntry entry = new GanhuoEntry();
                entry.setUrl(object.getString("url"));
                entry.setType(object.getString("type"));
                entry.setDesc(object.getString("desc"));
                entry.setPublishedAt(object.getString("publishedAt"));
                DBManager dbManager = App.getDbManager();
                GanhuoEntry entryInDb = dbManager.getDataByUrl(entry.getUrl());
                if ( entryInDb == null) {
                    isNew = true;
                    entry.setFavor_flag(Const.UNLIKE);
                    dbManager.insertData(entry);
                }else{
                    isNew = false;
                    entry.setFavor_flag(entryInDb.getFavor_flag());
                }
                list.add(entry);
            } catch (JSONException e) {
                LogUtils.loggerE(TAG,"getJSONObject error");
            }

        }
        return list;
    }

    private List<GanChaiEntry> parseGanChaiEntryList(JSONObject response) {
        JSONArray array;
        List<GanChaiEntry> list = new ArrayList<>();
        try {
            String result = response.getString("message");
            if ("Success".equals(result)) {
                JSONObject dataObject = response.getJSONObject("data");
                int curPage = Integer.parseInt(dataObject.getString("currentPage"));
                int allPage = Integer.parseInt(dataObject.getString("allPages"));
                //over the allpages
                if (curPage>allPage){
                    LogUtils.loggerD(TAG, "no more data");
                    return null;
                }
                array = dataObject.getJSONArray("result");
                if (array == null || array.length() == 0) return null;
                for (int i = 0; i < array.length(); i++) {
                    JSONObject object;
                    try {
                        object = array.getJSONObject(i);
                        GanChaiEntry entry = new GanChaiEntry();
                        entry.setType(Const.DIGEST_TYPE_ANDROID);
                        entry.setId(object.getInt("id")+"");
                        entry.setTitle(object.getString("title"));
                        entry.setSummary(object.getString("summary"));
                        entry.setThumbnail(object.getString("thumbnail"));
                        entry.setSource(object.getString("source"));
                        entry.setFavor_flag(Const.UNLIKE);
                        DBManager dbManager = App.getDbManager();
                        GanChaiEntry entryInDb = dbManager.getDigestByUrl(entry.getSource());
                        if ( entryInDb == null) {
                            isNew = true;
                            entry.setFavor_flag(Const.UNLIKE);
                            dbManager.insertDigest(entry);
                        }else{
                            entry.setFavor_flag(entryInDb.getFavor_flag());
                        }
                        list.add(entry);
                    } catch (JSONException e) {
                        LogUtils.loggerE(TAG, "getJSONObject error.");
                    }

                }
            }else{
                LogUtils.loggerE(TAG, "get error from server.");
            }
        }catch (JSONException e){
            LogUtils.loggerE(TAG, "parse JSONObject error :" + e.getLocalizedMessage());
        }

        return list;
    }



    private JSONArray parseJsonArrayFromUrlResponse(JSONObject response) {
        JSONArray array;
        try {
            String result = response.getString("error");
            if ("false".equals(result)) {
                array = response.getJSONArray("results");
            } else {
                LogUtils.loggerD(TAG, "no more data");
                return null;
            }
        } catch (JSONException e) {
            LogUtils.loggerE(TAG, "parse JSONObject error :" + e.getLocalizedMessage());
            return null;
        }
        if (array.length() == 0) {
            LogUtils.loggerD(TAG, "no data");
            return null;
        }
        return array;
    }

    private ArrayList<Atom> parseAtomXml(XmlPullParser response) {
        ArrayList<Atom> list = new ArrayList<>();
        Atom entry = null;
        try {
            int eventType = response.getEventType();
            while (XmlPullParser.END_DOCUMENT != eventType) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        String tag = response.getName();
                        if ("entry".equalsIgnoreCase(tag)) {
                            entry = new Atom();

                        } else if (entry != null) {
                            if ("title".equalsIgnoreCase(tag)) {
                                entry.setTitle(new String(response.nextText().getBytes(), "UTF-8"));
                            } else if ("content".equalsIgnoreCase(tag)) {
                                entry.setContent(new String(response.nextText().getBytes(), "UTF-8"));
                            } else if ("link".equalsIgnoreCase(tag)) {
                                entry.setLink(response.getAttributeValue(null, "href"));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (response.getName().equalsIgnoreCase("entry") && entry != null) {
                            list.add(entry);
                            entry = null;
                        }
                        break;
                    default:
                        break;
                }
                eventType = response.next();
            }
        } catch (XmlPullParserException e) {
            LogUtils.loggerE(TAG, "xml parse error :" + e.getLocalizedMessage());
        } catch (IOException e) {
            LogUtils.loggerE(TAG, "xml parse error IOException:" + e.getLocalizedMessage());
        }
        return list;
    }

    private ArrayList<Rss> parseRssXml(XmlPullParser response) {
        ArrayList<Rss> list = new ArrayList<>();
        Rss entry = null;
        try {
            int eventType = response.getEventType();
            while(XmlPullParser.END_DOCUMENT != eventType){
                switch (eventType){
                    case XmlPullParser.START_TAG:
                        String tag = response.getName();
                        if ("item".equalsIgnoreCase(tag)){
                            entry = new Rss();

                        }else if (entry != null){
                            if ("title".equalsIgnoreCase(tag)){
                                entry.setTitle(new String(response.nextText().getBytes(),"UTF-8"));
                            }else if ("description".equalsIgnoreCase(tag)){
                                entry.setDescription(new String(response.nextText().getBytes(),"UTF-8"));
                            }else if ("link".equalsIgnoreCase(tag)){
                                entry.setLink(response.nextText());
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
            LogUtils.loggerE(TAG, "xml parse error :" + e.getLocalizedMessage());
        } catch (IOException e) {
            LogUtils.loggerE(TAG,"xml parse error IOException:"+e.getLocalizedMessage());
        }
        return list;
    }
}
