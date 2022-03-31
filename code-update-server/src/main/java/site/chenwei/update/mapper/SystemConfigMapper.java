package site.chenwei.update.mapper;

import org.apache.ibatis.annotations.Param;
import site.chenwei.update.model.SystemConfig;

import java.util.List;


/**
* @author cw
* @date 2022年03月17日 15:02
*/
public interface SystemConfigMapper {
    int deleteByPrimaryKey(String id);

    int insert(SystemConfig record);

    int insertOrUpdate(SystemConfig record);

    int insertOrUpdateSelective(SystemConfig record);

    int insertSelective(SystemConfig record);

    SystemConfig selectByPrimaryKey(String id);
    SystemConfig selectByName(String name);

    int updateByPrimaryKeySelective(SystemConfig record);

    int updateByPrimaryKey(SystemConfig record);

    int updateBatch(List<SystemConfig> list);

    int batchInsert(@Param("list") List<SystemConfig> list);
}
