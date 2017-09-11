package com.applitools.obj;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.Serialized.ActualStepResult;
import com.applitools.obj.Serialized.ExpectedStepResult;
import com.applitools.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;

public class Step {
    private static final String BASELINE_IMG_TMPL = "step-%s-baseline.png";
    private static final String ACTUAL_IMG_TMPL = "step-%s-actual.png";

    protected String testId;
    protected ExpectedStepResult expected;
    protected ActualStepResult actual;
    private int index;
    private File outFolder;

    public Step(int i, ExpectedStepResult expected, ActualStepResult actual, String testId, File folder) {
        this.index = i;
        this.expected = expected;
        this.actual = actual;
        this.testId = testId;
        this.outFolder = folder;
    }

    public String getExpectedImage() throws IOException {
        return saveResourceById(expected.getImageId(), BASELINE_IMG_TMPL);
    }

    public String getActualImage() throws IOException {
        return saveResourceById(actual.getImageId(), ACTUAL_IMG_TMPL);
    }

    private String saveResourceById(String imageId, String imgTemplate) throws IOException {
        String dst = ensureTargetFolder();
        File destination = new File(dst, String.format(imgTemplate, index));
        URL imageResource = ResultsAPIContext.instance().getImageResource(imageId);
        Utils.saveImage(imageResource.toString(), destination.toString());
        return destination.toString();
    }

    public Integer getIndex() {
        return index;
    }

    protected String ensureTargetFolder() {
        if (outFolder == null)
            throw new InvalidParameterException("outFolder is null");
        if (!outFolder.exists() && !outFolder.mkdirs())
            throw new RuntimeException("Unable to create output folder");
        return outFolder.toString();
    }
}
