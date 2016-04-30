package com.leo.enjoytime.network;

import com.leo.enjoytime.model.Rss;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RssService {
    @GET
    Call<List<Rss>> getRssList();
}