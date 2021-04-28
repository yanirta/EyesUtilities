package com.yanirta.Commands;


import com.yanirta.obj.Contexts.BranchesAPIContext;
import com.yanirta.obj.Serialized.MergeBranchResponse;
import com.yanirta.utils.BaselinesManager;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Performs branch merge operations")
public class MergeBranch extends BaselineCommand {
    @Parameter(names = {"-s", "-source"}, description = "The source branch name", required = true)
    private String sourceBranch;
    @Parameter(names = {"-t", "-target"}, description = "The target branch name. If not used or passed “default” will copy to the main branch.")
    private String targetBranch = "default";
    @Parameter(names = {"-d", "-deleteSource"}, description = "Delete the source branch after a successful copy")
    private boolean isDelete = false;

    @Override
    public void run() throws Exception {
        System.out.println(String.format("Attempting to merge source branch: %s to target branch: %s.", sourceBranch, targetBranch));
        BranchesAPIContext context = BranchesAPIContext.Init(getFormattedServerUrl(), apiKey);
        BaselinesManager baselinesManager = new BaselinesManager(context);
        MergeBranchResponse response = baselinesManager.mergeBranches(this);
        if (!response.isMerged())
            System.out.println("\nConflicts have been found!!! Merge aborted" +
                    "\nPlease resolve conflicts through yanirta test-manager and try again");
        else {
            System.out.println("\nMerge succeeded");
            if (isDelete && baselinesManager.deleteBranch(sourceBranch))
                System.out.println(String.format("Source branch: %s has been deleted", sourceBranch));
        }
    }

    //Per object serialization
    public String getSourceBranch() {
        return sourceBranch;
    }

    public String getTargetBranch() {
        return targetBranch;
    }
}
