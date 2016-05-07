package com.leo.enjoytime.model;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.ArrayList;

@Root(name="feed", strict = false)
public class Atom {
    @ElementList(name="entry",inline = true)
    private ArrayList<AtomItem> items;

    public ArrayList<AtomItem> getItems() {
        return items;
    }
}