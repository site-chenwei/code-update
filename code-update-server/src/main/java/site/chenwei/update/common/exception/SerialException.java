package site.chenwei.update.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author cw
 * @date 2022年03月17日 09:41
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SerialException extends Exception {
    private String msg;

    public SerialException(String msg) {
        this.msg = msg;
    }
}
