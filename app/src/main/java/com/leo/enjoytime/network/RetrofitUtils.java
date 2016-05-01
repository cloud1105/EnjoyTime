package com.leo.enjoytime.network;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.leo.enjoytime.R;
import com.leo.enjoytime.contant.Const;
import com.leo.enjoytime.model.Atom;
import com.leo.enjoytime.model.GanChaiEntry;
import com.leo.enjoytime.model.GanhuoJsonEntry;
import com.leo.enjoytime.model.Rss;
import com.leo.enjoytime.utils.LogUtils;

import java.util.HashMap;
import java.util.List;

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

        Call<List<GanChaiEntry>> call  = service.ListGanchaiEntry(type,count,page);

        calls.put(tag,call);

        call.enqueue(new Callback<List<GanChaiEntry>>() {
            @Override
            public void onResponse(Call<List<GanChaiEntry>> call, Response<List<GanChaiEntry>> response) {
                    callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<GanChaiEntry>> call, Throwable t) {
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
                .baseUrl(url)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        RssService rssService = retrofit.create(RssService.class);
        Call<List<Rss>> call =  rssService.getRssList();
        calls.put(tag,call);
        call.enqueue(new Callback<List<Rss>>() {
            @Override
            public void onResponse(Call<List<Rss>> call, Response<List<Rss>> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Rss>> call, Throwable t) {
                callback.onError(t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void queryAtomPage(String tag, String url, final NetWorkCallback callback) {
        setCallback(callback);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .build();

        AtomService atomService = retrofit.create(AtomService.class);
        Call<List<Atom>> call =  atomService.getAtomList();
        calls.put(tag,call);
        call.enqueue(new Callback<List<Atom>>() {
            @Override
            public void onResponse(Call<List<Atom>> call, Response<List<Atom>> response) {
                callback.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<Atom>> call, Throwable t) {
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
