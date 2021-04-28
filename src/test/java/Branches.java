import com.yanirta.EyesUtilities;
import org.junit.Test;

public class Branches {
    @Test
    public void mergeToDefault() {
        String cmdstr = "merge -k %s -as %s -s %s";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "https://eyes.applitools.com", "eyesutilities_new");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }

    @Test
    public void merge() {
        String cmdstr = "merge -k %s -as %s -s %s -t %s";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "https://eyes.applitools.com", "eyesutilities_new", "default");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }

    @Test
    public void mergeWithDelete() {
        String cmdstr = "merge -k %s -as %s -s %s -t %s -d";
        cmdstr = String.format(cmdstr, System.getenv("APPLITOOLS_MERGE_KEY"), "https://eyes.applitools.com", "eyesutilities_new", "default");
        String[] cmd = cmdstr.split(" ");
        EyesUtilities.main(cmd);
    }
}
