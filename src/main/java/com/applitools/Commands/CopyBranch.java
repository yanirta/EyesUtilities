package com.applitools.Commands;


import com.applitools.obj.Contexts.BranchesAPIContext;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.codehaus.jackson.map.ObjectMapper;

@Parameters(commandDescription = "Performs copy based branch operations")
public class CopyBranch extends LongTaskAPI {
    private static ObjectMapper mapper = new ObjectMapper();


    @Parameter(names = {"-k", "-key"}, description = "Branch management api key", required = true)
    private String branchKey;
    @Parameter(names = {"-as", "-server"}, description = "Set Applitools server url. [default eyes.applitools.com]")
    private String server = "eyes.applitools.com";
    @Parameter(names = {"-a", "-app"}, description = "The application name that was used in the tests", required = true)
    private String appName;
    @Parameter(names = {"-s", "-source"}, description = "The source branch name", required = true)
    private String sourceBranch;
    @Parameter(names = {"-t", "-target"}, description = "The target branch name. If not used or passed “default” will copy to the main branch.")
    private String targetBranch = "default";
    @Parameter(names = {"-o", "-overwrite"}, description = "Overwrite the baseline of the target branch, even in case of a conflict.")
    private boolean overwriteChanges = false;
    @Parameter(names = {"-all"}, description = "Copy the entire baseline, [default = copies just the changes]")
    private boolean copyAll = false;
    @Parameter(names = {"-d", "deleteSource"}, description = "Delete the source branch once a successfully merged")
    private boolean deleteSourceBranch = false;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getSourceBranch() {
        return sourceBranch;
    }

    public void setSourceBranch(String sourceBranch) {
        this.sourceBranch = sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }

    public void setTargetBranch(String targetBranch) {
        this.targetBranch = targetBranch;
    }

    public boolean isOverwriteChanges() {
        return overwriteChanges;
    }

    public void setOverwriteChanges(boolean overwriteChanges) {
        this.overwriteChanges = overwriteChanges;
    }

    public boolean isCopyAll() {
        return copyAll;
    }

    public void setCopyAll(boolean copyAll) {
        this.copyAll = copyAll;
    }

    public boolean isDeleteSourceBranch() {
        return deleteSourceBranch;
    }

    public void setDeleteSourceBranch(boolean deleteSourceBranch) {
        this.deleteSourceBranch = deleteSourceBranch;
    }

    public String getTaskUrl() {
        return BranchesAPIContext.Init(server, branchKey).getComposedUrl();
    }
}
