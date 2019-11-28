package com.applitools.Commands;

import com.applitools.obj.Batches;
import com.applitools.obj.PathBuilder;
import com.applitools.obj.Query;
import com.applitools.obj.ResultUrl;
import com.applitools.utils.SemiColonSplitter;
import com.beust.jcommander.Parameter;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class ResultsAPI extends CommandBase {
    @Parameter(description = "<result url(s)>", required = true)
    private List<String> urls;

    @Parameter(names = {"-k", "--key"}, description = "Enterprise view key", required = true)
    protected String viewKey;

    @Parameter(names = {"-un", "--userName"}, description = "Applitools username")
    protected String userName;

    @Parameter(names = {"-ui", "--userId"}, description = "Applitools internal userId")
    protected String userId;

    @Parameter(names = {"-q", "--query"}, description = "Query for specific batches by specific properties, SemiColon(;) separated 'or' operand.", splitter = SemiColonSplitter.class)
    protected List<String> queries;

    public ResultsAPI() {
    }

    public ResultsAPI(String resUrl, String viewKey) {
        this.urls = new ArrayList<>();
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

    abstract protected HashMap<String, String> getParams();

    protected Batches getBatches(PathBuilder pathGen) throws IOException {
        PathBuilder pg = pathGen.recreate(getParams());
        if (queries != null && !queries.isEmpty()) {
            if (userName == null || userId == null || StringUtils.isEmpty(userName) || StringUtils.isEmpty(userId))
                throw new RuntimeException("Queries require userName(-un) and userId parameters(ui) to be specified");
            Optional<String> first = urls.stream().filter((str) -> ResultUrl.isServerUrl(str)).findFirst();
            if (!first.isPresent())
                throw new RuntimeException("No server url found in parameters! \n");
            String serverUrl = new ResultUrl(first.get(), "00").getServerAddress();
            extendUrlsWithQueryResults(serverUrl);
        }

        return new Batches(getUrls(), viewKey, pg);
    }

    private void extendUrlsWithQueryResults(String serverUrl) throws IOException {
        for (String query : queries) {
            if (!Query.isMatching(query)) {
                System.out.printf("Invalid query section %s \n", query);
                //throw new RuntimeException(String.format("Invalid query section %s \n", query));
                continue;
            }

            Query q = new Query(query, serverUrl, userName, userId);
            urls.addAll(q.getBatchIds());
        }
    }
}
