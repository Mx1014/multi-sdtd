/**    
 * 文件名：KHYHHISTORYRepository           
 * 版本信息：    
 * 日期：2017/12/27 17:23:43    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rzt.entity.KHYHHISTORY;
/**      
 * 类名称：KHYHHISTORYRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/27 17:23:43 
 * 修改人：张虎成    
 * 修改时间：2017/12/27 17:23:43    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface KHYHHISTORYRepository extends JpaRepository<KHYHHISTORY,String> {
}
