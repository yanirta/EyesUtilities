package com.applitools.obj.Serialized;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.PathGenerator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BatchInfo {
    private static final ObjectMapper mapper = new ObjectMapper();
    private TestInfo[] tests;

    private String name;
    private String id;
    private String batchId;
    private String startedAt;
    private int testsNew = 0;
    private int testsPassed = 0;
    private int testsUnresolved = 0;
    private int testsMismatched = 0;
    private int testsRunning = 0;
    private int stepsNew = 0;
    private int stepsPassed = 0;
    private int stepsMismatched = 0;
    private int stepsMissing = 0;
    private int totalBaselineSteps = 0;
    private int testsStatusPassed = 0;
    private int testsStatusUnresolved = 0;
    private int testsStatusRunning = 0;
    private int testsStatusFailed = 0;
    private int testsStatusAborted = 0;

    private String url;

    //JSON Ignored
    private PathGenerator pathGenerator;



    private BatchInfo() {
    }

    private BatchInfo(TestInfo[] tests, String url) {
        this.tests = tests;
        this.url = url;
    }

    public static BatchInfo get(ResultsAPIContext ctx, PathGenerator pathGenerator) throws IOException {
        URL batchUrl = ctx.getBatchAPIurl();

        TestInfo[] infos = mapper.readValue(batchUrl, TestInfo[].class);
        if (infos.length == 0)
            return new BatchInfo(infos, ctx.getBatchAPPurl().toString()); //Empty batch

        BatchInfo bi = mapper.readValue(
                mapper.writeValueAsString(((HashMap) infos[0].getStartInfo()).get("batchInfo")),
                BatchInfo.class);

        bi.tests = infos;
        bi.url = ctx.getBatchAPPurl().toString();
        bi.batchId = infos[0].getBatchId();
        bi.setPathGenerator(pathGenerator);
        bi.calculateBatchMetrics();
        return bi;
    }

    private void calculateBatchMetrics() {
        for (TestInfo test : tests) {
            if (!test.getState().equalsIgnoreCase("Completed")) ++testsRunning;
            else if (test.getIsNew()) ++testsNew;
            else if (test.getIsDifferent()) ++testsMismatched;
            else ++testsPassed;

            switch (test.getStatus()) {
                case Passed:
                    ++testsStatusPassed;
                    break;
                case Failed:
                    ++testsStatusFailed;
                    break;
                case Unresolved:
                    ++testsStatusUnresolved;
                    break;
                case Running:
                    ++testsStatusRunning;
                    break;
                case Aborted:
                    ++testsStatusAborted;
                    break;
            }
            stepsNew += test.NewCount();
            stepsPassed += test.PassedCount();
            stepsMismatched += test.MismatchingCount();
            stepsMissing += test.MissingCount();
            totalBaselineSteps += test.TotalBaselineSteps();
        }
    }

    public TestInfo[] getTestInfos() {
        return tests;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TestInfo[] getTests() {
        return tests;
    }

    public int getTotalTests() {
        return tests.length;
    }

    public int getTestsNew() {
        return testsNew;
    }

    public void setTestsNew(int testsNew) {
        this.testsNew = testsNew;
    }

    public int getTestsPassed() {
        return testsPassed;
    }

    public void setTestsPassed(int testsPassed) {
        this.testsPassed = testsPassed;
    }

    public int getTestsMismatched() {
        return testsMismatched;
    }

    public void setTestsMismatched(int testsMismatched) {
        this.testsMismatched = testsMismatched;
    }

    public int getTestsUnresolved() {
        return testsUnresolved;
    }

    public int getTestsRunning() {
        return testsRunning;
    }

    public void setTestsRunning(int testsRunning) {
        this.testsRunning = testsRunning;
    }

    public String getBatchUrl() {
        return url;
    }

    public void setBatchUrl(String url) {
        this.url = url;
    }

    public int getStepsNew() {
        return stepsNew;
    }

    public double getNewRate() {
        return 100 * ((double) stepsNew / totalBaselineSteps);
    }

    public int getStepsPassed() {
        return stepsPassed;
    }

    public double getPassedRate() {
        return 100 * ((double) stepsPassed / totalBaselineSteps);
    }

    public int getStepsMismatched() {
        return stepsMismatched;
    }

    public double getMismatchedRate() {
        return 100 * ((double) stepsMismatched / totalBaselineSteps);
    }

    public int getStepsMissing() {
        return stepsMissing;
    }

    public double getMissingRate() {
        return 100 * ((double) stepsMissing / totalBaselineSteps);
    }

    public int getTotalBaselineSteps() {
        return totalBaselineSteps;
    }

    public void setPathGenerator(PathGenerator pathGenerator) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("batch_id", batchId);
        params.put("batch_name", getName());
        this.pathGenerator = pathGenerator.build(params);
        for (TestInfo ti : tests) ti.setPathGenerator(this.pathGenerator);
    }

    public int getTestsStatusPassed() {
        return testsStatusPassed;
    }

    public int getTestsStatusUnresolved() {
        return testsStatusUnresolved;
    }

    public int getTestsStatusRunning() {
        return testsStatusRunning;
    }

    public int getTestsStatusFailed() {
        return testsStatusFailed;
    }

    public int getTestsStatusAborted() {
        return testsStatusAborted;
    }
}
