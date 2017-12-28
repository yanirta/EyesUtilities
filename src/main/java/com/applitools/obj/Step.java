package com.applitools.obj;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.Serialized.ActualStepResult;
import com.applitools.obj.Serialized.ExpectedStepResult;
import com.applitools.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
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

    public Step(int i, ExpectedStepResult expected, ActualStepResult actual, String testId, PathGenerator pathGenerator) {
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

    private String saveResourceById(String imageId, String artifact_type) throws IOException {
        ensureTargetFolder();
        Map<String, String> params = getPathParams();
        params.put("artifact_type",artifact_type);
        File destination = pathGenerator.build(params).generateFile();
        URL imageResource = ResultsAPIContext.instance().getImageResource(imageId);
        Utils.saveImage(imageResource.toString(), destination.toString());
        return destination.toString();
    }

    protected Map<String, String> getPathParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("step_index", String.valueOf(index));
        params.put("step_tag", actual.getTag());
        params.put("file_ext", ARTIFACT_EXT);
        return params;
    }

    public Integer getIndex() {
        return index;
    }

    protected void ensureTargetFolder() {
        if (pathGenerator == null)
            throw new InvalidParameterException("pathGenerator is null");
        File outFolder = pathGenerator.generatePath();
        if (!outFolder.exists() && !outFolder.mkdirs())
            throw new RuntimeException(
                    String.format("Unable to create output folder for path: %s", outFolder.toString()));
    }
}
