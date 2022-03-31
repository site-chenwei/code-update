package site.chenwei.update.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * 更新记录
 *
 * @author cw
 * @date 2022年03月18日 20:39
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecorder {
    private String id;

    private String serialId;

    private String phoneOsType;

    private String phoneVersion;

    private String phoneType;

    private String versionName;

    private String applicationId;

    private String applicationVersionId;

    private Date createTime;

    /**
     * 0:检测更新
     * 1:已下载
     * 2:已完成更新
     */
    private String updateStatus;

    private Date checkTime;

    private Date downloadTime;

    private Date installTime;
}
