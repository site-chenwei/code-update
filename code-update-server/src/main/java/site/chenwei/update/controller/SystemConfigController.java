package site.chenwei.update.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.chenwei.update.common.model.Result;
import site.chenwei.update.model.SystemConfig;
import site.chenwei.update.service.SystemConfigService;

import javax.annotation.Resource;

/**
 * @author cw
 * @date 2022年03月17日 16:20
 */
@RestController
@RequestMapping("system/config")
public class SystemConfigController {
    @Resource
    SystemConfigService systemConfigService;

    @RequestMapping("new")
    public Result<SystemConfig> newConfig(SystemConfig systemConfig) {
        return Result.success(systemConfigService.newConfig(systemConfig));
    }
}
