package com.applitools;

import com.applitools.Commands.*;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "A 'swiss knife' made to get advanced functionality out of Applitools api")
public class EyesUtilities {
    //TODO add proxy
    private Parse parser = new Parse();
    private DownloadDiffs downloadDiffs = new DownloadDiffs();
    private DownloadImages downloadImages = new DownloadImages();
    private Details detailedReport = new Details(); //TODO remove
    private Report report = new Report();
    private CopyBranch copyBranch = new CopyBranch();
    private AnimatedDiffs animatedDiffs = new AnimatedDiffs();
    private Playback playback = new Playback();
    private Admin admin = new Admin();
    private Version version = new Version();

    public static void main(String[] args) {
        EyesUtilities main = new EyesUtilities();
        JCommander jc = new JCommander();
        jc.setProgramName("EyesUtilities");

        try {
            main.run(jc, args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            jc.usage(jc.getParsedCommand());
        }
    }

    private void run(JCommander jc, String[] args) throws Exception {
        if (args.length == 2 && args[0].compareTo("admin") == 0) {
            admin.printHelp(args[1]);
            return;
        }

        jc.addCommand("-v", version);
        jc.addCommand("parse", parser);
        jc.addCommand("diffs", downloadDiffs);
        jc.addCommand("images", downloadImages);
        jc.addCommand("details", detailedReport);
        jc.addCommand("report", report);
        jc.addCommand("copybranch", copyBranch);
        jc.addCommand("anidiffs", animatedDiffs);
        jc.addCommand("playback", playback);
        jc.addCommand("admin", admin);
        jc.parse(args);

        String commandstr = jc.getParsedCommand();
        if (commandstr == null) {
            jc.usage();
            return;
        }

        Command command = (Command) jc.getCommands().get(jc.getParsedCommand()).getObjects().get(0);

        command.run();

        //TODO Migrate test
        //TODO deep search in many batches
    }
}
