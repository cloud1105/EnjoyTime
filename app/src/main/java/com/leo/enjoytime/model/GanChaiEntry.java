package com.leo.enjoytime.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class GanChaiEntry extends JsonEntry implements Parcelable{
    private String id;
    private String title;
    private String summary;
    private String thumbnail;
    private String source;
    private int type;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    public GanChaiEntry(){
    }

    protected GanChaiEntry(Parcel in) {
        id = in.readString();
        title = in.readString();
        summary = in.readString();
        thumbnail = in.readString();
        source = in.readString();
        type = in.readInt();
    }

    public static final Creator<GanChaiEntry> CREATOR = new Creator<GanChaiEntry>() {
        @Override
        public GanChaiEntry createFromParcel(Parcel in) {
            return new GanChaiEntry(in);
        }

        @Override
        public GanChaiEntry[] newArray(int size) {
            return new GanChaiEntry[size];
        }
    };

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    public GanChaiEntry withId(String id) {
        this.id = id;
        return this;
    }

    /**
     * @return The title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title The title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public GanChaiEntry withTitle(String title) {
        this.title = title;
        return this;
    }

    /**
     * @return The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     * @param summary The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    public GanChaiEntry withSummary(String summary) {
        this.summary = summary;
        return this;
    }

    /**
     * @return The thumbnail
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * @param thumbnail The thumbnail
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public GanChaiEntry withThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
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

    public GanChaiEntry withSource(String source) {
        this.source = source;
        return this;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public GanChaiEntry withAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
        return this;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnail);
        dest.writeString(title);
        dest.writeString(summary);
        dest.writeString(id);
    }
}