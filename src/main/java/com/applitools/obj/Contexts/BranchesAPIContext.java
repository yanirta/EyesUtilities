package com.applitools.obj.Contexts;

public class BranchesAPIContext {
    private static final String BRANCHES_URL_TMPL = "https://%s/api/baselines/branches/merge?apiKey=%s";

    private static BranchesAPIContext context_;

    private String serverUrl_;
    private String mergeKey_;

    private BranchesAPIContext(String serverUrl, String updateKey) {
        this.serverUrl_ = serverUrl;
        this.mergeKey_ = updateKey;
    }

    public static synchronized BranchesAPIContext Init(String serverUrl, String updateKey) {
        if (context_ != null)
            throw new RuntimeException("Invaild call of Context.init(...)");
        context_ = new BranchesAPIContext(serverUrl, updateKey);
        return context_;
    }

    public static BranchesAPIContext instance() {
        return context_;
    }

    public String getComposedUrl() {
        return String.format(BRANCHES_URL_TMPL, serverUrl_, mergeKey_);
    }
}
