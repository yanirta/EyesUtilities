package com.applitools.Commands;

import com.applitools.obj.ResultUrl;
import com.applitools.utils.Utils;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import java.security.InvalidParameterException;
import java.util.List;

@Parameters(commandDescription = "Parse result url and extract it's components", hidden = true)
public class Parse implements Command {
    @Parameter(description = "<result url>")
    private List<String> url;
    @Parameter(names = "-b", description = "Get only batch id")
    private boolean onlyBatchId = false;
    @Parameter(names = "-s", description = "Get only session id")
    private boolean onlySessionId = false;

    public void run() {
        if (url.size() != 1) throw new InvalidParameterException("must specify only one url");
        ResultUrl resultUrl = new ResultUrl(url.get(0));

        if (!onlyBatchId && !onlySessionId) {
            System.out.printf("Batch id: %s \n", resultUrl.getBatchId());
            System.out.printf("Session id: %s \n", resultUrl.getSessionId());
            //Setting clipboard
            //Utils.setClipboard(String.format("{\"batchId\": \"%s\", \"sessionId\": \"%s\"}", resultUrl.getBatchId(), resultUrl.getSessionId()));
            Utils.setClipboard(String.format("%s,%s", resultUrl.getBatchId(), resultUrl.getSessionId()));
        } else if (onlyBatchId) {
            System.out.printf("Batch id: %s \n", resultUrl.getBatchId());
            Utils.setClipboard(resultUrl.getBatchId());
        } else { //onlySessionId
            System.out.printf("Session id: %s \n", resultUrl.getSessionId());
            Utils.setClipboard(resultUrl.getSessionId());
        }
    }
}
