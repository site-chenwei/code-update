package site.chenwei.update.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cw
 * @date 2022年03月15日 13:24
 */
@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BusinessException extends RuntimeException {
    private String code;
    private String msg;
}
