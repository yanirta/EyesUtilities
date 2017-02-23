package com.applitools.Commands;

import com.applitools.obj.ExtendedTestResult;
import com.applitools.obj.ResultUrl;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;

@Parameters(commandDescription = "Download test steps images of a specific test")
public class DownloadImages extends ResultsAPI {
    private static final String BASELINE_IMG_TMPL = "step-%s-baseline.png";
    private static final String ACTUAL_IMG_TMPL = "step-%s-actual.png";

    @Parameter(names = {"-d", "-destination"}, description = "Destination folder to save the images")
    private String destination;
    @Parameter(names = {"-b"}, description = "Save only baselines")
    private boolean onlyBaselines = false;
    @Parameter(names = {"-a"}, description = "Save only actuals")
    private boolean onlyActuals = false;

    private ResultUrl resultUrl;

    public DownloadImages() {
    }

    public DownloadImages(String url, String destination, String viewKey, boolean onlyBaselines, boolean onlyActuals) {
        super(url, viewKey);
        this.destination = destination;
        this.onlyBaselines = onlyBaselines;
        this.onlyActuals = onlyActuals;
    }

    public void run() throws Exception {
        resultUrl = getUrl();

        ExtendedTestResult extendedTestResult = new ExtendedTestResult(resultUrl, viewKey);
        if (destination == null) destination = System.getProperty("user.dir");
        File dir = new File(new File(destination, resultUrl.getBatchId()), resultUrl.getSessionId());
        if (!dir.exists()) dir.mkdirs();
        if (!onlyActuals && !onlyBaselines) {
            saveImagesByUIDs(BASELINE_IMG_TMPL, extendedTestResult.getBaselineImagesUrls(), dir);
            saveImagesByUIDs(ACTUAL_IMG_TMPL, extendedTestResult.getActualImagesUrls(), dir);
        } else if (onlyBaselines) {
            saveImagesByUIDs(BASELINE_IMG_TMPL, extendedTestResult.getBaselineImagesUrls(), dir);
        } else {//only actuals
            saveImagesByUIDs(ACTUAL_IMG_TMPL, extendedTestResult.getActualImagesUrls(), dir);
        }
    }

    private void saveImagesByUIDs(String nameTemplate, String[] urls, File dir) throws IOException {
        for (int i = 0; i < urls.length; ++i) {
            if (urls[i] == null) continue;

            FileUtils.copyURLToFile(new URL(urls[i]), new File(dir, String.format(nameTemplate, i + 1)));
        }
    }
}
