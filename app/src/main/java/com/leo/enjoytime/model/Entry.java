package com.leo.enjoytime.model;

/**
 * desc : 省时省力的Andbase功能组件
 * type : Android
 * url : http://www.finalshares.com/read-608
 * create_at : 2016-02-01T02:16:52.628Z
 * favor_flag : 1
 */
public class Entry {
    private String type;
    private String url;
    private String create_at;
    private String desc;
    private int favor_flag;
    private int digest_id;
    private String thumb_nail;
    private String title;
    private String summary;
    private Class<?> intentActivity;

    public Entry() {

    }

    public Class<?> getIntentActivity() {
        return intentActivity;
    }

    public void setIntentActivity(Class<?> intentActivity) {
        this.intentActivity = intentActivity;
    }

    public int getDigest_id() {
        return digest_id;
    }

    public void setDigest_id(int digest_id) {
        this.digest_id = digest_id;
    }

    public String getThumb_nail() {
        return thumb_nail;
    }

    public void setThumb_nail(String thumb_nail) {
        this.thumb_nail = thumb_nail;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCreate_at() {
        return create_at;
    }

    public void setCreate_at(String create_at) {
        this.create_at = create_at;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getFavor_flag() {
        return favor_flag;
    }

    public void setFavor_flag(int favor_flag) {
        this.favor_flag = favor_flag;
    }
}
