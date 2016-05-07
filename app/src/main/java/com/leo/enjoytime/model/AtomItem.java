package com.leo.enjoytime.model;

/**
 * Created by leo on 16/5/7.
 */

import android.os.Parcel;
import android.os.Parcelable;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by leo on 16/4/30.
 */
@Root(name = "entry",strict = false)
public class AtomItem extends JsonEntry implements Parcelable {
    @Element(name = "title")
    private String title;
    @Element(name = "content")
    private String content;
    @Element(name = "link",required = false)
    private String link;
    @Attribute(required = false)
    private String href;

    public AtomItem() {
    }

    protected AtomItem(Parcel in) {
        title = in.readString();
        content = in.readString();
        link = in.readString();
    }

    public static final Creator<AtomItem> CREATOR = new Creator<AtomItem>() {
        @Override
        public AtomItem createFromParcel(Parcel in) {
            return new AtomItem(in);
        }

        @Override
        public AtomItem[] newArray(int size) {
            return new AtomItem[size];
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

    public String getHref() {
        return href;
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
