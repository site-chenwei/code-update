package site.chenwei.update.service;

import org.springframework.web.multipart.MultipartFile;
import site.chenwei.update.model.ApplicationVersion;
import site.chenwei.update.model.UpdateRecorder;
import site.chenwei.update.model.vo.ApplicationVersionVo;

/**
 * @author cw
 * @date 2022年03月17日 15:06
 */
public interface ApplicationVersionService {
    ApplicationVersion newVersion(MultipartFile file, ApplicationVersion applicationVersion);

    ApplicationVersionVo checkUpdate(ApplicationVersion applicationVersion, UpdateRecorder updateRecorder);
}
