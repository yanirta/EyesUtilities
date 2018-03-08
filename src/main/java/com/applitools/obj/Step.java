package com.applitools.obj;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.Serialized.ActualStepResult;
import com.applitools.obj.Serialized.ExpectedStepResult;
import com.applitools.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Step {
    private static final String ARTIFACT_EXT = "png";
    private static final String BASELINE_ARTIFACT_TYPE = "baseline";
    private static final String ACTUAL_ARTIFACT_TYPE = "actual";

    protected String testId;
    protected ExpectedStepResult expected;
    protected ActualStepResult actual;
    protected PathGenerator pathGenerator;
    private int index;
    private ResultsAPIContext context;

    public Step(ResultsAPIContext context, int i, ExpectedStepResult expected, ActualStepResult actual, String testId, PathGenerator pathGenerator) {
        this.context = context;
        this.index = i;
        this.expected = expected;
        this.actual = actual;
        this.testId = testId;
        this.pathGenerator = pathGenerator;
    }

    public String getExpectedImage() throws IOException {
        return saveResourceById(expected.getImageId(), BASELINE_ARTIFACT_TYPE);
    }

    public String getActualImage() throws IOException {
        return saveResourceById(actual.getImageId(), ACTUAL_ARTIFACT_TYPE);
    }

    public Result result() {
        if (expected == null) {
            return Result.New;
        } else if (actual == null) {
            return Result.Missing;
        } else if (actual.getIsMatching()) {
            return Result.Matched;
        } else {
            return Result.Mismatched;
        }
    }


    public URL getExpectedImageUrl() throws MalformedURLException {
        return context.getImageUrl(expected.getImageId());
    }

    public URL getActualImageUrl() throws MalformedURLException {
        return context.getImageUrl(actual.getImageId());
    }

    public URL getDiffImageUrl() throws MalformedURLException {
        return context.getDiffImageUrl(testId, index);
    }

    private String saveResourceById(String imageId, String artifact_type) throws IOException {
        Map<String, String> params = getPathParams();
        params.put("artifact_type", artifact_type);
        File destination = pathGenerator.build(params).generateFile();
        pathGenerator.ensureTargetFolder();
        URL imageResource = context.getImageResource(imageId);
        Utils.saveImage(imageResource.toString(), destination);
        return destination.toString();
    }

    protected Map<String, String> getPathParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("step_index", String.valueOf(index));
        params.put("step_tag", Utils.toFolderName(actual.getTag()));
        params.put("file_ext", ARTIFACT_EXT);
        return params;
    }

    public Integer getIndex() {
        return index;
    }


}
