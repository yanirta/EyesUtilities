package com.applitools.obj;

import com.applitools.Commands.FailedStep;
import com.applitools.obj.Contexts.ResultsAPIContext;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class TestInfo {

    //region Fields
    private String Id;
    private String runningSessionId;
    private String legacySessionId;
    private Object startInfo;
    private String BatchId;
    private String State;
    private String StartedAt;
    private String modelId;
    private Integer Duration;
    private Boolean isDifferent;
    private Object Env;
    private Object appEnvironment;
    private ArrayList<ExpectedStepResult> ExpectedAppOutput;
    private ArrayList<ActualStepResult> ActualAppOutput;
    private Object LegacyBaselineEnv;
    private String LegacyBaselineModelId;
    private String ScenarioId;
    private String ScenarioName;
    private String AppId;
    private String BaselineModelId;
    private Object BaselineEnv;
    private String AppName;
    private Boolean IsNew;
    private String baselineEnvId;
    private String baselineId;
    private String baselineRevId;
    private String branchName;
    private Object appOutputResolution;
    private Object branch;
    private Object baselineBranchName;
    private StepResult[] stepsResults = null;
    private Integer revision;
    private Boolean isStarred;
    //endregion

    //region getters/setters
    public String getBaselineEnvId() {
        return baselineEnvId;
    }

    public void setBaselineEnvId(String baselineEnvId) {
        this.baselineEnvId = baselineEnvId;
    }

    public Object getAppEnvironment() {
        return appEnvironment;
    }

    public void setAppEnvironment(Object appEnvironment) {
        this.appEnvironment = appEnvironment;
    }

    public String getId() {
        return Id;
    }

    public String getRunningSessionId() {
        return runningSessionId;
    }

    public String getLegacySessionId() {
        return legacySessionId;
    }

    public Object getStartInfo() {
        return startInfo;
    }

    public String getBatchId() {
        return BatchId;
    }

    public String getState() {
        return State;
    }

    public String getStartedAt() {
        return StartedAt;
    }

    public Integer getDuration() {
        return Duration;
    }

    public Boolean getIsDifferent() {
        return isDifferent;
    }

    public Object getEnv() {
        return Env;
    }

    public ArrayList<ExpectedStepResult> getExpectedAppOutput() {
        return ExpectedAppOutput;
    }

    public ArrayList<ActualStepResult> getActualAppOutput() {
        return ActualAppOutput;
    }

    public Object getLegacyBaselineEnv() {
        return LegacyBaselineEnv;
    }

    public String getLegacyBaselineModelId() {
        return LegacyBaselineModelId;
    }

    public String getScenarioId() {
        return ScenarioId;
    }

    public String getScenarioName() {
        return ScenarioName;
    }

    public String getAppId() {
        return AppId;
    }

    public String getBaselineModelId() {
        return BaselineModelId;
    }

    public Object getBaselineEnv() {
        return BaselineEnv;
    }

    public String getAppName() {
        return AppName;
    }

    public Boolean getIsNew() {
        return IsNew;
    }

    public void setId(String id) {
        this.Id = id;
    }

    public void setRunningSessionId(String runningSessionId) {
        this.runningSessionId = runningSessionId;
    }

    public void setLegacySessionId(String legacySessionId) {
        this.legacySessionId = legacySessionId;
    }

    public void setStartInfo(Object startInfo) {
        this.startInfo = startInfo;
    }

    public void setBatchId(String batchId) {
        BatchId = batchId;
    }

    public void setState(String state) {
        State = state;
    }

    public void setStartedAt(String startedAt) {
        StartedAt = startedAt;
    }

    public void setDuration(Integer duration) {
        Duration = duration;
    }

    public void setIsDifferent(Boolean different) {
        isDifferent = different;
    }

    public void setEnv(Object env) {
        Env = env;
    }

    public void setExpectedAppOutput(ArrayList<ExpectedStepResult> expectedAppOutput) {
        ExpectedAppOutput = expectedAppOutput;
    }

    public void setActualAppOutput(ArrayList<ActualStepResult> actualAppOutput) {
        ActualAppOutput = actualAppOutput;
    }

    public void setLegacyBaselineEnv(Object legacyBaselineEnv) {
        LegacyBaselineEnv = legacyBaselineEnv;
    }

    public void setLegacyBaselineModelId(String legacyBaselineModelId) {
        LegacyBaselineModelId = legacyBaselineModelId;
    }

    public void setScenarioId(String scenarioId) {
        ScenarioId = scenarioId;
    }

    public void setScenarioName(String scenarioName) {
        ScenarioName = scenarioName;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public void setBaselineModelId(String baselineModelId) {
        BaselineModelId = baselineModelId;
    }

    public void setBaselineEnv(Object baselineEnv) {
        BaselineEnv = baselineEnv;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public void setIsNew(Boolean isNew) {
        IsNew = isNew;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Object getAppOutputResolution() {
        return appOutputResolution;
    }

    public void setAppOutputResolution(Object appOutputResolution) {
        this.appOutputResolution = appOutputResolution;
    }
    //endregion

    public String getUrl() throws MalformedURLException {
        return ResultsAPIContext.instance().getTestAppUrl(getId()).toString();//TODO to check
    }

    public int TotalBaselineSteps() {
        int count = 0;
        for (ExpectedStepResult step : ExpectedAppOutput)
            if (step != null) ++count;
        return count;
    }

    public int TotalActualSteps() {
        int count = 0;
        for (ActualStepResult step : ActualAppOutput)
            if (step != null) ++count;
        return count;
    }

    public int MissingCount() {
        return count(StepResult.Missing);
    }

    public int NewCount() {
        return count(StepResult.New);
    }

    public int PassedCount() {
        return count(StepResult.Passed);
    }

    public int FailedCount() {
        return count(StepResult.Failed);
    }

    public String getBaselineId() {
        return baselineId;
    }

    public void setBaselineId(String baselineId) {
        this.baselineId = baselineId;
    }

    public String getBaselineRevId() {
        return baselineRevId;
    }

    public void setBaselineRevId(String baselineRevId) {
        this.baselineRevId = baselineRevId;
    }

    public String Result() {
        if (getIsNew()) return "New";
        else if (getIsDifferent()) return "Failed";
        else return "Passed";
    }

    public List<FailedStep> getFailedSteps() {
        LinkedList<FailedStep> failedSteps = new LinkedList();

        StepResult[] stepsResults = getStepsResults();
        ResultsAPIContext ctx = ResultsAPIContext.instance();
        ResultUrl ctxUrl = ctx.getUrl();
        String subpath = String.format("%s//%s", ctxUrl.getBatchId(), getId());
        File testArtifact = new File(ctx.getArtifactsFolder(), subpath);
        for (int i = 0; i < stepsResults.length; ++i) {
            if (stepsResults[i] == StepResult.Failed) {
                failedSteps.add(
                        new FailedStep(
                                i + 1,
                                ExpectedAppOutput.get(i),
                                ActualAppOutput.get(i),
                                getId(),
                                testArtifact));
            }
        }
        return failedSteps;
    }

    //region privates
    private StepResult[] getStepsResults() {
        if (stepsResults != null) return stepsResults;

        int steps = Math.max(ExpectedAppOutput.size(), ActualAppOutput.size());
        stepsResults = new StepResult[steps];

        for (int i = 0; i < steps; i++) {
            if (ExpectedAppOutput.get(i) == null) {
                stepsResults[i] = StepResult.New;
            } else if (ActualAppOutput.get(i) == null) {
                stepsResults[i] = StepResult.Missing;
            } else if (ActualAppOutput.get(i).getIsMatching()) {
                stepsResults[i] = StepResult.Passed;
            } else {
                stepsResults[i] = StepResult.Failed;
            }
        }

        return stepsResults;
    }

    private int count(StepResult criteria) {
        int count = 0;
        StepResult[] results = getStepsResults();
        for (int i = 0; i < results.length; ++i)
            if (results[i] == criteria)
                ++count;
        return count;
    }

    public Object getBranch() {
        return branch;
    }

    public void setBranch(Object branch) {
        this.branch = branch;
    }

    public Object getBaselineBranchName() {
        return baselineBranchName;
    }

    public void setBaselineBranchName(Object baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
    }

    public Integer getRevision() {
        return revision;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public Boolean getIsStarred() {
        return isStarred;
    }

    public void setIsStarred(Boolean starred) {
        isStarred = starred;
    }
    //endregion
}
