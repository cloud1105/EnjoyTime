package com.leo.enjoytime.network;

import com.leo.enjoytime.model.Atom;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface AtomService {
    @GET
    Call<Atom> getAtom(@Url String url);
}