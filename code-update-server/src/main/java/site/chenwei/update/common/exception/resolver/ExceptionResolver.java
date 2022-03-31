package site.chenwei.update.common.exception.resolver;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.chenwei.update.common.constant.CommonConstants;
import site.chenwei.update.common.exception.BusinessException;
import site.chenwei.update.common.model.Result;

/**
 * @author cw
 * @date 2022年03月17日 09:16
 */
@RestControllerAdvice
public class ExceptionResolver {
    @ExceptionHandler(value = Exception.class)
    public Result resolve(Exception e) {
        if (e instanceof BusinessException) {
            BusinessException businessException = (BusinessException) e;
            return Result.error(businessException.getCode(), businessException.getMessage());
        }
        return Result.error(CommonConstants.Request.INTERNAL_ERROR, CommonConstants.Request.INTERNAL_ERROR_MESSAGE);
    }
}
