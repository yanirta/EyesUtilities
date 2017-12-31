# EyesUtilities [ ![Download](https://api.bintray.com/packages/applitoolseyes/generic/EyesUtilities/images/download.svg)](https://bintray.com/applitoolseyes/generic/download_file?file_path=EyesUtilities.jar)
Eyes utilities is a Java CLI tool provides extended API features for Applitools Eyes.  
This tool based on the assumption that you already have tests with Applitools
and you want to gather more information about your test results via the API.

If you still don't have your Applitools account,
you can start by going to the[Applitools website](https://applitools.com).  
To quickly get on-board go to[the getting started tutorial](https://applitools.com/resources/tutorial).  
Another tool that will assist you achieve the goal of quickly running tests with
Applitools is the Web-Tester standalone executor, that can be found[here](https://github.com/yanirta/WebTester).

Note that for the most api functions there is a need in specialty enterprise api keys.
To get your keys please contact our support at[support@applitools.com](mailto:support@applitools.com).

The general syntax is derived from the fact that the EyesUtilities tool is built in Java.  
As a result every cli call should start with:
>Java -jar EyesUtilities.jar [command] [command specific parametes...]

To run in each one of the mode see the following sections.
## Supported Modes

### Generate steps animation
This command will generate a set of animated gifs for each failing step inside the provided test.
The animation will iterate between three states: (a) The expected from the baseline, (b) The actual and (c) The actual with purple diff marks.
After the execution, the results will be saved, the default location is: `./batchId/testId/`
<img src="https://user-images.githubusercontent.com/6667420/28462429-df68b3fc-6e23-11e7-89d7-4827acde2769.gif" width="550">

Syntax:
> java -jar EyesUtilities.jar anidiffs -k [EntKey] <[optional params]> [ResultUrl]
+ Required parameters:
    + `-k [EntKey]` - Your Enterprise api read key.
    + `[ResultUrl]` - Applitools test result url to be analyzed.
+ Optional parameters:
    + `-i [mSecs]` - Transition interval between the images in milliseconds. default: 1000
    + `-d [pathTmpl]` - Specify destination path template.
    default: 
        >{workdir_root}/Artifacts/{batch_id}/{test_id}/file:{step_index}_{step_tag}_{artifact_type}.{file_ext}
        + Available path template parameters: user_root, workdir_root, batch_id, test_id, test_name, batch_name, app_name, os, hostapp, viewport, branch_name, step_index, step_tag, artifact_type, file_ext

### Generate test playback
Generates one unified animated gif with all the actual steps of a test.
<img src="https://user-images.githubusercontent.com/6667420/34461889-b818022a-ee3f-11e7-88d4-153124790462.gif" width="550">
Syntax:
> java -jar EyesUtilities.jar playback -k [EntKey] <[optional params]> [ResultUrl]
+ Required parameters:
    + `-k [EntKey]` - Your Enterprise api read key.
    + `[ResultUrl]` - Applitools test result url to be analyzed.
+ Optional parameters:
    + `-i [mSecs]` - Transition interval between the images in milliseconds. default: 1000
    + `-d [pathTmpl]` - Specify destination path template.
    default: 
        >{workdir_root}/Artifacts/{batch_id}/{test_id}/file:{step_index}_{step_tag}_{artifact_type}.{file_ext}
        + Available path template parameters: user_root, workdir_root, batch_id, test_id, test_name, batch_name, app_name, os, hostapp, viewport, branch_name, step_index, step_tag, artifact_type, file_ext
    + `-m` - A Flag, Sets 'on' diff marks if a step is different from it's expected baseline.
    
### Download test diffs
Downloads the images of the failed steps with diff marks on them.

Syntax:
> java -jar EyesUtilities.jar diffs -k [EntKey] <[optional params]> [ResultUrl]
+ Required parameters:
    + `-k [EntKey]` - Your Enterprise api read key.
    + `[ResultUrl]` - Applitools test result url to be analyzed.
+ Optional parameters:
    + `-i [mSecs]` - Transition interval between the images in milliseconds. default: 1000
    + `-d [pathTmpl]` - Specify destination path template.
    default: 
        >{workdir_root}/Artifacts/{batch_id}/{test_id}/file:{step_index}_{step_tag}_{artifact_type}.{file_ext}
        + Available path template parameters: user_root, workdir_root, batch_id, test_id, test_name, batch_name, app_name, os, hostapp, viewport, branch_name, step_index, step_tag, artifact_type, file_ext

### Download test images
Downloads the baseline and the actual images of a test.

Syntax:
> java -jar EyesUtilities.jar images -k [EntKey] <[optional params]> [ResultUrl]

+ Required parameters:
    + `-k [EntKey]` - Your Enterprise api read key.
    + `[ResultUrl]` - Applitools test result url to be analyzed.
 + Optional parameters:   
    + `-a` - Flag to download only actuals
    + `-b` - Flag to download only baselines
    + `-d [pathTmpl]` - Specify destination path template.
    default: 
        >{workdir_root}/Artifacts/{batch_id}/{test_id}/file:{step_index}_{step_tag}_{artifact_type}.{file_ext}
        + Available path template parameters: user_root, workdir_root, batch_id, test_id, test_name, batch_name, app_name, os, hostapp, viewport, branch_name, step_index, step_tag, artifact_type, file_ext

### Copy branch
This command performs branch merging using copy with special flags.

Syntax:
> java -jar copybranch -k [BranchKey] -s [SourceBranch] -a [AppName] <[optional params]> 

+ Required parameters:
    + `-k [BranchKey]` - Your enterprise branch management api key.
    + `-s [SourceBranch]` - Source branch name
    + `-a [AppName]` - Application name in branch
+ Optional parameters:
    + `-t [TargetBranch]` - Target branch for merge
    + `-o` - Force overwrite in case of conflict
    + `-all` - Copy all including unchanged steps.
    + `-d` - Delete the source branch after successful copy.
    
### Print test details
This command prints the details of a given test

Syntax:
> java -jar EyesUtilities.jar details -k [EntKey] <[optional params]> [ResultUrl]
+ Required parameters:
    + `-k [EntKey]` - Your Enterprise api read key.
    + `[ResultUrl]` - Applitools test result url to be analyzed.
+ Optional parameters:
    + `-s` - Print steps details
    
### Generating batch Report (Beta)

This command will generate an offline report of a provided applitools results url,
based on a provided template.
The default name and the location of the template is `./report.teml`.

Syntax:
> java -jar EyesUtilities.jar report -k [EntKey] <[optional params]> [ResultUrl]

+ Required parameters:
    + `-k [EntKey]` - Your Enterprise api read key.
    + `[ResultUrl]` - Applitools test result url to be analyzed.
+ Optional parameters:
    + `-d [FolderPath]` - Set custom report output destination. Default: `'.'`
    + `-t [FilePath]` - Set report template file. Default `./report.teml`
    
#####Template syntax:
The engine lies beneath this report generation is based on [Velocity framework]() which widely used in MVC frameworks to implement web-apps.
The VTL(Velocity Template Language) syntax reference can be found[here](http://velocity.apache.org/engine/1.7/vtl-reference.html).   

Here is the partial list of parameters that are exposed for usage in template construction:
+ `$batch` - The object that contains all the tests and batch level data.
    + `$batch.name` - The name of the batch
    + `$batch.id` - The id of the batch
    + `$batch.startedAt` - The started date of the batch
    + `$batch.testsPassed` - The count of passed tests
    + `$batch.testsFailed` - The count of failed tests
    + `$batch.testsNew` - The count of new tests that caused baseline creation
    + `$batch.getStepsPassed()` - The count of __total__ passed __steps__
    + `$batch.getStepsFailed()` - The count of __total__ failed __steps__
    + `$batch.getStepsNew()` - The count of __total__ new __steps__
    + `$batch.getTotalBaselineSteps()` - The count of total steps in the baselines
    + `$batch.getPassedRate()` - 100% based tests in batch pass rate
    + `$batch.getFailedRate()` - 100% based tests in batch fail rate
    + `$batch.getNewRate()` - 100% based tests in batch new rate
+ `#foreach($test in $batch.tests)` - While `$batch.tests` is list of contained tests objects
    + `$test.scenarioName` - The test name
    + `$test.getUrl()` - The url to the test
    + `$test.TotalActualSteps()` - The count of the total steps that were captured in the test
    + `$test.PassedCount()` - The count of the passed steps in test
    + `$test.FailedCount()` - The count of the failed steps in test
    + `$test.NewCount()` - The count of the new steps in test
    + `$test.MissingCount()` - The count of the passed steps in test
    + `#foreach($step in $test.getFailedSteps())` - While `$test.getFailedSteps()` is the list of contained failed steps objects
        + `$step.getAnimatedThumbprints()` - Downloads step animated thumbprint and returns file's relative path.
        + `$step.getExpectedImage()` - Downloads step baseline (expected) image and returns file's relative path.
        + `$step.getActualImage()` - Downloads step actual image and returns file's relative path.
        + `$step.getDiff()` - Downloads step diff image and returns file's relative path.
    + `#foreach($step in $test.getSteps()` - While `$test.getSteps()` is the list of steps objects.
        + `$step.getExpectedImage()` - Downloads step baseline (expected) image and returns file's relative path.
        + `$step.getActualImage()` - Downloads step actual image and returns file's relative path.

        
A complete example of a template can be found in[./Report/report.templ](https://github.com/yanirta/EyesUtilities/blob/master/Report/report.templ)  
This example generates html report but the same idea can be applied on any textual format.

##Resources
+ [Applitools website](https://applitools.com)
+ [Web-Tester](https://github.com/yanirta/WebTester)
+ [Image-Tester](https://github.com/yanirta/ImageTester)
+ [Applitools support portal](http://support.applitools.com/)
