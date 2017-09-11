package com.applitools.obj.Serialized;

import com.applitools.obj.Contexts.ResultsAPIContext;
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
    private String startedAt;
    private int testsNew = 0;
    private int testsPassed = 0;
    private int testsFailed = 0;
    private int testsRunning = 0;
    private int stepsNew = 0;
    private int stepsPassed = 0;
    private int stepsFailed = 0;
    private int stepsMissing = 0;
    private int totalBaselineSteps = 0;

    private String url;

    private BatchInfo() {
    }

    private BatchInfo(TestInfo[] tests, String url) {
        this.tests = tests;
        this.url = url;
    }

    public static BatchInfo get(ResultsAPIContext ctx) throws IOException {
        URL batchUrl = ctx.getBatchAPIurl();

        TestInfo[] infos = mapper.readValue(batchUrl, TestInfo[].class);
        if (infos.length == 0) return new BatchInfo(infos, ctx.getBatchAPPurl().toString());

        BatchInfo bi = mapper.readValue(
                mapper.writeValueAsString(((HashMap) infos[0].getStartInfo()).get("batchInfo")),
                BatchInfo.class);

        bi.tests = infos;
        bi.url = ctx.getBatchAPPurl().toString();
        bi.calculateBatchMetrics();
        return bi;
    }

    private void calculateBatchMetrics() {
        for (TestInfo test : tests) {
            if (!test.getState().equalsIgnoreCase("Completed")) ++testsRunning;
            else if (test.getIsNew()) ++testsNew;
            else if (test.getIsDifferent()) ++testsFailed;
            else ++testsPassed;

            stepsNew += test.NewCount();
            stepsPassed += test.PassedCount();
            stepsFailed += test.FailedCount();
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

    public int getTestsFailed() {
        return testsFailed;
    }

    public void setTestsFailed(int testsFailed) {
        this.testsFailed = testsFailed;
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

    public int getStepsFailed() {
        return stepsFailed;
    }

    public double getFailedRate() {
        return 100 * ((double) stepsFailed / totalBaselineSteps);
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
}
