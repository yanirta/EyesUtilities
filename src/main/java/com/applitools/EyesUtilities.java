package com.applitools;

import com.applitools.Commands.*;
import com.beust.jcommander.JCommander;

public class EyesUtilities {
    //TODO add proxy
    Parse parser = new Parse();
    DownloadDiffs downloadDiffs = new DownloadDiffs();
    DownloadImages downloadImages = new DownloadImages();
    Details detailedReport = new Details(); //TODO remove
    Report report = new Report();
    CopyBranch copyBranch = new CopyBranch();
    AnimatedDiffs animatedDiffs = new AnimatedDiffs();

    public static void main(String[] args) {
        EyesUtilities main = new EyesUtilities();
        JCommander jc = new JCommander();
        jc.setProgramName("EyesUtilities");

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
        jc.addCommand("report", report);
        jc.addCommand("copybranch", copyBranch);
        jc.addCommand("anidiffs", animatedDiffs);
        jc.parse(args);

        String commandstr = jc.getParsedCommand();
        if (commandstr == null) {
            jc.usage();
            return;
        }

        Command command = (Command) jc.getCommands().get(jc.getParsedCommand()).getObjects().get(0);
        command.run();

        //TODO Migrate test
        //TODO analyze scrollable areas
        //TODO analyze dynamic areas
        //TODO deep search in many batches
    }
}
