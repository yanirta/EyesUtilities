package com.yanirta.Commands;

import com.yanirta.obj.Contexts.BranchesAPIContext;
import com.yanirta.obj.Serialized.BaselineInfo;
import com.yanirta.utils.BaselinesManager;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.util.List;

@Parameters(commandDescription = "Performs baselines copy operations")
public class CopyBaseline extends BaselineCommand {
    @Parameter(names = {"-s", "-source"}, description = "The source branch name. If not used or passed “default” will copy from the main branch.")
    private String sourceBranch = "default";
    @Parameter(names = {"-t", "-target"}, description = "The target branch name.", required = true)
    private String targetBranch;
    @Parameter(names = {"-an", "-appName"}, description = "Filter baselines to copy by application name.")
    private String appName = "";

    @Override
    public void run() throws Exception {
        System.out.println(String.format("Attempting to copy baselines from source branch: %s to target branch: %s.", sourceBranch, targetBranch));
        BranchesAPIContext context = BranchesAPIContext.Init(getFormattedServerUrl(), apiKey);
        BaselinesManager baselinesManager = new BaselinesManager(context);
        List<BaselineInfo> baselinesToCopy = baselinesManager.getBaselinesByBranch(sourceBranch);
        if (!appName.isEmpty()) {
            System.out.println(String.format("Filtering by app name: %s.", appName));
            baselinesToCopy = baselinesManager.filterBaselinesByAppName(baselinesToCopy, appName);
        }
        if (baselinesManager.copyBaselines(sourceBranch, targetBranch, baselinesToCopy)) {
            System.out.println("\nCopy baselines succeeded.");
        } else {
            System.out.println("\nCopy baselines aborted.");
        }
    }
}
