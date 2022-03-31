package site.chenwei.update.mapper;

import org.apache.ibatis.annotations.Param;
import site.chenwei.update.model.ApplicationVersion;

import java.util.List;


/**
 * @author cw
 * @date 2022年03月17日 18:09
 */
public interface ApplicationVersionMapper {
    int deleteByPrimaryKey(String id);

    int insert(ApplicationVersion record);

    int insertOrUpdate(ApplicationVersion record);

    int insertOrUpdateSelective(ApplicationVersion record);

    int insertSelective(ApplicationVersion record);

    ApplicationVersion selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(ApplicationVersion record);

    int updateByPrimaryKey(ApplicationVersion record);

    int updateBatch(List<ApplicationVersion> list);

    int batchInsert(@Param("list") List<ApplicationVersion> list);

    ApplicationVersion selectByApplicationIdAndSignature(String applicationId, String signature);

    List<ApplicationVersion> selectListByApplicationId(String applicationId);

    ApplicationVersion selectCurrentVersionByApplicationID(String applicationId);
}
