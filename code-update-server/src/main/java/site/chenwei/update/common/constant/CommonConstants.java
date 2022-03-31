package site.chenwei.update.common.constant;

/**
 * @author cw
 * @date 2022年02月24日 17:38
 */
public class CommonConstants {
    public interface Request {
        String SUCCESS = "S000000";
        String SUCCESS_MESSAGE = "操作成功";
        String ERROR = "E000000";
        String ERROR_MESSAGE = "操作失败";
        String INTERNAL_ERROR = "E000001";
        String INTERNAL_ERROR_MESSAGE = "服务器异常，请稍后再试";
        String PARAM_ERROR = "E000002";
        String PARAM_ERROR_MESSAGE = "参数有误，请重试";
        String NO_DOWNLOAD_URL_PREFIX = "E000003";
        String NO_FILE_BASE_URL = "E000004";

    }

    public interface MediaType {
        String APPLICATION_JSON_UTF8 = "application/json;charset=UTF-8";
        String APPLICATION_JSON = "application/json";
        String TEXT_PLAIN = "text/plain";
        String TEXT_PLAIN_UTF8 = "text/plain; charset=utf-8";
    }
}
