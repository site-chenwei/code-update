package site.chenwei.update.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import site.chenwei.update.model.UpdateRecorder;


/** 
* @author cw
* @date 2022年03月18日 20:39
*/
public interface UpdateRecorderMapper {
    int deleteByPrimaryKey(String id);

    int insert(UpdateRecorder record);

    int insertOrUpdate(UpdateRecorder record);

    int insertOrUpdateSelective(UpdateRecorder record);

    int insertSelective(UpdateRecorder record);

    UpdateRecorder selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(UpdateRecorder record);

    int updateByPrimaryKey(UpdateRecorder record);

    int updateBatch(List<UpdateRecorder> list);

    int batchInsert(@Param("list") List<UpdateRecorder> list);
}