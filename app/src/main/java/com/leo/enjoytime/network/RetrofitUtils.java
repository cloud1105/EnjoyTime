package com.leo.enjoytime.network;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.model.Atom;
import com.leo.enjoytime.model.GanChaiJsonDataEntry;
import com.leo.enjoytime.model.GanChaiJsonEntry;
import com.leo.enjoytime.model.GanhuoJsonEntry;
import com.leo.enjoytime.model.Rss;
import com.leo.enjoytime.utils.LogUtils;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

/**
 * Created by leo on 16/4/23.
 */
public class RetrofitUtils extends AbstractNewWorkerManager {
    private static final String TAG = RetrofitUtils.class.getSimpleName();

    private HashMap<String,Call> calls = new HashMap();

    @Override
    public void queryGanhuo(String tag, String type, int page, int count, final NetWorkCallback callback) {
        setCallback(callback);
        if (Const.REQUEST_TYPE_MEIZHI.equals(type)){
            type = "福利";
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.GANHUO_HOST_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GanhuoService service = retrofit.create(GanhuoService.class);

        Call<GanhuoJsonEntry> call  = service.getGanhuoEntry(type,count,page);

        calls.put(tag,call);
        if (callback == null){
            LogUtils.loggerE(TAG,"callback is null");
            return;
        }
        call.enqueue(new Callback<GanhuoJsonEntry>() {
            @Override
            public void onResponse(Call<GanhuoJsonEntry> call, Response<GanhuoJsonEntry> response) {
                if (response.body()!= null && response.body().getResults()!= null) {
                    callback.onSuccess(response.body().getResults());
                }
            }

            @Override
            public void onFailure(Call<GanhuoJsonEntry> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void queryGanChai(String tag, int type, int page, int count, final NetWorkCallback callback) {
        setCallback(callback);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Const.GANCHAI_HOST_API)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        GanchaiService service = retrofit.create(GanchaiService.class);

        Call<GanChaiJsonEntry> call  = service.getGanchaiJsonEntry(type,count,page);

        calls.put(tag,call);

        call.enqueue(new Callback<GanChaiJsonEntry>() {
            @Override
            public void onResponse(Call<GanChaiJsonEntry> call, Response<GanChaiJsonEntry> response) {
                if (response.body()!= null) {
                    GanChaiJsonEntry jsonEntry = response.body();
                    if (jsonEntry != null && jsonEntry.getData()!= null){
                        GanChaiJsonDataEntry dataEntry = jsonEntry.getData();
                        if (dataEntry != null && dataEntry.getResult() != null){
                            callback.onSuccess(response.body().getData().getResult());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<GanChaiJsonEntry> call, Throwable t) {
                    callback.onError(t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void setMeizhiImg(Context context,ImageView imageView, String url, NetWorkCallback callback) {
        setCallback(callback);
        Glide.with(context).load(url)
                .error(R.drawable.default_image)
                .placeholder(R.drawable.default_image)
                .into(imageView);
    }

    @Override
    public void queryRssPage(String tag, String url, final NetWorkCallback callback) {
        setCallback(callback);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.android.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        RssService rssService = retrofit.create(RssService.class);
        Call<Rss> call =  rssService.getRss(url);
        calls.put(tag,call);
        call.enqueue(new Callback<Rss>() {
            @Override
            public void onResponse(Call<Rss> call, Response<Rss> response) {
                callback.onSuccess(response.body().getChannel().getItems());
            }

            @Override
            public void onFailure(Call<Rss> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void queryAtomPage(String tag, String url, final NetWorkCallback callback) {
        setCallback(callback);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.android.com/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        AtomService atomService = retrofit.create(AtomService.class);
        Call<Atom> call =  atomService.getAtom(url);
        calls.put(tag,call);
        call.enqueue(new Callback<Atom>() {
            @Override
            public void onResponse(Call<Atom> call, Response<Atom> response) {
                callback.onSuccess(response.body().getItems());
            }

            @Override
            public void onFailure(Call<Atom> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void cancelQuery(String tag) {
        if (calls.containsKey(tag)){
            calls.get(tag).cancel();
        }
    }
}
