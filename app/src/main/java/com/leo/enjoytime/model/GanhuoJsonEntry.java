package com.leo.enjoytime.model;

import java.util.List;

/**
 * Created by leo on 16/5/1.
 */
public class GanhuoJsonEntry {
    private String error;
    private List<GanhuoEntry> results;

    public GanhuoJsonEntry() {
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<GanhuoEntry> getResults() {
        return results;
    }

    public void setResults(List<GanhuoEntry> results) {
        this.results = results;
    }
}
