package com.applitools.Commands;

import com.applitools.obj.BatchInfo;
import com.applitools.obj.Contexts.ResultsAPIContext;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

@Parameters(commandDescription = "Prepare report based on a template", hidden = true)

public class Report extends ResultsAPI {

    @Parameter(names = {"-t", "--template"}, description = "!!!!")
    private String templFileName = "report.templ";

    @Parameter(names = {"-d", "--destination"}, description = "!!!!")
    private String reportoutfile = "report.html";

    private File templFile = null;

    public void run() throws Exception {
        templFile = new File(templFileName);
        Velocity.setProperty("file.resource.loader.class", FileResourceLoader.class.getName());
        Velocity.setProperty("file.resource.loader.path", templFile.getParent().toString());
        Velocity.init();
        VelocityContext context = createContext();

        if (context == null) {
            System.out.printf("No information or unable to read it");
            return;
        }

        StringWriter writer = getReportStream(context);
        writeToFile(writer);
    }

    private void writeToFile(StringWriter writer) throws IOException {
        File report = new File(reportoutfile);
        FileOutputStream stream = new FileOutputStream(report);

        stream.write(writer.toString().getBytes());
        stream.flush();
        stream.close();
    }

    private StringWriter getReportStream(VelocityContext context) {
        StringWriter sw = new StringWriter();
        Template template = Velocity.getTemplate(templFile.getName());
        template.merge(context, sw);
        sw.flush();
        return sw;
    }

    private VelocityContext createContext() throws IOException {
        File artifacts = new File(
                new File(reportoutfile)
                        .getAbsoluteFile()
                        .getParentFile()
//                        .toURI()
//                        .relativize(new File(System.getProperty("user.dir")).toURI())
                        .getPath()
                , "/artifacts/");

        if (!artifacts.exists()) artifacts.mkdirs();
        ResultsAPIContext ctx = ResultsAPIContext.init(getUrl(), viewKey, artifacts);
        VelocityContext context = new VelocityContext();
        BatchInfo batchInfo = BatchInfo.get(ctx);
        if (batchInfo == null) return null;
        context.internalPut("batch", batchInfo);
        context.internalPut("server_url", getUrl().getServerAddress());
        return context;
    }
}
