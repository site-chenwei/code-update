package site.chenwei.update.service;

import site.chenwei.update.model.UpdateRecorder;

/**
 * @author cw
 * @date 2022年03月18日 20:40
 */
public interface UpdateRecorderService {

    UpdateRecorder addRecorder(UpdateRecorder updateRecorder);

    UpdateRecorder syncStatus(UpdateRecorder updateRecorder);
}
