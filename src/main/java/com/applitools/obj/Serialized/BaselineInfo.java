package com.applitools.obj.Serialized;

import com.applitools.obj.ResultUrl;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BaselineInfo {
    private static String BASELINES_URL_TMPL = "https://%s/api/baselines?ApiKey=%s&format=json";
    private static ObjectMapper mapper = new ObjectMapper();
    private String Id;
    private String BaseRev;
    private String Rev;
    private String LastUpdated;
    private String LastUpdatedBy;
    private String BaselineModelId;
    private String AppId;
    private String AppName;

    public String getId() {
        return Id;
    }

    public String getAppId() {
        return AppId;
    }

    public String getAppName() {
        return AppName;
    }

    public String getBaseRev() {
        return BaseRev;
    }

    public String getRev() {
        return Rev;
    }

    public String getLastUpdated() {
        return LastUpdated;
    }

    public String getLastUpdatedBy() {
        return LastUpdatedBy;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public void setBaseRev(String baseRev) {
        BaseRev = baseRev;
    }

    public void setRev(String rev) {
        Rev = rev;
    }

    public void setLastUpdated(String lastUpdated) {
        LastUpdated = lastUpdated;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        LastUpdatedBy = lastUpdatedBy;
    }

    public String getBaselineModelId() {
        return BaselineModelId;
    }

    public void setBaselineModelId(String baselineModelId) {
        BaselineModelId = baselineModelId;
    }

    public static BaselineInfo[] getAll(ResultUrl resultUrl, String viewkey) throws IOException {
        URL baselines = new URL(String.format(BASELINES_URL_TMPL, resultUrl.getServerAddress(), viewkey));
        return mapper.readValue(baselines, BaselineInfo[].class);
    }
}
