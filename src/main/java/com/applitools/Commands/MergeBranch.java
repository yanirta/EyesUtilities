package com.applitools.Commands;


import com.applitools.obj.Contexts.BranchesAPIContext;
import com.applitools.obj.Serialized.BranchInfo;
import com.applitools.obj.Serialized.MergeBranchResponse;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

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
    @Parameter(names = {"-d", "-deleteSource"}, description = "Delete the source branch after a successful copy")
    private boolean isDelete = false;

    @Override
    public void run() throws Exception {
        BranchesAPIContext.Init(server, mergeKey);
        super.run();
    }

    @Override
    protected void handleResponse(HttpEntity entity, CloseableHttpClient client) throws IOException {
        String resp = EntityUtils.toString(entity, "UTF-8");
        MergeBranchResponse response = mapper.readValue(resp, MergeBranchResponse.class);
        if (!response.isMerged())
            System.out.println("Found %s conflict(s), merge aborted. Please resolve conflicts through applitools test-manager and try again");
        else {
            System.out.println("Merge succeeded");
            if (isDelete) deleteBranch(client, sourceBranch);
        }
    }

    private void deleteBranch(CloseableHttpClient client, String sourceBranch) throws IOException {
        BranchInfo bi = getBranchInfoByName(sourceBranch);
        if (bi == null) return;
        CloseableHttpResponse response = performDeletion(client, bi);
        switch (response.getStatusLine().getStatusCode()) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_ACCEPTED:
                System.out.println("Source branch was deleted");
                break;
            default:
                throwUnexpectedResponse(response.getStatusLine());
        }

        if (response != null) response.close();
    }

    private CloseableHttpResponse performDeletion(CloseableHttpClient client, BranchInfo bi) throws IOException {
        bi.setIsDeleted(true);

        HttpPut put = new HttpPut(getDeleteUrl(bi.getId()));
        String json = mapper.writeValueAsString(bi);
        StringEntity entity = new StringEntity(json);
        put.setEntity(entity);
        put.addHeader("Content-Type", "application/json");
        return client.execute(put);
    }

    private String getDeleteUrl(String branchId) {
        return BranchesAPIContext.instance().getDeleteUrl(branchId);
    }

    private BranchInfo getBranchInfoByName(String sourceBranch) throws IOException {
        BranchInfo[] branchInfos = mapper.readValue(new URL(getBaseUrl()), BranchInfo[].class);
        Optional<BranchInfo> found = Arrays.stream(branchInfos).filter(b -> b.getName().equals(sourceBranch)).findFirst();
        return found.isPresent() ? found.get() : null;
    }

    public String getTaskUrl() {
        return BranchesAPIContext.instance().getMergedUrl();
    }

    private String getBaseUrl() {
        return BranchesAPIContext.instance().getBaseUrl();
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
