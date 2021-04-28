package com.yanirta.Commands;

import com.yanirta.obj.FailedStep;
import com.yanirta.obj.Serialized.TestInfo;
import com.beust.jcommander.Parameters;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Download test diff images of a specific test")
public class DownloadDiffs extends ResultsAPIExtract {


    public DownloadDiffs() {
    }

    public DownloadDiffs(String url, String destination, String viewKey) {
        super(url, viewKey, destination);
    }

    protected void runPerTest(TestInfo ti) throws IOException, InterruptedException {
        List<FailedStep> steps = ti.getFailedSteps();
        for (FailedStep step : steps) {
            step.getDiff();
        }
    }
}
