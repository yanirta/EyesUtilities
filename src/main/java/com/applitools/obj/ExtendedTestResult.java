package com.applitools.obj;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;

public class ExtendedTestResult {
    private static final String IMG_RESOURCE_URL_TMPL = "https://%s/api/images/%s?apiKey=%s";

    private JSONObject result_;
    private StepResult[] stepResults_;
    private String serverAddress_;
    private String sessionId_;
    private String batchId_;
    private String viewKey_;

    public ExtendedTestResult(ResultUrl url, String viewkey) throws Exception {
        serverAddress_ = url.getServerAddress();
        batchId_ = url.getBatchId();
        sessionId_ = url.getSessionId();
        viewKey_ = viewkey;
        String apiUrl = String.format("https://%s/api/sessions/batches/%s/%s/?ApiKey=%s&format=json",
                serverAddress_,
                batchId_,
                sessionId_,
                viewKey_);

        String jsonString = readStringFromUrl(apiUrl);
        result_ = new JSONObject(jsonString);
    }

    public StepResult[] getStepsResults() throws Exception {
        if (stepResults_ != null) return stepResults_;

        JSONArray expected = result_.getJSONArray("expectedAppOutput");
        JSONArray actual = result_.getJSONArray("actualAppOutput");

        int steps = Math.max(actual.length(), expected.length());
        StepResult[] retStepResults = new StepResult[steps];

        for (int i = 0; i < steps; i++) {

            if (expected.get(i) == JSONObject.NULL) {
                retStepResults[i] = StepResult.New;
            } else if (actual.get(i) == JSONObject.NULL) {
                retStepResults[i] = StepResult.Missing;
            } else if (actual.getJSONObject(i).getBoolean("isMatching") == true) {
                retStepResults[i] = StepResult.Passed;
            } else {
                retStepResults[i] = StepResult.Failed;
            }
        }

        stepResults_ = retStepResults;
        return stepResults_;
    }

    public boolean isNew() {
        return result_.getBoolean("isNew");
    }

    public boolean isPassed() {
        return !result_.getBoolean("isDifferent");
    }

    public String[] getDiffUrls() throws Exception {
        StepResult[] results = getStepsResults();
        String[] urls = new String[results.length];

        String diffUrlTemplate = "https://" + serverAddress_ + "/api/sessions/batches/%s/steps/%s/diff?ApiKey=%s";
        for (int step = 0; step < results.length; ++step) {
            if (results[step] == StepResult.Failed) {
                urls[step] = String.format(diffUrlTemplate, sessionId_, step + 1, viewKey_);
            } else
                urls[step] = null;
        }
        return urls;
    }

    public String[] getBaselineImagesUrls() {
        return getResImagesURLs("expectedAppOutput");
    }

    public String[] getActualImagesUrls() {
        return getResImagesURLs("actualAppOutput");
    }

    public String getBaselineModelId() {
        return result_.getString("baselineModelId");
    }

    public String getBaselineRev() {
        return result_.getString("baselineRev");
    }

    public String getBranchName() {
        return result_.getString("branchName");
    }

    public String getScenarioName() {
        return result_.getString("scenarioName");
    }

    public String getAppName() {
        return result_.getString("appName");
    }

    public String getHostOS() {
        return getEnvironmentObj().getString("os");
    }

    public String getHostApp() {
        return getEnvironmentObj().getString("hostingApp");
    }

    public String getViewPortSize() {
        JSONObject size = getEnvironmentObj().getJSONObject("displaySize");
        return String.format("%sx%s", size.getInt("width"), size.getInt("height"));
    }

    public String getMatchLevel() {
        return getDefaultMatchSettings().getString("matchLevel");
    }

    private String[] getResImagesURLs(String objectKey) {
        String[] uids = getImagesUIDs(result_.getJSONArray(objectKey));
        String[] retURLs = new String[uids.length];
        int i = 0;
        for (String uid : uids) {
            if (uid == null) {
                retURLs[i++] = null;
                continue;
            }

            retURLs[i++] = String.format(IMG_RESOURCE_URL_TMPL, serverAddress_, uid, viewKey_);
        }

        return retURLs;
    }

    private JSONObject getDefaultMatchSettings() {
        return getStartInfo().getJSONObject("defaultMatchSettings");
    }

    private JSONObject getEnvironmentObj() {
        return getStartInfo().getJSONObject("environment");
    }

    private JSONObject getStartInfo() {
        return result_.getJSONObject("startInfo");
    }

    private static String[] getImagesUIDs(JSONArray infoTable) throws JSONException {
        String[] retUIDs = new String[infoTable.length()];

        for (int i = 0; i < infoTable.length(); i++) {
            if (infoTable.isNull(i)) {
                retUIDs[i] = null;
            } else {
                JSONObject entry = infoTable.getJSONObject(i);
                JSONObject image = entry.getJSONObject("image");
                retUIDs[i] = image.getString("id");
            }
        }

        return retUIDs;
    }

    private static String readStringFromUrl(String url) throws Exception {
        SSLSocketFactory defaultSSLSocketFactory = HttpsURLConnection.getDefaultSSLSocketFactory();
        HttpsURLConnection.setDefaultSSLSocketFactory(new sun.security.ssl.SSLSocketFactoryImpl());
        InputStream is = new URL(url).openStream();
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            return readAll(rd);
        } finally {
            is.close();
        }
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
}
