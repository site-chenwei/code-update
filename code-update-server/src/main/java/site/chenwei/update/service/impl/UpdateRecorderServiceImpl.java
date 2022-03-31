package site.chenwei.update.service.impl;

import org.springframework.stereotype.Service;
import site.chenwei.update.mapper.UpdateRecorderMapper;
import site.chenwei.update.model.UpdateRecorder;
import site.chenwei.update.service.UpdateRecorderService;

import javax.annotation.Resource;

/**
 * @author cw
 * @date 2022年03月18日 20:40
 */
@Service
public class UpdateRecorderServiceImpl implements UpdateRecorderService {
    @Resource
    private UpdateRecorderMapper updateRecorderMapper;

    @Override
    public UpdateRecorder addRecorder(UpdateRecorder updateRecorder) {
        return null;
    }

    @Override
    public UpdateRecorder syncStatus(UpdateRecorder updateRecorder) {
        return null;
    }
}
