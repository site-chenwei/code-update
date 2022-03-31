package site.chenwei.update.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import site.chenwei.update.common.constant.CommonConstants;
import site.chenwei.update.common.exception.BusinessException;
import site.chenwei.update.common.exception.SerialException;
import site.chenwei.update.common.model.SerialNo;
import site.chenwei.update.common.serialNo.SerialNoGenerator;
import site.chenwei.update.mapper.SerialNoMapper;
import site.chenwei.update.service.SerialNoService;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author cw
 * @date 2022年03月16日 20:51
 */
@Service
public class SerialNoServiceImpl implements SerialNoService {
    private final Map<String, SerialNoGenerator> generatorMap = new HashMap<>();

    @Resource
    SerialNoMapper serialNoMapper;

    @Autowired
    public void initGenerators() {
        List<SerialNo> serialNos = serialNoMapper.selectAllSerialNos();
        serialNos.forEach(serialNo -> {
            generatorMap.put(serialNo.getName(), new SerialNoGenerator(serialNoMapper, serialNo));
        });
    }


    @Override
    public String generateSerialNo(String serialName) {
        SerialNoGenerator serialNoGenerator = generatorMap.get(serialName);
        if (serialNoGenerator == null) {
            throw new BusinessException(CommonConstants.Request.ERROR, "Serial不存在");
        }
        String serialStr;
        try {
            serialStr = serialNoGenerator.generate();
        } catch (SerialException e) {
            e.printStackTrace();
            throw new BusinessException(CommonConstants.Request.ERROR, "生成序列号失败");
        }
        return serialStr;
    }
}
