import com.applitools.EyesUtilities;
import org.junit.Test;

public class Branches {
    @Test
    public void mergeToDefault(){
        String cmdstr = "merge -k %s -s %s";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "eyesutilities_test");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }

    @Test
    public void mergeToNewBranch(){
        String cmdstr = "merge -k %s -s %s -t %s";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "eyesutilities_test_delete", "eyesutilities_test");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }

    @Test
    public void merge(){
        String cmdstr = "merge -k %s -s %s -t %s";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "eyesutilities_test_new", "eyesutilities_test");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }

    @Test
    public void mergeWithDelete(){
        String cmdstr = "merge -k %s -s %s -t %s -d";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "eyesutilities_test_new", "eyesutilities_test");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }
}
