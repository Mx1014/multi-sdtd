/**    
 * 文件名：CMTOWERRepository           
 * 版本信息：    
 * 日期：2017/11/28 18:05:13    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rzt.entity.CMTOWER;
/**      
 * 类名称：CMTOWERRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/11/28 18:05:13 
 * 修改人：张虎成    
 * 修改时间：2017/11/28 18:05:13    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CMTOWERRepository extends JpaRepository<CMTOWER,String> {
}
