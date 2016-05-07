package com.leo.enjoytime.network;

import com.leo.enjoytime.model.Rss;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface RssService {
    @GET
    Call<Rss> getRss(@Url String url);
}