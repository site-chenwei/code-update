package site.chenwei.update.common.bsdiff;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.net.URL;

/**
 * @author cw
 * @date 2022年03月17日 13:47
 */
public class BSDiff {
    private static String BS_DIFF_PATH;
    private static String BS_DIFF_NAME = "bsdiff";

    static {
        try {
            URL resource = BSDiff.class.getResource("/bsdiff/bsdiff");
            String currentPath = System.getProperty("user.dir");
            File file = new File(currentPath + File.separator + BS_DIFF_NAME);
            if (resource != null) {
                BS_DIFF_PATH = resource.getPath();
                if (file.exists()) {
                    file.delete();
                }
                FileUtil.copyFile(new File(BS_DIFF_PATH), file);
                BS_DIFF_PATH = currentPath + File.separator + BS_DIFF_NAME;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean diff(String oldPath, String newPath, String patchPath) {
        try {
            Runtime runtime = Runtime.getRuntime();
            Process exec = runtime.exec(BS_DIFF_PATH + " " + oldPath + " " + newPath + " " + patchPath);
            exec.waitFor();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        BSDiff.diff("/Users/cw/Documents/code/cw/code-update/version/2022031700000001/2022031700000002/dist.zip", "/Users/cw/Documents/code/cw/code-update/version/2022031700000001/2022031700000003/dist2.zip", "/Users/cw/Documents/code/cw/code-update/version/2022031700000001/2022031700000002/patch/patch.zip");
    }
}
