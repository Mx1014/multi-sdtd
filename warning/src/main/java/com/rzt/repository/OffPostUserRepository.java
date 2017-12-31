/**    
 * 文件名：WarningOffPostUserRepository           
 * 版本信息：    
 * 日期：2017/12/27 03:58:05    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;

import com.rzt.entity.OffPostUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
/**      
 * 类名称：WarningOffPostUserRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 03:58:05 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 03:58:05    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface OffPostUserRepository extends JpaRepository<OffPostUser,String> {
 @Query(value = "select * from warning_off_post_user where user_id=?1 and task_id=?2",nativeQuery = true)
 OffPostUser findByUserIdAndTaskId(String userId, Long taskId);

}
