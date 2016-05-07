package com.leo.enjoytime.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class GanhuoEntry extends  JsonEntry implements Parcelable {

    private String Id;
    private String createdAt;
    private String desc;
    private String publishedAt;
    private String source;
    private String type;
    private String url;
    private boolean used;
    private String who;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public GanhuoEntry() {
    }

    protected GanhuoEntry(Parcel in) {
        Id = in.readString();
        createdAt = in.readString();
        desc = in.readString();
        publishedAt = in.readString();
        source = in.readString();
        type = in.readString();
        url = in.readString();
        used = in.readByte() != 0;
        who = in.readString();
    }

    public static final Creator<GanhuoEntry> CREATOR = new Creator<GanhuoEntry>() {
        @Override
        public GanhuoEntry createFromParcel(Parcel in) {
            return new GanhuoEntry(in);
        }

        @Override
        public GanhuoEntry[] newArray(int size) {
            return new GanhuoEntry[size];
        }
    };

    /**
     * @return The Id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param Id The _id
     */
    public void setId(String Id) {
        this.Id = Id;
    }

    public GanhuoEntry withId(String Id) {
        this.Id = Id;
        return this;
    }

    /**
     * @return The createdAt
     */
    public String getCreatedAt() {
        return createdAt;
    }

    /**
     * @param createdAt The createdAt
     */
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public GanhuoEntry withCreatedAt(String createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    /**
     * @return The desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc The desc
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public GanhuoEntry withDesc(String desc) {
        this.desc = desc;
        return this;
    }

    /**
     * @return The publishedAt
     */
    public String getPublishedAt() {
        return publishedAt;
    }

    /**
     * @param publishedAt The publishedAt
     */
    public void setPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
    }

    public GanhuoEntry withPublishedAt(String publishedAt) {
        this.publishedAt = publishedAt;
        return this;
    }

    /**
     * @return The source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source The source
     */
    public void setSource(String source) {
        this.source = source;
    }

    public GanhuoEntry withSource(String source) {
        this.source = source;
        return this;
    }

    /**
     * @return The type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setType(String type) {
        this.type = type;
    }

    public GanhuoEntry withType(String type) {
        this.type = type;
        return this;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    public GanhuoEntry withUrl(String url) {
        this.url = url;
        return this;
    }

    /**
     * @return The used
     */
    public boolean isUsed() {
        return used;
    }

    /**
     * @param used The used
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    public GanhuoEntry withUsed(boolean used) {
        this.used = used;
        return this;
    }

    /**
     * @return The who
     */
    public String getWho() {
        return who;
    }

    /**
     * @param who The who
     */
    public void setWho(String who) {
        this.who = who;
    }

    public GanhuoEntry withWho(String who) {
        this.who = who;
        return this;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public GanhuoEntry withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(url);
        dest.writeString(publishedAt);
        dest.writeString(desc);
        dest.writeInt(favor_flag);
    }
}