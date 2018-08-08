package com.applitools.Commands;

import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Print the version and more details about the tool")
public class Version implements Command {
    private String version = "1.0.3";

    @Override
    public void run() throws Exception {
        System.out.printf(
                "EyesUtilities for Applitools Eyes\n" +
                        "Version: %s \n" +
                        "Author: Yanir Taflev \n" +
                        "Created in 2016 \n" +
                        "Courtesy of the APAC/AMEA Customer Success team!\n"
                , version);
    }
}
