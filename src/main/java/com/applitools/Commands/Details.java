package com.applitools.Commands;

//import com.applitools.obj.ExtendedTestResult;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Get detailed report of a test", hidden = true)
public class Details extends ResultsAPI {

    @Parameter(names = {"-s", "-stepResults"}, description = "Include steps results")
    private boolean stepResults = false;

    public void run() throws Exception {
//        ExtendedTestResult result = new ExtendedTestResult(getUrl(), viewKey);
//        System.out.format("Test name: \t\t%s \n", result.getScenarioName());
//        System.out.format("App name: \t\t%s \n", result.getAppName());
//        System.out.format("OS: \t\t\t%s \n", result.getHostOS());
//        System.out.format("Application: \t\t%s \n", result.getHostApp());
//        System.out.format("Size: \t\t\t%s \n", result.getViewPortSize());
//        System.out.format("Match level: \t\t%s \n", result.getMatchLevel());
//
//        System.out.format("Test result: \t\t%s \n", result.isNew() ? "New" : result.isPassed() ? "Matched" : "Failed");
//        BaselineInfo bi = BaselineInfo.get(getUrl(), viewKey, result);
//        System.out.format("Last updated by: \t%s \n", bi.getLastUpdatedBy() == null ? "Information unavailable" : bi.getLastUpdatedBy());
//        System.out.format("Last updated at: \t%s \n", bi.getLastUpdated() == null ? "Information unavailable" : bi.getLastUpdated());
//        if (stepResults) {
//            System.out.println("--Step results--");
//            Result[] stepsResults = result.getStepsResults();
//            for (int i = 0; i < stepsResults.length; ++i) {
//                System.out.format("[Step %s] - %s \n", i + 1, stepsResults[i].toString());
//            }
//        }
    }
}
