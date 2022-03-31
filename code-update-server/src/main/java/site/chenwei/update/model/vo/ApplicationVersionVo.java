package site.chenwei.update.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import site.chenwei.update.model.ApplicationVersion;

/**
 * @author cw
 * @date 2022年03月17日 18:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ApplicationVersionVo extends ApplicationVersion {
    public enum UpdateType {
        FULL_UPDATE,
        INCREMENTAL_UPDATE;
    }

    public ApplicationVersionVo(ApplicationVersion applicationVersion) {
        super(applicationVersion.getId(),
                applicationVersion.getApplicationId(),
                applicationVersion.getName(),
                applicationVersion.getCreateTime(),
                applicationVersion.getUpdateTime(),
                applicationVersion.getEnableStatus(),
                applicationVersion.getSignature(),
                applicationVersion.getVersionFile(),
                applicationVersion.getPatchFile(),
                applicationVersion.getCurrentVersion());
    }

    private String downloadUrl;
    /**
     * 更新类型 1 增量更新  0全量更新  默认为1
     */
    private UpdateType updateType = UpdateType.INCREMENTAL_UPDATE;

}
