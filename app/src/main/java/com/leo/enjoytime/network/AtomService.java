package com.leo.enjoytime.network;

import com.leo.enjoytime.model.Atom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface AtomService {
    @GET
    Call<List<Atom>> getAtomList();
}