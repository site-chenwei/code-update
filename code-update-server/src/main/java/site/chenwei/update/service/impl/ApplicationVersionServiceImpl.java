package site.chenwei.update.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import site.chenwei.update.common.bsdiff.BSDiff;
import site.chenwei.update.common.bsdiff.BSPatch;
import site.chenwei.update.common.constant.CommonConstants;
import site.chenwei.update.common.constant.SerialNoConstant;
import site.chenwei.update.common.constant.SystemConfigConstant;
import site.chenwei.update.common.exception.BusinessException;
import site.chenwei.update.common.util.MD5Util;
import site.chenwei.update.mapper.ApplicationVersionMapper;
import site.chenwei.update.model.ApplicationVersion;
import site.chenwei.update.model.SystemConfig;
import site.chenwei.update.model.UpdateRecorder;
import site.chenwei.update.model.vo.ApplicationVersionVo;
import site.chenwei.update.service.ApplicationVersionService;
import site.chenwei.update.service.SerialNoService;
import site.chenwei.update.service.SystemConfigService;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * @author cw
 * @date 2022年03月17日 15:06
 */
@Service
public class ApplicationVersionServiceImpl implements ApplicationVersionService {
    private static String FILE_BASE_URL = "";
    private static String DOWNLOAD_URL_PREFIX = "";
    private static final String VERSION_FILE_FOLDER = "version";
    private static final String VERSION_PATCH_FILE_NAME = "patch.zip";
    private static final String VERSION_PATCHED_FILE_NAME = "patched.zip";
    private static final String DOWNLOAD_URL_SEPARATOR = "/";
    @Resource
    SystemConfigService systemConfigService;
    @Resource
    SerialNoService serialNoService;
    @Resource
    ApplicationVersionMapper applicationVersionMapper;

    @Autowired
    public void init() {
        SystemConfig config = systemConfigService.getConfig(SystemConfig.builder().name(SystemConfigConstant.FILE_BASE_PATH).build());
        if (config != null) {
            FILE_BASE_URL = config.getValue();
        }
        config = systemConfigService.getConfig(SystemConfig.builder().name(SystemConfigConstant.DOWNLOAD_URL_PREFIX).build());
        if (config != null) {
            String value = config.getValue();
            if (StringUtils.endsWith(value, "/")) {
                DOWNLOAD_URL_PREFIX = value.substring(0, StringUtils.lastIndexOf(value, "/") - 1);
            } else {
                DOWNLOAD_URL_PREFIX = value;
            }
        }
    }

    @Override
    @Transactional
    public ApplicationVersion newVersion(MultipartFile file, ApplicationVersion applicationVersion) {
        if (file == null) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, "请上传更新ZIP包");
        }
        if (applicationVersion == null) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, "请指定更新版本信息");
        }
        if (StringUtils.isBlank(applicationVersion.getApplicationId())) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, "请指定更新应用信息");
        }
        if (StringUtils.isBlank(applicationVersion.getName())) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, "请指定更新版本号");
        }
        if (StringUtils.isBlank(FILE_BASE_URL)) {
            SystemConfig config = systemConfigService.getConfig(SystemConfig.builder().name(SystemConfigConstant.FILE_BASE_PATH).build());
            if (config == null || StringUtils.isBlank(config.getValue())) {
                throw new BusinessException(CommonConstants.Request.NO_FILE_BASE_URL, "未配置更新文件目录！");
            }
            FILE_BASE_URL = config.getValue();
        }
        String applicationVersionId = serialNoService.generateSerialNo(SerialNoConstant.APPLICATION_VERSION_SERIAL);
        applicationVersion.setId(applicationVersionId);
        String destPath = FILE_BASE_URL + File.separator + VERSION_FILE_FOLDER + File.separator + applicationVersion.getApplicationId() + File.separator + applicationVersionId + File.separator + file.getOriginalFilename();
        try {
            File destFile = new File(destPath);
            if (destFile.exists()) {
                destFile.delete();
            }
            destFile.mkdirs();
            file.transferTo(destFile);
            applicationVersion.setVersionFile(destPath);
            String sign = MD5Util.md5(new FileInputStream(destPath));
            applicationVersion.setSignature(sign);
            List<ApplicationVersion> applicationVersions = applicationVersionMapper.selectListByApplicationId(applicationVersion.getApplicationId());
            for (ApplicationVersion applicationVersionOld : applicationVersions) {
                String oldFilePath = applicationVersionOld.getVersionFile();
                File oldFile = new File(oldFilePath);
                applicationVersionOld.setCurrentVersion(false);
                if (!oldFile.exists()) {
                    continue;
                }
                String patchFilePath = oldFile.getParent() + File.separator + VERSION_PATCH_FILE_NAME;
                File patchFile = new File(patchFilePath);
                if (patchFile.exists()) {
                    patchFile.delete();
                }
                if (!BSDiff.diff(oldFilePath, destPath, patchFilePath)) {
                    continue;
                }
                String patchedFilePath = oldFile.getParent() + File.separator + VERSION_PATCHED_FILE_NAME;
                File patchedFile = new File(patchedFilePath);
                if (patchedFile.exists()) {
                    patchedFile.delete();
                }
                BSPatch.patch(oldFilePath, patchedFilePath, patchFilePath);
                if (!StringUtils.equals(MD5Util.md5(new FileInputStream(patchedFilePath)), MD5Util.md5(new FileInputStream(destPath)))) {
                    continue;
                }
                applicationVersionOld.setPatchFile(patchFilePath);
            }
            if (!applicationVersions.isEmpty()) {
                applicationVersionMapper.updateBatch(applicationVersions);
            }
            applicationVersion.setCurrentVersion(true);
            applicationVersionMapper.insertSelective(applicationVersion);
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(CommonConstants.Request.INTERNAL_ERROR, "上传新版本失败");
        }
        return applicationVersion;
    }

    @Override
    public ApplicationVersionVo checkUpdate(ApplicationVersion applicationVersion, UpdateRecorder updateRecorder) {
        if (applicationVersion == null || StringUtils.isBlank(applicationVersion.getApplicationId())) {
            throw new BusinessException(CommonConstants.Request.PARAM_ERROR, "请传入当前应用信息");
        }
        ApplicationVersion latestVersion = applicationVersionMapper.selectCurrentVersionByApplicationID(applicationVersion.getApplicationId());
        if (StringUtils.equals(latestVersion.getSignature(), applicationVersion.getSignature())) {
            return null;
        }
        ApplicationVersion currentVersion = applicationVersionMapper.selectByApplicationIdAndSignature(applicationVersion.getApplicationId(), applicationVersion.getSignature());
        ApplicationVersionVo applicationVersionVo = new ApplicationVersionVo(latestVersion);
        if (currentVersion != null) {
            applicationVersionVo.setDownloadUrl(currentVersion.getPatchFile());
            applicationVersionVo.setUpdateType(ApplicationVersionVo.UpdateType.INCREMENTAL_UPDATE);
        } else {
            applicationVersionVo.setDownloadUrl(latestVersion.getVersionFile());
            applicationVersionVo.setUpdateType(ApplicationVersionVo.UpdateType.FULL_UPDATE);
        }
        if (StringUtils.isBlank(DOWNLOAD_URL_PREFIX)) {
            SystemConfig config = systemConfigService.getConfig(SystemConfig.builder().name(SystemConfigConstant.DOWNLOAD_URL_PREFIX).build());
            if (config == null || StringUtils.isBlank(config.getValue())) {
                throw new BusinessException(CommonConstants.Request.NO_DOWNLOAD_URL_PREFIX, "未匹配下载URL，无法更新");
            }
            DOWNLOAD_URL_PREFIX = config.getValue();
            if (StringUtils.endsWith(DOWNLOAD_URL_PREFIX, DOWNLOAD_URL_SEPARATOR)) {
                DOWNLOAD_URL_PREFIX = DOWNLOAD_URL_PREFIX.substring(0, DOWNLOAD_URL_PREFIX.length() - 1);
            }
        }
        String downloadUrlSuffix = applicationVersionVo.getDownloadUrl().replace(FILE_BASE_URL, "");
        if (!StringUtils.startsWith(downloadUrlSuffix, DOWNLOAD_URL_SEPARATOR)) {
            downloadUrlSuffix = DOWNLOAD_URL_SEPARATOR + downloadUrlSuffix;
        }
        applicationVersionVo.setVersionFile("");
        applicationVersionVo.setDownloadUrl(DOWNLOAD_URL_PREFIX + downloadUrlSuffix);
        return applicationVersionVo;
    }
}
