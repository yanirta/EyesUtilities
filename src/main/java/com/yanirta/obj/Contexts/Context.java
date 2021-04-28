package com.yanirta.obj.Contexts;

public abstract class Context {
    private static final String GENERAL_API_CALL = "%s?ApiKey=%s";
    private final String key;

    public Context(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String decorateLocation(String url) {
        return String.format(url.contains("?") ? GENERAL_API_CALL.replace("?","&") : GENERAL_API_CALL, url, key);
    }
}
