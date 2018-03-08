package com.applitools.obj;

import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.Serialized.BatchInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Batches extends ArrayList<BatchInfo> {
    //Batches
    private int batchesPassed = 0;
    private int batchesFailed = 0;
    private int batchesUnresolved = 0;
    private int batchesRunning = 0;
    private int batchesAborted = 0;
    private int batchesMatched = 0;
    private int batchesMismatched = 0;
    private int batchesNew = 0;
    private int batchesMissing = 0;
    //Tests
    private int totalTests = 0;
    private int testsPassed = 0;
    private int testsFailed = 0;
    private int testsUnresolved = 0;
    private int testsMismatched = 0;
    private int testsMatched = 0;
    private int testsNew = 0;
    private int testsRunning = 0;
    private int testsAborted = 0;
    //Steps
    private int totalSteps = 0;
    private int stepsMatched = 0;
    private int stepsMismatched = 0;
    private int stepsNew = 0;
    private int stepsMissing = 0;

    public Batches(List<String> urls, String viewKey, PathGenerator pathGenerator) throws IOException {
        for (String url : urls) {
            ResultsAPIContext context = new ResultsAPIContext(url, viewKey);
            BatchInfo batchInfo = BatchInfo.get(context, pathGenerator);
            if (batchInfo != null) this.add(batchInfo);
        }

        calculateMetrics();
    }

    private void calculateMetrics() {
        for (BatchInfo batchInfo : this) {
            totalTests += batchInfo.getTotalTests();
            testsPassed += batchInfo.getTestsPassed();
            testsFailed += batchInfo.getTestsPassed();
            testsUnresolved += batchInfo.getTestsUnresolved();
            testsRunning += batchInfo.getTestsRunning();
            testsAborted += batchInfo.getTestsAborted();
            testsMismatched += batchInfo.getTestsMismatched();
            totalSteps += batchInfo.getTotalActualSteps();
            stepsMatched += batchInfo.getStepsMatched();
            stepsMismatched += batchInfo.getStepsMismatched();
            stepsNew += batchInfo.getStepsNew();
            stepsMissing += batchInfo.getStepsMissing();
            switch (batchInfo.getStatus()) {
                case Passed:
                    ++batchesPassed;
                    break;
                case Failed:
                    ++batchesFailed;
                    break;
                case Unresolved:
                    ++batchesUnresolved;
                    break;
                case Running:
                    ++batchesRunning;
                    break;
                case Aborted:
                    ++batchesAborted;
                    break;
            }

            switch (batchInfo.getResult()) {
                case Matched:
                    ++batchesMatched;
                    break;
                case Mismatched:
                    ++batchesMismatched;
                    break;
                case New:
                    ++batchesNew;
                    break;
                case Missing:
                    ++batchesMissing;
                    break;
            }
        }
    }

    //#region batches
    public int getBatchesPassed() {
        return batchesPassed;
    }

    public int getBatchesFailed() {
        return batchesFailed;
    }

    public int getBatchesUnresolved() {
        return batchesUnresolved;
    }

    public int getBatchesRunning() {
        return batchesRunning;
    }

    public float getBatchesPassedRate() {
        return 100 * (float) batchesPassed / size();
    }

    public float getBatchesFailedRate() {
        return 100 * (float) batchesFailed / size();
    }

    public float getBatchesUnresolvedRate() {
        return 100 * (float) batchesUnresolved / size();
    }

    public float getBatchesRunningRate() {
        return 100 * (float) batchesRunning / size();
    }

    public int getBatchesMatched() {
        return batchesMatched;
    }

    public int getBatchesMismatched() {
        return batchesMismatched;
    }

    public int getBatchesNew() {
        return batchesNew;
    }

    public int getBatchesMissing() {
        return batchesMissing;
    }

    public int getBatchesAborted() {
        return batchesAborted;
    }
    //#endregion

    //#region testes
    public int getTestsPassed() {
        return testsPassed;
    }

    public int getTestsFailed() {
        return testsFailed;
    }

    public int getTestsUnresolved() {
        return testsUnresolved;
    }

    public int getTestsRunning() {
        return testsRunning;
    }

    public float getTestsPassedRate() {
        return 100 * (float) testsPassed / totalTests;
    }

    public float getTestsFailedRate() {
        return 100 * (float) testsFailed / totalTests;
    }

    public float getTestsUnresolvedRate() {
        return 100 * (float) testsUnresolved / totalTests;
    }

    public float getTestsRunningRate() {
        return 100 * (float) testsRunning / totalTests;
    }

    public int getTestsTotal() {
        return totalTests;
    }

    public int getTestsMismatched() {
        return testsMismatched;
    }

    public int getTestsMatched() {
        return testsMatched;
    }

    public int getTestsNew() {
        return testsNew;
    }

    public int getTestsAborted() {
        return testsAborted;
    }
    //#endregion

    //#region steps
    public int getStepsTotal() {
        return totalSteps;
    }

    public int getStepsMatched() {
        return stepsMatched;
    }

    public int getStepsMismatched() {
        return stepsMismatched;
    }

    public int getStepsNew() {
        return stepsNew;
    }

    public int getStepsMissing() {
        return stepsMissing;
    }

    public float getStepsMatchedRate() {
        return 100 * (float) stepsMatched / totalSteps;
    }

    public float getStepsMismatchedRate() {
        return 100 * (float) stepsMismatched / totalSteps;
    }

    public float getStepsNewRate() {
        return 100 * (float) stepsNew / totalSteps;
    }

    public float getStepsMissingRate() {
        return 100 * (float) stepsMissing / totalSteps;
    }


    //#endregion
}
