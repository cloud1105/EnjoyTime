package com.leo.enjoytime.network;

import com.leo.enjoytime.model.GanhuoJsonEntry;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by leo on 16/4/23.
 */
public interface GanhuoService {

    @GET("{type}/{count}/{page}")
    Call<GanhuoJsonEntry> getGanhuoEntry(@Path("type") String type
                    , @Path("count") int count, @Path("page") int page);

}
