package com.applitools.obj;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.Serialized.ActualStepResult;
import com.applitools.obj.Serialized.ExpectedStepResult;
import com.applitools.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FailedStep extends Step {
    private static final int ANIMATION_TRANSITION_INTERVAL = 1000;
    private static final String DESTINATION_GIF_TEMPLATE = "Step_%s.gif";
    private static final String DIFF_FILE_TMPL = "step-%s-diff.png";

    public FailedStep(int i, ExpectedStepResult expected, ActualStepResult actual, String testId, File folder) {
        super(i, expected, actual, testId, folder);
    }

    public String getDiff() throws IOException {
        String outFolder = ensureTargetFolder();
        ResultsAPIContext ctx = ResultsAPIContext.instance();
        URL diffImage = ctx.getDiffImageUrl(testId, getIndex());
        File destination = new File(outFolder, String.format(DIFF_FILE_TMPL, getIndex()));
        Utils.saveImage(diffImage.toString(), destination.toString());
        return destination.toString();
    }

    public String getAnimatedDiff() throws IOException {
        return getAnimatedDiff(expected.getImageId(), actual.getImageId(), true, false);
    }

    public String getAnimatedDiff(int transitionInterval) throws IOException {
        return getAnimatedDiff(expected.getImageId(), actual.getImageId(), true, false, transitionInterval);
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

    private static void saveAnimatedDiff(String baselineImg, String actualImg, String diffImg, File target, int transitionInterval) throws IOException {
        List<BufferedImage> images = new ArrayList<BufferedImage>(3);
        images.add(ImageIO.read(new URL(baselineImg)));
        images.add(ImageIO.read(new URL(actualImg)));
        if (diffImg != null) images.add(ImageIO.read(new URL(diffImg)));
        Utils.createAnimatedGif(images, target, transitionInterval);
    }

    private String getAnimatedDiff(String expectedImageId, String actualImageId, boolean withDiff, boolean skipIfExists) throws IOException {
        return getAnimatedDiff(expectedImageId, actualImageId, withDiff, skipIfExists, ANIMATION_TRANSITION_INTERVAL);
    }

    private String getAnimatedDiff(String expectedImageId, String actualImageId, boolean withDiff, boolean skipIfExists, int transitionInterval) throws IOException {
        String outFolder = ensureTargetFolder();
        ResultsAPIContext ctx = ResultsAPIContext.instance();
        URL expectedImageURL = ctx.getImageUrl(expectedImageId);
        URL actualImageURL = ctx.getImageUrl(actualImageId);
        URL diffImageURL = ctx.getDiffImageUrl(testId, getIndex());
        File destination = new File(outFolder, String.format(DESTINATION_GIF_TEMPLATE, getIndex()));
        if (skipIfExists && destination.exists()) return destination.toString();

        saveAnimatedDiff(
                expectedImageURL.toString(),
                actualImageURL.toString(),
                withDiff ? diffImageURL.toString() : null,
                destination,
                transitionInterval
        );

        return destination.toString(); //TODO reletivize
    }


}
