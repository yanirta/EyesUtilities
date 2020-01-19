package com.applitools.Commands;


import com.applitools.obj.Contexts.BranchesAPIContext;
import com.applitools.obj.Serialized.MergeBranchResponse;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

@Parameters(commandDescription = "Performs branch merge operations")
public class MergeBranch extends LongTaskAPI {
    @Parameter(names = {"-k", "-key"}, description = "Branch management api key", required = true)
    private String mergeKey;
    @Parameter(names = {"-as", "-server"}, description = "Set Applitools server url. [default eyes.applitools.com]")
    private String server = "eyes.applitools.com";
    @Parameter(names = {"-s", "-source"}, description = "The source branch name", required = true)
    private String sourceBranch;
    @Parameter(names = {"-t", "-target"}, description = "The target branch name. If not used or passed “default” will copy to the main branch.")
    private String targetBranch = "default";

    @Override
    protected void parseResponse(HttpEntity entity) throws IOException {
        String resp = EntityUtils.toString(entity, "UTF-8");
        MergeBranchResponse response = mapper.readValue(resp, MergeBranchResponse.class);
        if (!response.isMerged())
            System.out.println("Found %s conflict(s), merge aborted. Please resolve conflicts through applitools test-manager and try again");
        else
            System.out.println("Success");
    }

    public String getTaskUrl() {
        return BranchesAPIContext.Init(server, mergeKey).getComposedUrl();
    }

    //Per object serialization
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
}
