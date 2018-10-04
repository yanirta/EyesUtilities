package com.applitools.Commands;

import com.applitools.obj.ResultUrl;
import com.beust.jcommander.Parameter;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultsAPI extends CommandBase {
    @Parameter(description = "<result url(s)>", required = true)
    private List<String> urls;

    @Parameter(names = {"-k", "-key"}, description = "Enterprise view key", required = true)
    protected String viewKey;

    public ResultsAPI() {
    }

    public ResultsAPI(String resUrl, String viewKey) {
        this.urls = new ArrayList<String>();
        this.urls.add(resUrl);
        this.viewKey = viewKey;
    }

    protected ResultUrl getUrl() {
        if (urls.size() != 1) throw new InvalidParameterException("must specify exactly one url");
        return new ResultUrl(urls.get(0));
    }

    protected List<String> getUrls() {
        return urls;
    }
}
