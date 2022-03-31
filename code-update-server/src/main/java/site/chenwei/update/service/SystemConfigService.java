package site.chenwei.update.service;

import site.chenwei.update.model.SystemConfig;

/**
 * @author cw
 * @date 2022年03月17日 16:22
 */
public interface SystemConfigService {
    SystemConfig newConfig(SystemConfig systemConfig);

    SystemConfig getConfig(SystemConfig systemConfig);
}
