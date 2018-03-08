package com.applitools.obj.Serialized;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.PathGenerator;
import com.applitools.obj.Result;
import com.applitools.obj.Status;
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
    private int testsMatched = 0;
    private int testsMismatched = 0;
    private int testsRunning = 0;
    private int stepsNew = 0;
    private int stepsMatched = 0;
    private int stepsMismatched = 0;
    private int stepsMissing = 0;
    private int totalBaselineSteps = 0;
    private int totalActualSteps = 0;
    private int testsPassed = 0;
    private int testsUnresolved = 0;
    //    private int testsStatusRunning = 0;
    private int testsFailed = 0;
    private int testsAborted = 0;

    private String url;

    //JSON Ignored
    private PathGenerator pathGenerator;
    private ResultsAPIContext context;
    private Result batchResult = Result.Matched;

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
        bi.setContext(ctx);
        bi.calculateBatchMetrics();
        return bi;
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

//    public void setTestsNew(int testsNew) {
//        this.testsNew = testsNew;
//    }

    public String getUrl() {
        return url;
    }

    public void setBatchUrl(String url) {
        this.url = url;
    }

    public void setPathGenerator(PathGenerator pathGenerator) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("batch_id", batchId);
        params.put("batch_name", getName());
        this.pathGenerator = pathGenerator.build(params);
        for (TestInfo ti : tests) ti.setPathGenerator(this.pathGenerator);
    }

    public void setContext(ResultsAPIContext context) {
        this.context = context;
        for (TestInfo ti : tests) ti.setContext(context);
    }

    //region privates
    private BatchInfo() {
    }

    private BatchInfo(TestInfo[] tests, String url) {
        this.tests = tests;
        this.url = url;
    }

    private void calculateBatchMetrics() {
        for (TestInfo test : tests) {
            //if (!test.getState().equalsIgnoreCase("Completed")) ++testsRunning;
            if (test.getIsNew()) ++testsNew;
            else if (test.getIsDifferent()) ++testsMismatched;
            else ++testsMatched;

            switch (test.getStatus()) {
                case Passed:
                    ++testsPassed;
                    break;
                case Failed:
                    ++testsFailed;
                    break;
                case Unresolved:
                    ++testsUnresolved;
                    break;
                case Running:
                    ++testsRunning;
                    break;
                case Aborted:
                    ++testsAborted;
                    break;
            }
            stepsNew += test.getNewCount();
            stepsMatched += test.getMatchedCount();
            stepsMismatched += test.getMismatchedCount();
            stepsMissing += test.getMissingCount();
            totalBaselineSteps += test.getTotalBaselineSteps();
            totalActualSteps += test.getTotalActualSteps();
        }
    }
    //endregion

    //region data getters
    //region batch data
    public Status getStatus() {
        if (testsRunning > 0) return Status.Running;
        if (testsAborted > 0 || testsUnresolved > 0) return Status.Unresolved;
        if (testsFailed > 0) return Status.Failed;
        return Status.Passed;
    }

    public Result getResult() {
        if (testsRunning > 0) return Result.Running;
        if (testsMismatched > 0) return Result.Mismatched;
        return Result.Matched;
    }
    //endregion

    //region tests data
    public int getTestsNew() {
        return testsNew;
    }

    public int getTestsMatched() {
        return testsMatched;
    }

    public int getTestsMismatched() {
        return testsMismatched;
    }

    public int getTestsRunning() {
        return testsRunning;
    }

    public int getTestsUnresolved() {
        return testsUnresolved;
    }

    public int getTestsFailed() {
        return testsFailed;
    }

    public int getTestsAborted() {
        return testsAborted;
    }

    public int getTestsPassed() {
        return testsPassed;
    }

    public int getTotalTests() {
        return tests.length;
    }
    //endregion

    //region steps data
    public int getTotalActualSteps() {
        return totalActualSteps;
    }

    public int getStepsMissing() {
        return stepsMissing;
    }

    public int getTotalBaselineSteps() {
        return totalBaselineSteps;
    }

    public int getStepsNew() {
        return stepsNew;
    }

    public int getStepsMatched() {
        return stepsMatched;
    }

    public int getStepsMismatched() {
        return stepsMismatched;
    }

    public double getNewRate() {
        return 100 * ((double) stepsNew / totalBaselineSteps);
    }

    public double getMatchedRate() {
        return 100 * ((double) stepsMatched / totalBaselineSteps);
    }

    public double getMissingRate() {
        return 100 * ((double) stepsMissing / totalBaselineSteps);
    }

    public double getMismatchedRate() {
        return 100 * ((double) stepsMismatched / totalBaselineSteps);
    }

    //endregion

    //endregion
}
