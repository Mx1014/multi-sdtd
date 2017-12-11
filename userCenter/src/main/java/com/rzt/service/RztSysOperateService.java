/**    
 * 文件名：RztSysOperateService           
 * 版本信息：    
 * 日期：2017/10/12 10:25:31    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;

import com.rzt.service.CurdService;
import com.rzt.entity.RztSysOperate;
import com.rzt.repository.RztSysOperateRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**      
 * 类名称：RztSysOperateService    
 * 类描述：InnoDB free: 537600 kB    
 * 创建人：张虎成   
 * 创建时间：2017/10/12 10:25:31 
 * 修改人：张虎成    
 * 修改时间：2017/10/12 10:25:31    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class RztSysOperateService extends CurdService<RztSysOperate,RztSysOperateRepository> {


}