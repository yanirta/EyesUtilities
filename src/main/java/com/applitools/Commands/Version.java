package com.applitools.Commands;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Print the version and more details about the tool")
public class Version implements Command {
    private String version = "1.0.8";

    public void run() throws Exception {
        System.out.printf(
                "EyesUtilities for Applitools Eyes\n" +
                        "Version: %s \n" +
                        "Author: Yanir Taflev \n" +
                        "2019 \n"
                , version);
    }
}
