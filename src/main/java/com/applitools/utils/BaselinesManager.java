package com.applitools.utils;

import com.applitools.Commands.MergeBranch;
import com.applitools.obj.Contexts.BranchesAPIContext;
import com.applitools.obj.Serialized.BranchInfo;
import com.applitools.obj.Serialized.MergeBranchResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;

public class BaselinesManager {
    private BranchesAPIContext context;
    protected static ObjectMapper mapper = new ObjectMapper();

    public BaselinesManager(BranchesAPIContext context) {

        this.context = context;
    }

    public MergeBranchResponse mergeBranches(MergeBranch mergeBranch) throws InterruptedException, IOException {
        MergeBranchResponse mergeBranchResponse = null;
        String url = context.getMergedUrl();
        String json = mapper.writeValueAsString(mergeBranch);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        try (CloseableHttpResponse response = ApiCallHandler.sendPostRequest(url, entity, context)) {
            String resp = EntityUtils.toString(response.getEntity(), "UTF-8");
            mergeBranchResponse = mapper.readValue(resp, MergeBranchResponse.class);
        }
        return mergeBranchResponse;
    }

    public boolean deleteBranch(String sourceBranch) throws IOException, InterruptedException {
        BranchInfo bi = getBranchInfoByName(sourceBranch);
        if (bi == null) return false;
        bi.setIsDeleted(true);
        String url = context.getDeleteUrl(bi.getId());
        String json = mapper.writeValueAsString(bi);
        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);

        try (CloseableHttpResponse response = ApiCallHandler.sendPutRequest(url, entity, context)) {
            switch (response.getStatusLine().getStatusCode()) {
                case HttpStatus.SC_OK:
                case HttpStatus.SC_ACCEPTED:
                    return true;
                default:
                    throwUnexpectedResponse(response.getStatusLine());
            }
        }
        return false;
    }

    protected static void throwUnexpectedResponse(StatusLine statusLine) {
        throw new RuntimeException(String.format("Unexpected response from request, code: %s, message: %s \n",
                statusLine.getStatusCode(),
                statusLine.getReasonPhrase()));
    }

    private BranchInfo getBranchInfoByName(String sourceBranch) throws IOException {
        BranchInfo[] branchInfos = mapper.readValue(new URL(context.getBaseUrl()), BranchInfo[].class);
        Optional<BranchInfo> found = Arrays.stream(branchInfos).filter(b -> b.getName().equals(sourceBranch)).findFirst();
        return found.isPresent() ? found.get() : null;
    }
}
