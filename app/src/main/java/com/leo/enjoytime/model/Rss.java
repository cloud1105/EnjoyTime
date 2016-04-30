package com.leo.enjoytime.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by leo on 16/4/30.
 */
@Root(name = "item")
public class Rss extends JsonEntry implements Parcelable{
    @Element(name = "title")
    private String title;
    @Element(name = "description")
    private String description;
    @Element(name = "link")
    private String link;

    public Rss() {
    }

    protected Rss(Parcel in) {
        title = in.readString();
        description = in.readString();
        link = in.readString();
    }

    public static final Creator<Rss> CREATOR = new Creator<Rss>() {
        @Override
        public Rss createFromParcel(Parcel in) {
            return new Rss(in);
        }

        @Override
        public Rss[] newArray(int size) {
            return new Rss[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(link);
    }
}
