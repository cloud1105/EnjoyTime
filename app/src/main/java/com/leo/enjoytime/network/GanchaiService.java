package com.leo.enjoytime.network;

import com.leo.enjoytime.model.GanChaiEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by leo on 16/4/30.
 */
public interface GanchaiService {
    @GET("digest")
    Call<List<GanChaiEntry>> ListGanchaiEntry(@Query("t")int type
            , @Query("size") int count, @Query("p") int page);
}
