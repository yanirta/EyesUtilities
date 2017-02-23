package com.applitools.Commands;

import com.applitools.obj.ResultUrl;
import com.beust.jcommander.Parameter;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public abstract class ResultsAPI {
    @Parameter(description = "<result url>", required = true)
    private List<String> url;
    @Parameter(names = {"-k", "-key"}, description = "Enterprise view key", required = true)
    protected String viewKey;

    public ResultsAPI() {
    }

    public ResultsAPI(String resUrl, String viewKey) {
        this.url = new ArrayList<String>();
        this.url.add(resUrl);
        this.viewKey = viewKey;
    }

    protected ResultUrl getUrl() {
        if (url.size() != 1) throw new InvalidParameterException("must specify exactly one url");
        return new ResultUrl(url.get(0));
    }
}
