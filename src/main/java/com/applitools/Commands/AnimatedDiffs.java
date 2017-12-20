package com.applitools.Commands;

import com.applitools.obj.FailedStep;
import com.applitools.obj.Serialized.TestInfo;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;

@Parameters(commandDescription = "Build and save animated diffs gif for failing steps")
public class AnimatedDiffs extends ResultsAPIExtract {
    private static ObjectMapper mapper = new ObjectMapper();

    @Parameter(names = {"-i", "-interval"}, description = "Transition interval between the images")
    private int interval = 1000;

    public AnimatedDiffs() {
    }

    public AnimatedDiffs(String url, String destination, String viewKey) {
        super(url, viewKey, destination);
    }

    protected void runPerTest(TestInfo ti) throws IOException {
        downloadTestStepsAnimation(ti);
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
