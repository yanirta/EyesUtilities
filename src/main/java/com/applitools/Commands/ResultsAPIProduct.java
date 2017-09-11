package com.applitools.Commands;

import com.applitools.obj.Serialized.BatchInfo;
import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.ResultUrl;
import com.applitools.obj.Serialized.TestInfo;
import com.beust.jcommander.Parameter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

public abstract class ResultsAPIProduct extends ResultsAPI {
    private static ObjectMapper mapper = new ObjectMapper();

    @Parameter(names = {"-d", "-destination"}, description = "Destination folder to save the diff animated images")
    protected String destination;

    public ResultsAPIProduct() {
    }

    public ResultsAPIProduct(String resUrl, String viewKey, String destination) {
        super(resUrl, viewKey);
        this.destination = destination;
    }

    public void run() throws Exception {
        ResultUrl resultUrl = getUrl();
        ResultsAPIContext.init(resultUrl, viewKey, new File(destination));
        ResultsAPIContext ctx = ResultsAPIContext.instance();
        if (destination == null) destination = System.getProperty("user.dir");
        if (resultUrl.getSessionId() != null) {
            //Just one test
            TestInfo testInfo = mapper.readValue(ctx.getTestApiUrl(), TestInfo.class);
            runPerTest(testInfo);
        } else if (resultUrl.getBatchId() != null) {
            //Url contains batch
            BatchInfo bi = BatchInfo.get(ctx);
            TestInfo[] tests = bi.getTests();
            int i = 1;
            int total = bi.getTotalTests();
            System.out.println("Starting batch download...\n");
            for (TestInfo test : tests) {
                System.out.printf("[%s/%s] -->\n", i++, total);
                runPerTest(test);
                System.out.printf("Done\n");
            }
            System.out.println("Batch download done\n");
        } else return;//TODO except
    }

    protected abstract void runPerTest(TestInfo testInfo) throws IOException;
}
