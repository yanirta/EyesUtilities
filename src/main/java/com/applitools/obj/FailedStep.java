package com.applitools.obj;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.Serialized.ActualStepResult;
import com.applitools.obj.Serialized.ExpectedStepResult;
import com.applitools.utils.ResourceDownloader;
import com.applitools.utils.Utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FailedStep extends Step {
    private static final int ANIMATION_TRANSITION_INTERVAL = 1000;
    private static final String DIFF_ARTIFACT_EXT = "png";
    private static final String ANIDIFF_ARTIFACT_EXT = "gif";
    private static final String DIFF_ARTIFACT_TYPE = "diff";

    public FailedStep(ResultsAPIContext context, int i, ExpectedStepResult expected, ActualStepResult actual, String testId, PathBuilder pathBuilder) {
        super(context, i, expected, actual, testId, pathBuilder);
    }

    public String getDiff() throws IOException, InterruptedException {
        URL diffImage = getDiffImageUrl();
        Map<String, String> params = getPathParams();
        params.put("file_ext", DIFF_ARTIFACT_EXT);
        params.put("artifact_type", DIFF_ARTIFACT_TYPE);
        File destination = pathBuilder.recreate(params).buildFile();
        pathBuilder.ensureTargetFolder();
        BufferedImage image = downloader.getImage(diffImage);
        Utils.saveImage(image, destination);
        return destination.toString();
    }

    public String getAnimatedDiff() throws IOException, InterruptedException {
        return getAnimatedDiff(true, false);
    }

    public String getAnimatedDiff(int transitionInterval) throws IOException, InterruptedException {
        return getAnimatedDiff(true, false, transitionInterval);
    }

    public String getAnimatedThumbprints() throws IOException, InterruptedException {
        return getAnimatedThumbprints(false);
    }

    public String getAnimatedThumbprints(boolean skipIfExists) throws IOException, InterruptedException {
        try {
            return getAnimatedDiff(false, skipIfExists);
        } catch (Exception e) {
            return getAnimatedDiff();
        }
    }

    private static void saveAnimatedDiff(String baselineImg, String actualImg, String diffImg, File target, int transitionInterval, ResourceDownloader downloader) throws IOException, InterruptedException {
        List<BufferedImage> images = new ArrayList<BufferedImage>(3);
        images.add(downloader.getImage(baselineImg));
        images.add(downloader.getImage(actualImg));
        if (diffImg != null) images.add(downloader.getImage(diffImg));
        Utils.createAnimatedGif(images, target, transitionInterval);
    }

    private String getAnimatedDiff(boolean withDiff, boolean skipIfExists) throws IOException, InterruptedException {
        return getAnimatedDiff(withDiff, skipIfExists, ANIMATION_TRANSITION_INTERVAL);
    }

    private String getAnimatedDiff(boolean withDiff, boolean skipIfExists, int transitionInterval) throws IOException, InterruptedException {
        URL expectedImageURL = getExpectedImageUrl();
        URL actualImageURL = getActualImageUrl();
        URL diffImageURL = getDiffImageUrl();

        Map<String, String> params = getPathParams();
        params.put("file_ext", ANIDIFF_ARTIFACT_EXT);
        params.put("artifact_type", DIFF_ARTIFACT_TYPE);
        File destination = pathBuilder.recreate(params).buildFile();
        pathBuilder.ensureTargetFolder();

        if (skipIfExists && destination.exists()) return destination.toString();

        saveAnimatedDiff(
                expectedImageURL.toString(),
                actualImageURL.toString(),
                withDiff ? diffImageURL.toString() : null,
                destination,
                transitionInterval,
                downloader
        );

        return destination.toString(); //TODO relativize
    }

}
