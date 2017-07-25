package com.applitools.obj.Contexts;

import com.applitools.obj.ResultUrl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class ResultsAPIContext {
    private static final String BATCHINFO_URL_TMPL = "https://%s/api/sessions/batches/%s?ApiKey=%s&format=json";
    private static final String BATCH_URL_TEMPLATE = "https://%s/app/batches/%s";
    private static final String TEST_URL_TEMPLATE = "https://%s/app/sessions/%s/%s/";
    private static final String IMAGE_URL_TEMPLATE = "https://%s/api/images/%s/?ApiKey=%s";
    private static final String DIFF_URL_TEMPLATE = "https://%s/api/sessions/batches/%s/%s/steps/%s/diff?ApiKey=%s";

    private static ResultsAPIContext context_;
    private final ResultUrl url;
    private final String viewkey;
    private File artifactsFolder;

    private ResultsAPIContext(ResultUrl url, String viewkey, File artifactsOut) {
        this.url = url;
        this.viewkey = viewkey;
        this.artifactsFolder = artifactsOut;
    }

    public static synchronized ResultsAPIContext init(ResultUrl url, String viewkey, File artifactsOut) {
        if (context_ != null)
            throw new RuntimeException("Invaild call of Context.init(...)");
        context_ = new ResultsAPIContext(url, viewkey, artifactsOut);
        return context_;
    }

    public static ResultsAPIContext instance() {
        return context_;
    }

    public ResultUrl getUrl() {
        return url;
    }

    public String getViewkey() {
        return viewkey;
    }

    public URL getBatchAPIurl() throws MalformedURLException {
        return new URL(String.format(BATCHINFO_URL_TMPL,
                url.getServerAddress(),
                url.getBatchId(),
                viewkey));
    }

    public URL getBatchAPPurl() throws MalformedURLException {
        return new URL(String.format(BATCH_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId()));
    }

    public URL getTestAppurl(String testId) throws MalformedURLException {
        return new URL(String.format(TEST_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId(),
                testId));
    }

    public URL getImageUrl(String imageId) throws MalformedURLException {
        return new URL(String.format(IMAGE_URL_TEMPLATE,
                url.getServerAddress(),
                imageId,
                viewkey));
    }

    public URL getDiffImageUrl(String testId, int imageIndex) throws MalformedURLException {
        return new URL(String.format(DIFF_URL_TEMPLATE,
                url.getServerAddress(),
                url.getBatchId(),
                testId,
                imageIndex,
                viewkey));
    }

    public File getArtifactsFolder() {
        return artifactsFolder;
    }
}
