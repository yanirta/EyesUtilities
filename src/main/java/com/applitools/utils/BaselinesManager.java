package com.applitools.utils;

import com.applitools.Commands.MergeBranch;
import com.applitools.obj.Contexts.BranchesAPIContext;
import com.applitools.obj.Serialized.BaselineInfo;
import com.applitools.obj.Serialized.BranchInfo;
import com.applitools.obj.Serialized.MergeBranchResponse;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    public List<BaselineInfo> getBaselinesByBranch(String branchName) throws IOException, InterruptedException {
        List<BaselineInfo> baselines = new ArrayList<>();
        BranchInfo bi = getBranchInfoByName(branchName);
        if (bi == null) return baselines;
        String url = context.getBaselinesUrl();
        JsonObject entity = new JsonObject();
        entity.addProperty("limit", "1000");
        entity.addProperty("branchId", bi.getId());
        StringEntity stringEntity = new StringEntity(entity.toString(), ContentType.APPLICATION_JSON);

        try (CloseableHttpResponse response = ApiCallHandler.sendPostRequest(url, stringEntity, context)) {
            String resp = EntityUtils.toString(response.getEntity(), "UTF-8");
            JsonNode jsonNode = mapper.readTree(resp);
            String baselinesAsString = jsonNode.get("baselines").toString();
            baselines = mapper.readValue(baselinesAsString, new TypeReference<List<BaselineInfo>>() {
            });
        }
        return baselines;
    }

    public List<BaselineInfo> filterBaselinesByAppName(List<BaselineInfo> baselines, String appName) {
        List<BaselineInfo> filteredBaselines = new ArrayList<>();
        baselines.forEach(baseline -> {
            if (baseline.getAppName().equalsIgnoreCase(appName)) {
                filteredBaselines.add(baseline);
            }
        });
        return filteredBaselines;
    }

    public boolean copyBaselines(String sourceBranch, String targetBranch, List<BaselineInfo> baselines) throws IOException, InterruptedException {
        if (baselines.size() == 0) {
            System.out.println("\nFound 0 baselines to copy.");
            return false;
        }
        String url = context.copyBaselinesUrl();
        StringEntity entity = generateCopyBaselinesEntity(sourceBranch, targetBranch, baselines);

        try (CloseableHttpResponse response = ApiCallHandler.sendPostRequest(url, entity, context)) {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) return true;
            return false;
        }
    }

    private StringEntity generateCopyBaselinesEntity(String sourceBranch, String targetBranch, List<BaselineInfo> baselines) throws IOException {
        JsonObject entity = new JsonObject();
        entity.addProperty("sourceBranch", sourceBranch);
        entity.addProperty("targetBranch", targetBranch);

        JsonArray baselinesIds = new JsonArray();
        baselines.forEach(baseline -> {
            baselinesIds.add(baseline.getId());
        });

        entity.add("baselineIds", baselinesIds);

        return new StringEntity(entity.toString(), ContentType.APPLICATION_JSON);
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
