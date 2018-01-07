/**    
 * 文件名：KHYHHISTORYRepository           
 * 版本信息：    
 * 日期：2017/11/30 18:31:34    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import com.rzt.entity.KhYhHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * 类名称：KHYHHISTORYRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/30 18:31:34 
 * 修改人：张虎成    
 * 修改时间：2017/11/30 18:31:34    
 * 修改备注：    
 * @version        
 */
@Repository
public interface KhYhHistoryRepository extends JpaRepository<KhYhHistory,String> {

    @Modifying
    @Query(value = "UPDATE PICTURE_YH SET YH_ID = ?2,TASK_ID=?3,YH_ORIGIN = 0 WHERE ID =?1",nativeQuery = true)
    void updateYhPicture(long id, Long yhId, long xstaskId);
    @Modifying
    @Query(value = "UPDATE PICTURE_YH SET YH_ID = ?2,YH_ORIGIN = 0 WHERE ID =?1",nativeQuery = true)
    void updatePicture(long id, Long yhId);

}
