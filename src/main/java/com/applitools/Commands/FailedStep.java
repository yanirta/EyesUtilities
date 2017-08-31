package com.applitools.Commands;

import com.applitools.obj.ActualStepResult;
import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.ExpectedStepResult;
import com.applitools.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class FailedStep {
    private static final int ANIMATION_TRANSITION_INTERVAL = 1000;
    private static final String DESTINATION_GIF_TEMPLATE = "%s/Step_%s.gif";

    private String testId;
    private File outFolder;
    private ExpectedStepResult expected;
    private ActualStepResult actual;
    private int index;

    public FailedStep(int i, ExpectedStepResult expected, ActualStepResult actual, String testId, File folder) {
        this.index = i;
        this.expected = expected;
        this.actual = actual;
        this.testId = testId;
        this.outFolder = folder;
    }

    private String getAnimatedDiff(String expectedImageId, String actualImageId, boolean withDiff, boolean skipIfExists) throws IOException {
        if (outFolder == null)
            throw new InvalidParameterException("outFolder is null");
        if (!outFolder.exists() && !outFolder.mkdirs())
            throw new RuntimeException("Unable to create output folder");
        ResultsAPIContext ctx = ResultsAPIContext.instance();
        URL expectedImageURL = ctx.getImageUrl(expectedImageId);
        URL actualImageURL = ctx.getImageUrl(actualImageId);
        URL diffImageURL = ctx.getDiffImageUrl(testId, index);
        File destination = new File(String.format(DESTINATION_GIF_TEMPLATE, outFolder, index));
        if (skipIfExists && destination.exists()) return destination.toString();

        saveAnimatedDiff(
                expectedImageURL.toString(),
                actualImageURL.toString(),
                withDiff ? diffImageURL.toString() : null,
                destination
        );

        return destination.toString();
    }

    public String getAnimatedDiff() throws IOException {
        return getAnimatedDiff(expected.getImageId(), actual.getImageId(), true, false);
    }

    public String getAnimatedThumbprints() throws IOException {
        return getAnimatedThumbprints(false);
    }

    public String getAnimatedThumbprints(boolean skipIfExists) throws IOException {
        try {
            return getAnimatedDiff(expected.getThumbprintId(), actual.getThumbprintId(), false, skipIfExists);
        } catch (Exception e) {
            return getAnimatedDiff();
        }
    }


    private static void saveAnimatedDiff(String baselineImg, String actualImg, String diffImg, File target) throws IOException {
        List<BufferedImage> images = new ArrayList<BufferedImage>(3);
        images.add(ImageIO.read(new URL(baselineImg)));
        images.add(ImageIO.read(new URL(actualImg)));
        if (diffImg != null) images.add(ImageIO.read(new URL(diffImg)));
        Utils.createAnimatedGif(images, target, ANIMATION_TRANSITION_INTERVAL);
    }
}
