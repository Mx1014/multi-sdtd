/**    
 * 文件名：RztsyscompanyService           
 * 版本信息：    
 * 日期：2017/12/08 16:40:23    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.repository.RztsyscompanyRepository;
import com.rzt.entity.Rztsyscompany;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import com.rzt.service.CurdService;

/**      
 * 类名称：RztsyscompanyService    
 * 类描述：InnoDB free: 536576 kB    
 * 创建人：张虎成   
 * 创建时间：2017/12/08 16:40:23 
 * 修改人：张虎成    
 * 修改时间：2017/12/08 16:40:23    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class RztsyscompanyService extends CurdService<Rztsyscompany,RztsyscompanyRepository> {


}