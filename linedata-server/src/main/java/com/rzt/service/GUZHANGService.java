/**    
 * 文件名：GUZHANGService           
 * 版本信息：    
 * 日期：2017/12/13 14:37:30    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.repository.GUZHANGRepository;
import com.rzt.entity.GUZHANG;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import com.rzt.service.CurdService;

/**      
 * 类名称：GUZHANGService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/13 14:37:30 
 * 修改人：张虎成    
 * 修改时间：2017/12/13 14:37:30    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class GUZHANGService extends CurdService<GUZHANG,GUZHANGRepository> {


}