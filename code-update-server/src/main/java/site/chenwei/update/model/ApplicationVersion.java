package site.chenwei.update.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


/**
 * @author cw
 * @date 2022年03月17日 18:09
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationVersion {
    private String id;

    private String applicationId;

    private String name;

    @JsonIgnore
    private Date createTime;

    @JsonIgnore
    private Date updateTime;

    @JsonIgnore
    private String enableStatus;

    private String signature;

    @JsonIgnore
    private String versionFile;

    @JsonIgnore
    private String patchFile;

    @JsonIgnore
    private Boolean currentVersion;
}
