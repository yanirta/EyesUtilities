package com.applitools.obj;

import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//Path and file generator based on a template of a full path that can contain different variables
public class PathBuilder {
    private static final Pattern FILE_PATTERN_PATTERN = Pattern.compile("^(?<path>.*)\\/file:(?<file>.*\\.\\{file_ext\\})$");
    private static final String PARAM_TEMPLATE_REGEX = "\\{%s\\}";

    private String path_template;
    private String file_template;

    public PathBuilder(String fullpathTmpl) {
        Matcher matcher = FILE_PATTERN_PATTERN.matcher(fullpathTmpl);

        if (matcher.matches()) {
            path_template = matcher.group(1);
            file_template = matcher.group(2);
        } else {
            throw new RuntimeException("Path template doesn't match to the expected pattern");
        }
    }

    public PathBuilder(String pathtmpl, String filetmpl) {
        this.path_template = pathtmpl;
        this.file_template = filetmpl;
    }

    private PathBuilder() {
        //nothing here
    }

    public static boolean isTemplateMatches(String templ) {
        return FILE_PATTERN_PATTERN.matcher(templ).matches();
    }

    public PathBuilder recreate(Map<String, String> tokens) {
        String pathTempl = path_template;
        String fileTempl = file_template;
        for (Map.Entry<String, String> token : tokens.entrySet()) {
            if (token.getValue() == null) continue;
            String value = Matcher.quoteReplacement(token.getValue());
            pathTempl = pathTempl.replaceAll(String.format(PARAM_TEMPLATE_REGEX, token.getKey()), value);
            fileTempl = fileTempl.replaceAll(String.format(PARAM_TEMPLATE_REGEX, token.getKey()), value);
        }

        PathBuilder newpb = new PathBuilder();
        newpb.path_template = pathTempl;
        newpb.file_template = fileTempl;
        return newpb;
    }

    public File buildPath() {
        return new File(path_template);
    }

    public File buildFile() {
        if (StringUtils.isEmpty(file_template))
            throw new RuntimeException("file name is empty");
        File pwd = new File(System.getProperty("user.dir"));
        String path = Paths.get(path_template).normalize().toString();
        return new File(
                pwd.toURI().relativize(
                        new File(path, file_template).toURI()
                ).getPath()
        );
    }

    public synchronized void ensureTargetFolder() {
        File outFolder = buildPath();
        if (!outFolder.exists() && !outFolder.mkdirs())
            throw new RuntimeException(
                    String.format("Unable to create output folder for path: %s", outFolder.toString()));
    }
}
