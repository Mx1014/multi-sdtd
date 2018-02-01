/**    
 * 文件名：CmTowerUpdateRecordService           
 * 版本信息：    
 * 日期：2018/01/31 10:53:52    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.repository.CmTowerUpdateRecordRepository;
import com.rzt.entity.CmTowerUpdateRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import com.rzt.service.CurdService;

/**      
 * 类名称：CmTowerUpdateRecordService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2018/01/31 10:53:52 
 * 修改人：张虎成    
 * 修改时间：2018/01/31 10:53:52    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class CmTowerUpdateRecordService extends CurdService<CmTowerUpdateRecord,CmTowerUpdateRecordRepository> {


}