package com.applitools.Commands;

import com.applitools.obj.BatchInfo;
import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.ResultUrl;
import com.applitools.obj.TestInfo;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Build and save animated diffs gif for failing steps")
public class AnimatedDiffs extends ResultsAPI {
    private static ObjectMapper mapper = new ObjectMapper();

    @Parameter(names = {"-d", "-destination"}, description = "Destination folder to save the diff animated images")
    private String destination;
    @Parameter(names = {"-i", "-interval"}, description = "Transition interval between the images")
    private int interval = 1000;

    public AnimatedDiffs() {
    }

    public AnimatedDiffs(String url, String destination, String viewKey) {
        super(url, viewKey);
        this.destination = destination;
    }

    public void run() throws IOException {
        ResultUrl resultUrl = getUrl();
        ResultsAPIContext.init(resultUrl, viewKey, new File(destination));
        ResultsAPIContext ctx = ResultsAPIContext.instance();
        if (destination == null) destination = System.getProperty("user.dir");
        if (resultUrl.getSessionId() != null) getTestAnimations(ctx);
        else if (resultUrl.getBatchId() != null) getBatchAnimations(ctx);
        else return;//TODO
    }

    private void getBatchAnimations(ResultsAPIContext context) throws IOException {
        BatchInfo bi = BatchInfo.get(context);
        TestInfo[] tests = bi.getTests();
        if (tests.length == 0) {
            System.out.println("No tests found, the provided url doesn't contain results!");
            return;
        }

        System.out.println("Starting download...");
        int i = 1;
        int total = bi.getTotalTests();

        for (TestInfo test : tests) {
            System.out.printf("[%s/%s] -->\n", i++, total);
            downloadTestStepsAnimation(test);
            System.out.printf("Done\n", i++, total);
        }

    }

    private void getTestAnimations(ResultsAPIContext context) throws IOException {
        TestInfo testInfo = mapper.readValue(context.getTestApiUrl(), TestInfo.class);

        downloadTestStepsAnimation(testInfo);
    }

    private void downloadTestStepsAnimation(TestInfo testInfo) {
        List<FailedStep> failedSteps = testInfo.getFailedSteps();
        int i = 1;
        int total = failedSteps.size();
        System.out.printf("\tFound %s failed steps\n", total);
        for (FailedStep step : failedSteps) {
            try {
                System.out.printf("\t\t[%s/%s] Downloading...", i++, total);
                step.getAnimatedDiff(interval);
                System.out.printf("done\n");
            } catch (IOException e) {
                System.out.printf("Failed - %s\n", e.getMessage());
                //TODO Print verbose error
            }
        }
    }
}
