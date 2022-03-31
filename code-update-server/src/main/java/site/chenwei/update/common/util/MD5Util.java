package site.chenwei.update.common.util;

import org.springframework.util.DigestUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author cw
 * @date 2022年03月17日 14:55
 */
public class MD5Util {
    public static String md5(InputStream inputStream) {
        try {
            return DigestUtils.md5DigestAsHex(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
