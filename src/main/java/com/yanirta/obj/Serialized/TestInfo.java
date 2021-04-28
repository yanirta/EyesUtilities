package com.yanirta.obj.Serialized;

import com.yanirta.obj.*;
import com.yanirta.obj.Contexts.ResultsAPIContext;
import com.yanirta.utils.Utils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TestInfo {
    private static final String PLAYBACK_FILENAME = "test_playback.gif";

    //region Fields
    private String Id;
    private Boolean isAborted;
    private Boolean isDefaultStatus;
    private com.yanirta.obj.Status Status;
    private Object savedTo;
    private String runningSessionId;
    private String legacySessionId;
    private Object startInfo;
    private String BatchId;
    private String State;
    private String StartedAt;
    private String modelId;
    private Integer Duration;
    private Boolean isDifferent;
    private Environment Env;
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
    private Result[] stepsResults = null;
    private Integer revision;
    private Boolean isStarred;
    private String secretToken;
    //endregion

    @JsonIgnore
    private PathBuilder pathBuilder;
    @JsonIgnore
    private ResultsAPIContext context;

    //region getters/setters

    //region getters
    public Object getSavedTo() {
        return savedTo;
    }

    public Boolean getIsDefaultStatus() {
        return isDefaultStatus;
    }

    public Status getStatus() {
        return Status;
    }

    public String getBaselineEnvId() {
        return baselineEnvId;
    }

    public Object getAppEnvironment() {
        return appEnvironment;
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

    public Environment getEnv() {
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

    public String getModelId() {
        return modelId;
    }

    public String getBranchName() {
        return branchName;
    }

    public Object getAppOutputResolution() {
        return appOutputResolution;
    }

    public String getUrl() throws MalformedURLException {
        return context.getTestAppUrl(getId()).toString();//TODO to check
    }

    public int getTotalBaselineSteps() {
        int count = 0;
        if (ExpectedAppOutput == null) return count;
        for (ExpectedStepResult step : ExpectedAppOutput)
            if (step != null) ++count;
        return count;
    }

    public int getTotalActualSteps() {
        int count = 0;
        if (ActualAppOutput == null) return count;
        for (ActualStepResult step : ActualAppOutput)
            if (step != null) ++count;
        return count;
    }

    public int getMissingCount() {
        return count(Result.Missing);
    }

    public int getNewCount() {
        return count(Result.New);
    }

    public int getMatchedCount() {
        return count(Result.Matched);
    }

    public int getMismatchedCount() {
        return count(Result.Mismatched);
    }

    public String getBaselineId() {
        return baselineId;
    }

    public String getBaselineRevId() {
        return baselineRevId;
    }

    public List<FailedStep> getFailedSteps() {
        LinkedList<FailedStep> failedSteps = new LinkedList();
        Result[] stepsResults = getStepsResults();

        for (int i = 0; i < stepsResults.length; ++i) {
            if (stepsResults[i] == Result.Mismatched) {
                failedSteps.add(
                        new FailedStep(
                                context,
                                i + 1,
                                ExpectedAppOutput.get(i),
                                ActualAppOutput.get(i),
                                getId(),
                                pathBuilder));
            }
        }
        return failedSteps;
    }

    public List<Step> getSteps() {
        LinkedList<Step> steps = new LinkedList();
        if (ExpectedAppOutput == null || ActualAppOutput == null) return steps;

        int count = Math.max(ExpectedAppOutput.size(), ActualAppOutput.size());

        for (int i = 0; i < count; ++i)
            steps.add(
                    new Step(
                            context,
                            i + 1,
                            ExpectedAppOutput.get(i),
                            ActualAppOutput.get(i),
                            getId(),
                            pathBuilder));
        return steps;
    }

    public Object getBranch() {
        return branch;
    }

    public Object getBaselineBranchName() {
        return baselineBranchName;
    }

    public Integer getRevision() {
        return revision;
    }

    public Boolean getIsStarred() {
        return isStarred;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public String getPlaybackAnimation(int interval, boolean withDiffs) throws IOException {
        pathBuilder.ensureTargetFolder();

        List<BufferedImage> images = new ArrayList<BufferedImage>(ActualAppOutput.size());
        for (Step step : getSteps()) {
            if (step.result() != Result.Missing)
                if (withDiffs && step.result() == Result.Mismatched)
                    images.add(ImageIO.read(step.getDiffImageUrl()));
                else
                    images.add(ImageIO.read(step.getActualImageUrl()));
        }

        File file = new PathBuilder(pathBuilder.buildPath().toString(), PLAYBACK_FILENAME).buildFile();
        Utils.createAnimatedGif(images, file, interval);

        return file.getPath();
    }

    public String getResult() {
        if (getIsNew()) return "New";
        else if (getIsDifferent()) return "Mismatched";
        else return "Matched";
    }

    //endregion

    //region setters
    public void setSavedTo(Object savedTo) {
        this.savedTo = savedTo;
    }

    public void setIsDefaultStatus(Boolean defaultStatus) {
        isDefaultStatus = defaultStatus;
    }

    public void setStatus(com.yanirta.obj.Status status) {
        Status = status;
    }

    public Boolean getIsAborted() {
        return isAborted;
    }

    public void setIsAborted(Boolean aborted) {
        isAborted = aborted;
    }

    public void setBaselineEnvId(String baselineEnvId) {
        this.baselineEnvId = baselineEnvId;
    }

    public void setAppEnvironment(Object appEnvironment) {
        this.appEnvironment = appEnvironment;
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

    public void setEnv(Environment env) {
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

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public void setAppOutputResolution(Object appOutputResolution) {
        this.appOutputResolution = appOutputResolution;
    }

    public void setBaselineId(String baselineId) {
        this.baselineId = baselineId;
    }

    public void setBaselineRevId(String baselineRevId) {
        this.baselineRevId = baselineRevId;
    }

    public void setBranch(Object branch) {
        this.branch = branch;
    }

    public void setBaselineBranchName(Object baselineBranchName) {
        this.baselineBranchName = baselineBranchName;
    }

    public void setRevision(Integer revision) {
        this.revision = revision;
    }

    public void setIsStarred(Boolean starred) {
        isStarred = starred;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }

    @JsonIgnore
    public void setContext(ResultsAPIContext context) {
        this.context = context;
    }

    @JsonIgnore
    public void setPathBuilder(PathBuilder pathBuilder) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("test_id", getId());
        params.put("batch_id", getBatchId());
        params.put("test_name", getScenarioName());
        params.put("batch_name", getBatchName());
        params.put("app_name", getAppName());
        params.put("branch_name", getBranchName());
        params.put("os", getEnv().getOs());
        params.put("hostapp", getEnv().getHostingApp());
        params.put("viewport", getEnv().getDisplaySizeStr());
        this.pathBuilder = pathBuilder.recreate(params);
    }
    //endregion

    //endregion

    //region privates
    private String getBatchName() {
        return ((HashMap) ((HashMap) this.getStartInfo()).get("batchInfo")).get("name").toString();
    }

    private Result[] getStepsResults() {
        if (stepsResults != null) return stepsResults;

        List<Step> steps = getSteps();
        stepsResults = new Result[steps.size()];

        int i = 0;
        for (Step step : steps) {
            stepsResults[i++] = step.result();
        }

        return stepsResults;
    }

    private int count(Result criteria) {
        int count = 0;
        Result[] results = getStepsResults();
        for (int i = 0; i < results.length; ++i)
            if (results[i] == criteria)
                ++count;
        return count;
    }
    //endregion


}
