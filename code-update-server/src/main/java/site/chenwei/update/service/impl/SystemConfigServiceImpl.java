package site.chenwei.update.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import site.chenwei.update.common.constant.CommonConstants;
import site.chenwei.update.common.constant.SerialNoConstant;
import site.chenwei.update.common.exception.BusinessException;
import site.chenwei.update.mapper.SystemConfigMapper;
import site.chenwei.update.model.SystemConfig;
import site.chenwei.update.service.SerialNoService;
import site.chenwei.update.service.SystemConfigService;

import javax.annotation.Resource;

/**
 * @author cw
 * @date 2022年03月17日 16:22
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    @Resource
    SystemConfigMapper systemConfigMapper;
    @Resource
    SerialNoService serialNoService;

    @Override
    public SystemConfig newConfig(SystemConfig systemConfig) {
        if (systemConfig == null || StringUtils.isAnyBlank(systemConfig.getName(), systemConfig.getValue())) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, CommonConstants.Request.PARAM_ERROR_MESSAGE);
        }
        SystemConfig systemConfigByName = systemConfigMapper.selectByName(systemConfig.getName());
        if (systemConfigByName != null) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, "参数名已存在！");
        }
        String serialNo = serialNoService.generateSerialNo(SerialNoConstant.SYSTEM_CONFIG);
        systemConfig.setId(serialNo);
        systemConfigMapper.insertSelective(systemConfig);
        return systemConfig;
    }

    @Override
    public SystemConfig getConfig(SystemConfig systemConfig) {
        if (systemConfig == null || StringUtils.isAllBlank(systemConfig.getName(), systemConfig.getId())) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, CommonConstants.Request.PARAM_ERROR_MESSAGE);
        }
        if (StringUtils.isNotBlank(systemConfig.getName())) {
            systemConfig = systemConfigMapper.selectByName(systemConfig.getName());
        }
        if (StringUtils.isNotBlank(systemConfig.getId())) {
            systemConfig = systemConfigMapper.selectByPrimaryKey(systemConfig.getId());
        }
        return systemConfig;
    }
}
