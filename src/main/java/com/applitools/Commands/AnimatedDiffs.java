package com.applitools.Commands;

import com.applitools.obj.ExtendedTestResult;
import com.applitools.obj.ResultUrl;
import com.applitools.obj.StepResult;
import com.applitools.utils.Utils;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Parameters(commandDescription = "Build and save animated diffs gif for failing steps")
public class AnimatedDiffs extends ResultsAPI {
    private static final String ANIDIFF_FILE_TMPL = "step-%s-anidiff.gif";

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

    public void run() throws Exception {
        ResultUrl resultUrl = getUrl();
        try {
            ExtendedTestResult extendedTestResult = new ExtendedTestResult(resultUrl, viewKey);
            if (destination == null) destination = System.getProperty("user.dir");
            File dir = new File(new File(destination, resultUrl.getBatchId()), resultUrl.getSessionId());
            if (!dir.exists()) dir.mkdirs();


            StepResult[] stepsResults = extendedTestResult.getStepsResults();
            String[] blinesUrls = extendedTestResult.getBaselineImagesUrls();
            String[] actualUrls = extendedTestResult.getActualImagesUrls();
            String[] diffUrls = extendedTestResult.getDiffUrls();

            for (int i = 0; i < stepsResults.length; ++i) {
                switch (stepsResults[i]) {
                    case Passed:
                        break;
                    case Failed:
                        File dst = new File(dir, String.format(ANIDIFF_FILE_TMPL, i + 1));
                        saveAnimatedDiff(blinesUrls[i], actualUrls[i], diffUrls[i], dst);
                        break;
                    case New:
                        break;
                    case Missing:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void saveAnimatedDiff(String baselineImg, String actualImg, String diffImg, File target) throws IOException {
        List<BufferedImage> images = new ArrayList<BufferedImage>(3);
        images.add(ImageIO.read(new URL(baselineImg)));
        images.add(ImageIO.read(new URL(actualImg)));
        images.add(ImageIO.read(new URL(diffImg)));
        Utils.createAnimatedGif(images, target, interval);
    }
}
