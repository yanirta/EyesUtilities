package com.applitools;

import com.applitools.Commands.*;
import com.applitools.utils.Utils;
import com.beust.jcommander.JCommander;

public class EyesUtilities {
    //TODO add proxy
    Parse parser = new Parse();
    DownloadDiffs downloadDiffs = new DownloadDiffs();
    DownloadImages downloadImages = new DownloadImages();
    DetailedReport detailedReport = new DetailedReport();
    CopyBranch copyBranch = new CopyBranch();
    AnimatedDiffs animatedDiffs = new AnimatedDiffs();

    public static void main(String[] args) {
        EyesUtilities main = new EyesUtilities();
        JCommander jc = new JCommander();
        try {
            main.run(jc, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());

            jc.usage();
            //e.printStackTrace();
        }
    }

    private void run(JCommander jc, String[] args) throws Exception {
        jc.addCommand("parse", parser);
        jc.addCommand("diffs", downloadDiffs);
        jc.addCommand("images", downloadImages);
        jc.addCommand("details", detailedReport);
        jc.addCommand("copybranch", copyBranch);
        jc.addCommand("anidiffs", animatedDiffs);
        jc.parse(args);

        String commandstr = jc.getParsedCommand();
        if (commandstr == null) {
            jc.usage();
            return;
        }

        Commands command = Utils.parseEnum(Commands.class, commandstr);
        switch (command) {
            case parse:
                parser.run();
                break;
            case diffs:
                downloadDiffs.run();
                break;
            case images:
                downloadImages.run();
                break;
            case details:
                detailedReport.run();
                break;
            case copybranch:
                copyBranch.run();
                break;
            case anidiffs:
                animatedDiffs.run();
                break;
        }

        //TODO Migrate test
        //TODO analyze scrollable areas
        //TODO analyze dynamic areas
        //TODO deep search in many batches
    }
}
