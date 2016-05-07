package com.leo.enjoytime.model;

import java.util.List;

/**
 * Created by leo on 16/5/2.
 */
public class GanChaiJsonDataEntry {
    private String allPages;
    private String count;
    private String currentPage;
    private List<GanChaiEntry> result;

    public String getAllPages() {
        return allPages;
    }

    public void setAllPages(String allPages) {
        this.allPages = allPages;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(String currentPage) {
        this.currentPage = currentPage;
    }

    public List<GanChaiEntry> getResult() {
        return result;
    }

    public void setResult(List<GanChaiEntry> result) {
        this.result = result;
    }
}
