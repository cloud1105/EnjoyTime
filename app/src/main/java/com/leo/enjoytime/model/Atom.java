package com.leo.enjoytime.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by leo on 16/4/30.
 */
@Root(name = "entry")
public class Atom extends JsonEntry implements Parcelable{
    @Element(name = "title")
    private String title;
    @Element(name = "content")
    private String content;
    @Element(name = "link")
    private String link;

    public Atom() {
    }

    protected Atom(Parcel in) {
        title = in.readString();
        content = in.readString();
        link = in.readString();
    }

    public static final Creator<Atom> CREATOR = new Creator<Atom>() {
        @Override
        public Atom createFromParcel(Parcel in) {
            return new Atom(in);
        }

        @Override
        public Atom[] newArray(int size) {
            return new Atom[size];
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
        dest.writeString(title);
        dest.writeString(link);
    }
}
