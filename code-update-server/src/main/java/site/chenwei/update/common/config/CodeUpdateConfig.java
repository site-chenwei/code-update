package site.chenwei.update.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author cw
 * @date 2022年03月17日 09:50
 */
@Data
@Component
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "code.update")
public class CodeUpdateConfig {

    private String qiNiuSecretKey;

    private String qiNiuAccessKey;


}
