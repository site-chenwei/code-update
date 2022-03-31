package site.chenwei.update.mapper;

import org.apache.ibatis.annotations.Param;
import site.chenwei.update.common.model.SerialNo;

import java.util.List;


/**
 * @author cw
 * @date 2022年03月16日 20:50
 */
public interface SerialNoMapper {
    int deleteByPrimaryKey(String id);

    int insert(SerialNo record);

    int insertOrUpdate(SerialNo record);

    int insertOrUpdateSelective(SerialNo record);

    int insertSelective(SerialNo record);

    SerialNo selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(SerialNo record);

    int updateByPrimaryKey(SerialNo record);

    int updateBatch(List<SerialNo> list);

    int batchInsert(@Param("list") List<SerialNo> list);

    List<SerialNo> selectAllSerialNos();
}
