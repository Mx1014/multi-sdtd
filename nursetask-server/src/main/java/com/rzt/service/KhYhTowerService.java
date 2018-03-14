/**    
 * 文件名：KhYhTowerService           
 * 版本信息：    
 * 日期：2018/03/14 02:22:31    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.repository.KhYhTowerRepository;
import com.rzt.entity.KhYhTower;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import com.rzt.service.CurdService;

/**      
 * 类名称：KhYhTowerService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2018/03/14 02:22:31 
 * 修改人：张虎成    
 * 修改时间：2018/03/14 02:22:31    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class KhYhTowerService extends CurdService<KhYhTower,KhYhTowerRepository> {


}