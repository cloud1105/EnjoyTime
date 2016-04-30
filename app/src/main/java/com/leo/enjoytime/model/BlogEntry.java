package com.leo.enjoytime.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by leo on 16/4/23.
 */
public class BlogEntry implements Parcelable{
    private String title;
    private String desc;
    private String url;

    public Class getIntentActivity() {
        return intentActivity;
    }

    public void setIntentActivity(Class intentActivity) {
        this.intentActivity = intentActivity;
    }

    private Class intentActivity;

    public BlogEntry() {
    }

    protected BlogEntry(Parcel in) {
        title = in.readString();
        desc = in.readString();
        url = in.readString();
    }

    public static final Creator<BlogEntry> CREATOR = new Creator<BlogEntry>() {
        @Override
        public BlogEntry createFromParcel(Parcel in) {
            return new BlogEntry(in);
        }

        @Override
        public BlogEntry[] newArray(int size) {
            return new BlogEntry[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(title);
        dest.writeString(desc);
    }
}
