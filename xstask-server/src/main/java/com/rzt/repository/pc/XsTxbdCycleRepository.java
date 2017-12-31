/**    
 * 文件名：XsTxbdCycleRepository           
 * 版本信息：    
 * 日期：2017/12/26 16:46:02    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository.pc;

import com.rzt.entity.pc.XsTxbdCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/**      
 * 类名称：XsTxbdCycleRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2017/12/26 16:46:02 
 * 修改人：张虎成    
 * 修改时间：2017/12/26 16:46:02    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface XsTxbdCycleRepository extends JpaRepository<XsTxbdCycle,String> {
}
