/**    
 * 文件名：KHTASKWPQRService           
 * 版本信息：    
 * 日期：2017/12/17 16:53:35    
 * Copyright 融智通科技(北京)股份有限公司 版权所有    
 */
package com.rzt.service;
import com.rzt.entity.KhTaskWpqr;
import com.rzt.repository.KhTaskWpqrRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;
import com.rzt.service.CurdService;

/**      
 * 类名称：KHTASKWPQRService    
 * 类描述：${table.comment}    
 * 创建人：张虎成   
 * 创建时间：2017/12/17 16:53:35 
 * 修改人：张虎成    
 * 修改时间：2017/12/17 16:53:35    
 * 修改备注：    
 * @version        
 */
@Service
@Transactional
public class KhTaskWpqrService extends CurdService<KhTaskWpqr,KhTaskWpqrRepository> {


}