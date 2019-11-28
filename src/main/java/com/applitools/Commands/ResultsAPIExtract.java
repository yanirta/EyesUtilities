package com.applitools.Commands;

import com.applitools.obj.PathBuilder;
import com.applitools.obj.Serialized.BatchInfo;
import com.applitools.obj.Contexts.ResultsAPIContext;
import com.applitools.obj.ResultUrl;
import com.applitools.obj.Serialized.TestInfo;
import com.beust.jcommander.Parameter;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public abstract class ResultsAPIExtract extends ResultsAPI {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String DEFAULT_PATH_TMPL = "{workdir_root}/Artifacts/{batch_id}/{test_id}/";
    private static final String DEFAULT_FILE_TMPL = "{step_index}_{step_tag}_{artifact_type}.{file_ext}";

    @Parameter(names = {"-d", "--destination"}, description = "Destination folder/template to save the results")
    protected String destination = String.format("%sfile:%s", DEFAULT_PATH_TMPL, DEFAULT_FILE_TMPL);

    public ResultsAPIExtract() {
    }

    public ResultsAPIExtract(String resUrl, String viewKey, String destination) {
        super(resUrl, viewKey);
        if (destination != null && StringUtils.isNotBlank(destination))
            this.destination = destination;
    }

    public void run() throws Exception {
        ResultUrl resultUrl = getUrl();
        ResultsAPIContext ctx = new ResultsAPIContext(resultUrl, viewKey);
        PathBuilder builder = null;
        if (PathBuilder.isTemplateMatches(destination))
            builder = new PathBuilder(destination);
        else  //we assume destination was set only for path without file
            builder = new PathBuilder(destination, DEFAULT_FILE_TMPL);
        builder = builder.recreate(getParams());
        if (resultUrl.getSessionId() != null) {
            //Just one test
            TestInfo testInfo = mapper.readValue(ctx.getTestApiUrl(), TestInfo.class);
            testInfo.setPathBuilder(builder);
            testInfo.setContext(ctx);
            runPerTest(testInfo);
        } else if (resultUrl.getBatchId() != null) {
            //Url contains batch
            BatchInfo bi = BatchInfo.get(ctx, builder);
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

    @Override
    protected HashMap<String, String> getParams() {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("user_root", new File(System.getProperty("user.dir")).getAbsolutePath());
        params.put("workdir_root", new File("").getAbsolutePath());
        params.put("artifacts", "artifacts");
        return params;
    }

    protected abstract void runPerTest(TestInfo testInfo) throws IOException;
}
