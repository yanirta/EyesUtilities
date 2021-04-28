package com.yanirta.Commands;

import com.yanirta.obj.Serialized.TestInfo;
import com.yanirta.obj.Step;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Download test steps images of a specific test")
public class DownloadImages extends ResultsAPIExtract {

    @Parameter(names = {"-b"}, description = "Save only baselines")
    private boolean onlyBaselines = false;
    @Parameter(names = {"-a"}, description = "Save only actuals")
    private boolean onlyActuals = false;

    public DownloadImages() {
    }

    public DownloadImages(String url, String destination, String viewKey, boolean onlyBaselines, boolean onlyActuals) {
        super(url, viewKey, destination);
        this.onlyBaselines = onlyBaselines;
        this.onlyActuals = onlyActuals;
    }

    protected void runPerTest(TestInfo testInfo) throws IOException, InterruptedException {
        List<Step> steps = testInfo.getSteps();
        for (Step step : steps) {
            if (onlyBaselines) saveBaselineImage(step);
            else if (onlyActuals) saveActualImage(step);
            else saveImages(step);
        }
    }

    private void saveImages(Step step) throws IOException, InterruptedException {
        saveBaselineImage(step);
        saveActualImage(step);
    }

    private void saveActualImage(Step step) throws IOException, InterruptedException {
        step.getActualImage();
    }

    private void saveBaselineImage(Step step) throws IOException, InterruptedException {
        step.getExpectedImage();
    }
}
