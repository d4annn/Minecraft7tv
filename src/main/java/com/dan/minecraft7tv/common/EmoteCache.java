package com.dan.minecraft7tv.common;

public class EmoteCache {
    private String url;
    private String name;

    public EmoteCache(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public EmoteCache setUrl(String url) {
        this.url = url;
        return this;
    }

    public EmoteCache setName(String name) {
        this.name = name;
        return this;
    }
}
