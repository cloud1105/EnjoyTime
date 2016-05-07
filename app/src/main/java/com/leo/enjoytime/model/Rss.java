package com.leo.enjoytime.model;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Created by leo on 16/4/30.
 */
@Root(name="rss", strict = false)
public class Rss {

    @Element(name="channel")
    private Channel channel;

    public Channel getChannel() {
        return channel;
    }
}

