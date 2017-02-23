package com.applitools.obj;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by yanir on 26/12/2016.
 */
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

    public String getId() {
        return Id;
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

    public static BaselineInfo get(ResultUrl resultUrl, String viewkey, ExtendedTestResult testResult) throws IOException {
        BaselineInfo[] infos = getAll(resultUrl, viewkey);
        String modelId = testResult.getBaselineModelId();
        for (BaselineInfo info : infos) {
            if (info.getBaselineModelId().equals(modelId)) return info;
        }

        return null;
    }

}
