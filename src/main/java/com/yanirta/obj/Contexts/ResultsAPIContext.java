package com.yanirta.obj.Contexts;

import com.yanirta.obj.ResultUrl;

import java.net.MalformedURLException;
import java.net.URL;

public class ResultsAPIContext extends Context{
    private static final String BATCHINFO_URL_TMPL = "https://%s/api/sessions/batches/%s?ApiKey=%s&format=json";
    private static final String BATCH_URL_TEMPLATE = "https://%s/app/batches/%s";
    private static final String TEST_APP_URL_TEMPLATE = "https://%s/app/sessions/%s/%s/";
    private static final String TEST_API_URL_TEMPLATE = "https://%s/api/sessions/batches/%s/%s?ApiKey=%s&format=json";
    private static final String IMAGE_URL_TEMPLATE = "https://%s/api/images/%s/?ApiKey=%s";
    private static final String DIFF_URL_TEMPLATE = "https://%s/api/sessions/batches/%s/%s/steps/%s/diff?ApiKey=%s";
    private final ResultUrl url;

    public ResultsAPIContext(String url, String viewkey) {
        this(new ResultUrl(url), viewkey);
    }

    public ResultsAPIContext(ResultUrl url, String viewkey) {
        super(viewkey);
        this.url = url;
    }

    public ResultUrl getUrl() {
        return url;
    }

    public String getViewkey() {
        return getKey();
    }

    public URL getBatchAPIurl() throws MalformedURLException {
        return new URL(String.format(BATCHINFO_URL_TMPL,
                url.getServerAddress(),
                url.getBatchId(),
                getKey()));
    }

    public URL getBatchAPPurl() throws MalformedURLException {
        return new URL(String.format(BATCH_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId()));
    }

    public URL getTestAppUrl(String testId) throws MalformedURLException {
        return new URL(String.format(TEST_APP_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId(),
                testId));
    }

    public URL getTestApiUrl() throws MalformedURLException {
        return new URL(String.format(TEST_API_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId(),
                url.getSessionId(),
                getKey()));
    }

    public URL getTestApiUrl(String testId) throws MalformedURLException {
        return new URL(String.format(TEST_API_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId(),
                testId,
                getKey()));
    }

    public URL getImageUrl(String imageId) throws MalformedURLException {
        return new URL(String.format(IMAGE_URL_TEMPLATE,
                url.getServerAddress(),
                imageId,
                getKey()));
    }

    public URL getDiffImageUrl(String testId, int imageIndex) throws MalformedURLException {
        return new URL(String.format(DIFF_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId(),
                testId,
                imageIndex,
                getKey()));
    }

    public URL getImageResource(String imageId) throws MalformedURLException {
        return new URL(String.format(IMAGE_URL_TEMPLATE,
                url.getServerAddress(),
                imageId,
                getKey()));
    }
}
