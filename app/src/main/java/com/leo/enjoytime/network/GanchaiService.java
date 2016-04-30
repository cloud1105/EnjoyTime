package com.leo.enjoytime.network;

import com.leo.enjoytime.model.GanChaiEntry;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by leo on 16/4/30.
 */
public interface GanchaiService {
    @GET("/digest?t={type}&p={page}&size={count}")
    Call<List<GanChaiEntry>> ListGanchaiEntry(@Path("type") int type
            , @Path("count") int count, @Path("page") int page);
}
