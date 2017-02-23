package com.applitools.obj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanir on 07/12/2016.
 */
public class ResultUrl {
    private static final String RESULT_REGEX = "^https:\\/\\/(?<serverURL>.+)\\/app\\/(sessions|batches)\\/(?<batchId>\\d+)\\/(?<sessionId>\\d+).*$";
    private final String batchId_;
    private final String sessionId_;
    private final String serverURL_;
    private String url_;

    public ResultUrl(String url) {
        url_ = url;
        Pattern pattern = Pattern.compile(RESULT_REGEX);
        Matcher matcher = pattern.matcher(url);
        if (!matcher.find()) ; //TODO error
        batchId_ = matcher.group("batchId");
        sessionId_ = matcher.group("sessionId");
        serverURL_ = matcher.group("serverURL");
    }

    public String getBatchId() {
        return batchId_;
    }

    public String getSessionId() {
        return sessionId_;
    }

    public String getServerAddress() {
        return serverURL_;
    }

    public String getUrl() {
        return url_;
    }
}
