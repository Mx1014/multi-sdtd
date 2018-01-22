/**    
 * 文件名：WarningOffPostUserTimeRepository           
 * 版本信息：    
 * 日期：2017/12/27 03:58:05    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.OffPostUserTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 类名称：WarningOffPostUserTimeRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface OffPostUserTimeRepository extends JpaRepository<OffPostUserTime,String> {

 @Transactional
 @Modifying
 @Query(value = "update warning_off_post_user_time set end_time=?2 where fk_user_id=?1 and end_time is null and FK_TASK_ID=?3",nativeQuery = true)
 void updateOffUserEndTime(String userId, Date date,Long taskId);

 @Query(value="select * from warning_off_post_user_time where fk_user_id=?1 and end_time is null and FK_TASK_ID=?2",nativeQuery = true)
 List<OffPostUserTime> findByUserIdAndDateisNull(String userId,Long taskId);
}
