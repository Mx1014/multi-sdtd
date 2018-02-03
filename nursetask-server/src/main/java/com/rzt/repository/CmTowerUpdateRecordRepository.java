/**    
 * 文件名：CmTowerUpdateRecordRepository           
 * 版本信息：    
 * 日期：2018/01/31 10:53:52    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.rzt.entity.CmTowerUpdateRecord;
/**      
 * 类名称：CmTowerUpdateRecordRepository    
 * 类描述：    
 * 创建人：张虎成   
 * 创建时间：2018/01/31 10:53:52 
 * 修改人：张虎成    
 * 修改时间：2018/01/31 10:53:52    
 * 修改备注：    
 * @version        
 */
 @Repository
public interface CmTowerUpdateRecordRepository extends JpaRepository<CmTowerUpdateRecord,String> {
}
