package com.applitools.Commands;

import com.beust.jcommander.Parameter;

public abstract class BaselineCommand extends CommandBase {
    @Parameter(names = {"-k", "-key"}, description = "Applitools API key", required = true)
    protected String apiKey;
    @Parameter(names = {"-as", "-server"}, description = "Applitools server url. [default eyes.applitools.com]")
    protected String server = "eyes.applitools.com";

    protected String getFormattedServerUrl() {
        String formattedServerUrl = server.replace("https://", "");
        return formattedServerUrl.replace("http://", "");
    }
}
