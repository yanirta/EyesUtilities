package com.applitools.obj;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yanir on 07/12/2016.
 */
public class ResultUrl {
    //TODO consider http
    private static final String RESULT_RGX = "^http(s?):\\/\\/(?<serverURL>.+)\\/app\\/(sessions|batches|test-results)\\/(?<batchId>\\d+)\\/?(?<sessionId>\\d+)?.*$";
    private static final String SERVER_URL_RGX = "^(http(s?):\\/\\/)?(?<serverURL>.+)\\/?$";
    private static final String ID_RGX = "^[a-zA-Z0-9]+$";
    private static final Pattern RESULT_PTRN = Pattern.compile(RESULT_RGX);
    private static final Pattern ID_PTRN = Pattern.compile(ID_RGX);
    private static final Pattern SERVER_URL_PTRN = Pattern.compile(SERVER_URL_RGX);

    private final String batchId_;
    private final String sessionId_;
    private final String serverURL_;
    private String url_;

    public ResultUrl(String url) {
        Matcher matcher = RESULT_PTRN.matcher(url);
        if (!matcher.find()) throw new RuntimeException(String.format("Invalid url %s \n", url));
        url_ = url;
        batchId_ = matcher.group("batchId");
        sessionId_ = matcher.group("sessionId");
        serverURL_ = matcher.group("serverURL");
    }

    public ResultUrl(String serverURL, String batchId) {
        Matcher matcher = SERVER_URL_PTRN.matcher(serverURL);
        if (!matcher.find()) throw new RuntimeException(String.format("Invalid server url %s \n", serverURL));
        serverURL_ = matcher.group("serverURL");
        batchId_ = batchId;
        sessionId_ = null;
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

    public static boolean isResultURL(String url){
        return isMatching(RESULT_PTRN, url);
    }

    public static boolean isResultId(String id){
        return isMatching(ID_PTRN, id);
    }

    public static boolean isServerUrl(String url){
        return isMatching(SERVER_URL_PTRN, url);
    }

    private static boolean isMatching(Pattern pattern, String str){
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
