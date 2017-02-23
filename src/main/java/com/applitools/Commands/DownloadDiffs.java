package com.applitools.Commands;

import com.applitools.utils.Utils;
import com.applitools.obj.ExtendedTestResult;
import com.applitools.obj.ResultUrl;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.File;

@Parameters(commandDescription = "Download test diff images of a specific test")
public class DownloadDiffs extends ResultsAPI {
    private static String DIFF_FILE_TMPL = "step-%s-diff.png";

    @Parameter(names = {"-d", "-destination"}, description = "Destination folder to save the diff images")
    private String destination;

    public DownloadDiffs() {
    }

    public DownloadDiffs(String url, String destination, String viewKey) {
        super(url, viewKey);
        this.destination = destination;
    }

    public void run() throws Exception {
        ResultUrl resultUrl = getUrl();

        try {
            ExtendedTestResult extendedTestResult = new ExtendedTestResult(resultUrl, viewKey);
            if (destination == null) destination = System.getProperty("user.dir");
            File dir = new File(new File(destination, resultUrl.getBatchId()), resultUrl.getSessionId());
            if (!dir.exists()) dir.mkdirs();
            String[] diffUrls = extendedTestResult.getDiffUrls();
            for (int i = 0; i < diffUrls.length; ++i) {
                if (diffUrls[i] == null) continue;
                File dst = new File(dir, String.format(DIFF_FILE_TMPL, i + 1));
                Utils.saveImage(diffUrls[i], dst.getAbsolutePath());
            }
        } catch (Exception e) {
            //TODO
            e.printStackTrace();
            throw e;
        }
    }
}
