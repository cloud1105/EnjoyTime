package com.leo.enjoytime.network;

import com.leo.enjoytime.model.JsonEntry;

import java.util.List;

/**
 * Created by leo on 16/4/12.
 */
public interface NetWorkCallback {
    void onSuccess(List<? extends JsonEntry> list);
    void onError(String errorMsg);
}
