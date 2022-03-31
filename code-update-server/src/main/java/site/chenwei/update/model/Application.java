package site.chenwei.update.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/** 
* @author cw
* @date 2022年03月17日 15:02
*/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Application {
    private String id;

    private String name;

    private Date createTime;

    private Date updateTime;

    private String enableStatus;
}