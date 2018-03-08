package com.applitools.Commands;

import com.applitools.obj.PathGenerator;
import com.applitools.obj.Serialized.BatchInfo;
import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.ResultUrl;
import com.applitools.obj.Serialized.TestInfo;
import com.beust.jcommander.Parameter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class ResultsAPIExtract extends ResultsAPI {
    private static ObjectMapper mapper = new ObjectMapper();

    @Parameter(names = {"-d", "--destination"}, description = "Destination folder/template to save the results")
    protected String destination = "{workdir_root}/Artifacts/{batch_id}/{test_id}/file:{step_index}_{step_tag}_{artifact_type}.{file_ext}";

    public ResultsAPIExtract() {
    }

    public ResultsAPIExtract(String resUrl, String viewKey, String destination) {
        super(resUrl, viewKey);
        this.destination = destination;
    }

    public void run() throws Exception {
        ResultUrl resultUrl = getUrl();
        ResultsAPIContext ctx = new ResultsAPIContext(resultUrl, viewKey);
        PathGenerator generator = new PathGenerator(destination).build(getPathParams());
        if (resultUrl.getSessionId() != null) {
            //Just one test
            TestInfo testInfo = mapper.readValue(ctx.getTestApiUrl(), TestInfo.class);
            testInfo.setPathGenerator(generator);
            runPerTest(testInfo);
        } else if (resultUrl.getBatchId() != null) {
            //Url contains batch
            BatchInfo bi = BatchInfo.get(ctx, generator);
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

    private Map<String, String> getPathParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_root", new File(System.getProperty("user.dir")).getAbsolutePath());
        params.put("workdir_root", new File("").getAbsolutePath());
        params.put("artifacts", "artifacts");
        return params;
    }

    protected abstract void runPerTest(TestInfo testInfo) throws IOException;
}
