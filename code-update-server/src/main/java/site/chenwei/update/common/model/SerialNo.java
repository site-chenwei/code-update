package site.chenwei.update.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author cw
 * @date 2022年03月16日 20:50
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SerialNo {
    private String id;

    private String name;

    private Long value;

    private Date createTime;

    private Date updateTime;

    /**
     * 序列格式：yyyyMMdd########
     */
    private String format;
}
