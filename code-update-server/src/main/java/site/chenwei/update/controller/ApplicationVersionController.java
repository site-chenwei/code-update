package site.chenwei.update.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import site.chenwei.update.common.model.Result;
import site.chenwei.update.model.ApplicationVersion;
import site.chenwei.update.model.UpdateRecorder;
import site.chenwei.update.service.ApplicationVersionService;
import site.chenwei.update.service.UpdateRecorderService;

import javax.annotation.Resource;

/**
 * @author cw
 * @date 2022年03月17日 15:03
 */
@RestController()
@RequestMapping("application/version")
public class ApplicationVersionController {
    @Resource
    ApplicationVersionService applicationVersionService;
    @Resource
    UpdateRecorderService updateRecorderService;

    @RequestMapping("new")
    public Result<ApplicationVersion> newVersion(@RequestPart(name = "file") MultipartFile file, ApplicationVersion applicationVersion) {
        return Result.success(applicationVersionService.newVersion(file, applicationVersion));
    }

    @RequestMapping("check")
    public Result<ApplicationVersion> checkUpdate(ApplicationVersion applicationVersion, UpdateRecorder updateRecorder) {
        return Result.success(applicationVersionService.checkUpdate(applicationVersion, updateRecorder));
    }

    @RequestMapping("status/sync")
    public Result<UpdateRecorder> syncStatus(UpdateRecorder updateRecorder) {
        return Result.success(updateRecorderService.syncStatus(updateRecorder));
    }


}
