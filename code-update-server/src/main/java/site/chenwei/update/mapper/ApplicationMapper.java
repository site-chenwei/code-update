package site.chenwei.update.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import site.chenwei.update.model.Application;


/** 
* @author cw
* @date 2022年03月17日 15:02
*/
public interface ApplicationMapper {
    int deleteByPrimaryKey(String id);

    int insert(Application record);

    int insertOrUpdate(Application record);

    int insertOrUpdateSelective(Application record);

    int insertSelective(Application record);

    Application selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Application record);

    int updateByPrimaryKey(Application record);

    int updateBatch(List<Application> list);

    int batchInsert(@Param("list") List<Application> list);
}