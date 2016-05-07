package com.leo.enjoytime.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by leo on 16/5/7.
 */

@Root(name = "item",strict = false)
public class Item extends JsonEntry implements Parcelable {
    @Element(name = "title",required = true)
    private String title;
    @Element(name = "description",required = true)
    private String description;
    @Element(name = "link",required = true)
    private String link;

    public Item(){

    }

    public Item(Parcel in) {
        title = in.readString();
        description = in.readString();
        link = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
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