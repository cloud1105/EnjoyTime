package com.leo.enjoytime.network;

import com.leo.enjoytime.model.Entry;

import java.util.List;

/**
 * Created by leo on 16/4/12.
 */
public interface NetWorkCallback {
    void onSuccess(List<Entry> list);
    void onError(Exception e);
}
