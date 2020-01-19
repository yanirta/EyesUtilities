package com.applitools.obj.Contexts;

public class BranchesAPIContext {
    private static final String BRANCHES_BASE_URL_TMPL = "https://%s/api/baselines/branches?apiKey=%s";
    private static final String BRANCHES_MERGE_URL_TMPL = "https://%s/api/baselines/branches/merge?apiKey=%s";
    private static final String BRANCHES_DELETE_URL_TMPL = "https://%s/api/baselines/branches/%s?apiKey=%s";


    private static BranchesAPIContext context_;

    private String serverUrl_;
    private String mergeKey_;

    private BranchesAPIContext(String serverUrl, String updateKey) {
        this.serverUrl_ = serverUrl;
        this.mergeKey_ = updateKey;
    }

    public static synchronized BranchesAPIContext Init(String serverUrl, String mergeKey) {
        if (context_ != null)
            throw new RuntimeException("Invaild call of Context.init(...)");
        context_ = new BranchesAPIContext(serverUrl, mergeKey);
        return context_;
    }

    public static BranchesAPIContext instance() {
        return context_;
    }

    public String getMergedUrl() {
        return String.format(BRANCHES_MERGE_URL_TMPL, serverUrl_, mergeKey_);
    }

    public String getBaseUrl() {
        return String.format(BRANCHES_BASE_URL_TMPL, serverUrl_, mergeKey_);
    }

    public String getDeleteUrl(String branchId) {
        return String.format(BRANCHES_DELETE_URL_TMPL, serverUrl_, branchId, mergeKey_);
    }
}
